import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Objects;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Hilos implements Runnable{

    Socket s;
    ServerSocket ss;

    Logger logger = Logger.getLogger("MyLog");

    public Hilos(Socket s, ServerSocket ss) throws IOException {
        this.s = s;
        this.ss = ss;
    }

    @Override
    public void run() {
        try {

            Logger logger = Logger.getLogger("MyLog");

            FileHandler fh;

            fh = new FileHandler("./log_actividad_server.log", true);

            logger.setUseParentHandlers(false);
            SimpleFormatter formato = new SimpleFormatter();
            fh. setFormatter(formato);

            logger.setLevel(Level.ALL);
            logger.addHandler(fh);

            ObjectInputStream recibir_objeto = new ObjectInputStream(s.getInputStream());
            DataInputStream recibir_dato = new DataInputStream(s.getInputStream());
            ObjectOutputStream enviar_objeto = new ObjectOutputStream(s.getOutputStream());
            DataOutputStream enviar_dato = new DataOutputStream(s.getOutputStream());


            servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Función que recibe y comprueba si va a iniciar sesión o registrar un nuevo usuario
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public void servidor(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        System.out.println("Dato");
        int i = recibir_dato.readInt();
        System.out.println("Dato2");

        if (i == 1){
            Comprobar_inicio_sesion(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (i == 2) {
            Registro_servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                /*
                Registro_servidor();
                servidor();*/
        }

    }

    /**
     * Registra a un nuevo usuario
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public void Registro_servidor(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        Usuarios usuario = null;
        try {
            usuario = (Usuarios) recibir_objeto.readObject();
            Cuentas_bancarias cuenta = (Cuentas_bancarias) recibir_objeto.readObject();

            Usuarios usuarios = new Usuarios();
            Cuentas_bancarias cuentas = new Cuentas_bancarias();

            usuarios.escribir_usuario(usuario);

            cuentas.escribir_cuentas_bancarias(cuenta);

            System.out.println("Usuario creado");

            logger.log(Level.INFO,"\tUsuario creado. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);

        } catch (IOException e) {
            logger.log(Level.WARNING,"\tError al crear usuario. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING,"\tError al crear usuario. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);
        }



        servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

    }

    /**
     * Comprueba que los datos recibidos existen en el fichero .dat de los usuarios. En caso de que el usuario exista, pregunta si quiere leer el contrato, y después si quiere aceptarlo en caso de aceptar.
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public void Comprobar_inicio_sesion(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        Usuarios usuario = (Usuarios) recibir_objeto.readObject();
        File f = new File("Usuarios.dat");
        if(!f.isFile()) {
            enviar_dato.writeUTF("f");
            System.out.println("Error. No hay usuarios");
            logger.log(Level.WARNING,"\tNo hay usuarios");
            servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else {

            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream(f));

            int es = 0;
            System.out.println("Ha entrado en Inicio de sesión");
            Usuarios usuarios = (Usuarios) mostrar.readObject();

            try {
                while (usuarios != null) {
                    if (Objects.equals(usuarios.usuario, usuario.usuario) && Objects.equals(usuarios.contrasena, usuario.contrasena)) {
                        enviar_dato.writeUTF("s");
                        es += 1;
                        System.out.println("Concuerda");
                        String ds = recibir_dato.readUTF();
                        if (ds.equals("s")) {
                            String contrato = "Normas del banco.";

                            enviar_dato.writeUTF(contrato);

                            String respuesta = recibir_dato.readUTF();

                            if (respuesta.equals("s")) {
                                Firmado_digital(usuario, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                            } else {
                                System.out.println("Vuelves al menú");
                                //  enviar_dato.writeUTF("n");
                                logger.log(Level.WARNING, "\tEl usuario ha rechazado el contrato. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);
                                mostrar.close();
                                servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                            }
                            es++;
                            break;
                        } else {
                            System.out.println("Vuelves al menú");
                            // enviar_dato.writeUTF("n");
                            logger.log(Level.WARNING, "\tEl usuario ha rechazado el contrato. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);
                            mostrar.close();
                            servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

                        }
                    } else {
                        usuarios = (Usuarios) mostrar.readObject();
                    }

                }
                if (es == 0) {
                    System.out.println("El usuario y/o contraseña no existe. Vuelves al menú");
                    enviar_dato.writeUTF("n");
                    logger.log(Level.WARNING, "\tEl usuario insertado no existe. Nombre: " + usuario.nombre + "  Usuario: " + usuario.usuario);
                    mostrar.close();
                    servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                }
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("Ha ocurrido un error");
                logger.log(Level.WARNING, "\tError al iniciar error. Usuario: " + usuario.usuario);
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
                logger.log(Level.WARNING, "\tError al iniciar error. Usuario: " + usuario.usuario);
            }

            //enviar_dato.writeUTF("d");
            mostrar.close();
            servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }

    }

    public void Comprobar_normas_banco() throws IOException {

        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());
        DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());





    }

    /**
     * Firma el documento y devuelve si se ha firmado correctamente o no.
     * @param usuarios
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     */
    public void Firmado_digital(Usuarios usuarios, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");

            KeyPair par = keyGen.generateKeyPair();
            PrivateKey clavepriv = par.getPrivate();
            PublicKey clavepub = par.getPublic();

            Signature dsa = Signature.getInstance("SHA256withDSA");
            dsa.initSign(clavepriv);
            String mensaje = "Contrato aceptado";
            dsa.update(mensaje.getBytes());
            byte[] firma = dsa.sign();

            Signature verificadsa = Signature.getInstance("SHA256withDSA");
            verificadsa.initVerify(clavepub);
            verificadsa.update(mensaje.getBytes());
            boolean check = verificadsa.verify(firma);
            if (check) {
                System.out.println("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                enviar_dato.writeUTF("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                logger.log(Level.INFO,"\tEl usuario ha iniciado sesión. Usuario: " + usuarios.usuario);
                Banca_online(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
            } else {
                System.out.println("FiRMA NO VERIFICADA");
                enviar_dato.writeUTF("FiRMA NO VERIFICADA");
                logger.log(Level.WARNING,"\tError al iniciar sesión en el firmado digital. Usuario: " + usuarios.usuario);
                servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Recibe el dato del usuario para dirigirte o a ver el saldo de una cuenta o hacer una transferencia.
     * @param usuarios
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ClassNotFoundException
     */
    public void Banca_online(Usuarios usuarios, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {

        System.out.println("Banca online dentro");
        int i = recibir_dato.readInt();
        System.out.println("Dato2");

        if (i == 1){
            Mostrar_cuenta_usuario(usuarios, i, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

        } else if (i == 2) {
            Mostrar_cuenta_usuario(usuarios, i, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
            //Mostrar_cuentas();
            Banca_online(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }
    }

  /*  public void Mostrar_cuentas() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        try {
            while (usuario1 != null) {
                    System.out.println("Cuenta bancaria: " + usuario1.usuario);
                    outData.writeUTF(usuario1.cuenta_bancaria);
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
        mostrar.close();


    }*/

    /**
     * Busca en el fichero cuentas para mostrar el número de cuenta del usuario logeado y te va dependiendo si es para mostrar el saldo o hacer una transferencia.
     * @param usuarios
     * @param i
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public void Mostrar_cuenta_usuario(Usuarios usuarios, int i, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //  DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        //  DataInputStream inData = new DataInputStream(s.getInputStream());
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        try {
            while (usuario1 != null) {
                if (usuario1.usuario.usuario.equals(usuarios.usuario)){
                    System.out.println("Cuenta bancaria: " + usuario1.usuario);
                    enviar_dato.writeUTF(usuario1.cuenta_bancaria);
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                } else {
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                }
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
            logger.log(Level.WARNING,"\tError al mostrar usuarios. Usuario: " + usuarios.usuario);

        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
            logger.log(Level.WARNING,"\tError al mostrar usuarios. Usuario: " + usuarios.usuario);
        }
        mostrar.close();


        if (i == 1){
            Cuenta_Mostrar(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (i == 2) {
            Transferir_dinero(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }

    }

    /**
     * Recibe tanto el dinero y la cuenta desde el cliente, llama a la función de doble certificado. Recibe o 1 o 2 desde el cliente para saber si el número de cuenta recibida por el cliente existe o no.
     * Si existe, resta el dinero de la cuenta del usuario y añade al seleccionado por el usuario. En caso de que no exista, tan solo resta el dinero a la cuenta del usuario.
     * @param usuario
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws ClassNotFoundException
     */
    public void Transferir_dinero(Usuarios usuario, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        // DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        // DataInputStream inData = new DataInputStream(s.getInputStream());
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(s.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(s.getInputStream());

        String cuenta;
        int dinero;

        dinero = recibir_dato.readInt();

        int x = recibir_dato.readInt();

        cuenta = recibir_dato.readUTF();

        doble_certificado(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

        //dinero = recibir_dato.readInt();

        int d = recibir_dato.readInt();
        if (d == 1){
            System.out.println("Transacción Completada");
            // TODO: Continuar
            Cuentas_bancarias cuentas = new Cuentas_bancarias();
            // Cuenta del usuario al que enviamos el dinero
            Cuentas_bancarias cuentaa = cuentas.Comprobar_cuenta(cuenta);
            // Cuenta del usuario del que quitamos el dinero
            Cuentas_bancarias cuen = cuentas.Comprobar_cuenta2(usuario);
            if (cuentaa == null){
                int di = cuen.getDinero();
                int zc = di - dinero;
                cuen.setDinero(zc);
                cuentas.Modificar_cuentas_bancarias(cuen);
                logger.log(Level.INFO,"\tTransferencia completada. Usuario: " + usuario.usuario);
            } else {
                int din = cuentaa.getDinero();
                int zc = din + dinero;
                cuentaa.setDinero(zc);
                cuentas.Modificar_cuentas_bancarias(cuentaa);
                int di = cuen.getDinero();
                int zcs = di - dinero;
                cuen.setDinero(zcs);
                cuentas.Modificar_cuentas_bancarias(cuen);
                logger.log(Level.INFO,"\tTransferencia completada. Usuario: " + usuario.usuario);
            }
        } else if (d == 2){
            System.out.println("No se corresponde. Vuelta al menú");
            Banca_online(usuario ,enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }


    }

    /**
     * Recibe la llave del usuario para encriptar el número aleatorio y enviarlo al cliente.
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     */
    public void doble_certificado(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException {
        // DataInputStream inData = new DataInputStream(s.getInputStream());
        // DataOutputStream enviar_dato = new DataOutputStream(s.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(s.getInputStream());
        //  ObjectOutputStream enviar_objeto = new ObjectOutputStream(s.getOutputStream());
        Random random = new Random();

        int rand = random.nextInt(0, 100);
        String num = String.valueOf(rand);

        //enviar_dato.writeInt(rand);


        Cipher desCipher;
        byte[] mensajeEnviadoCifrado;

        try
        {

            //recogemos del flujo la clave simetrica
            SecretKey key = (SecretKey) recibir_objeto.readObject();
            System.out.println("le clave es : " + key);
            System.out.println("Configurando Cipher para encriptar");
            desCipher = Cipher.getInstance("DES");

            desCipher.init(Cipher.ENCRYPT_MODE, key);
            System.out.print("Numero para desencriptar\n");

            mensajeEnviadoCifrado = desCipher.doFinal(num.getBytes());

            enviar_objeto.writeObject(mensajeEnviadoCifrado);


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (NoSuchPaddingException e) {
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (IllegalBlockSizeException e) {
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (BadPaddingException e) {
            logger.log(Level.SEVERE,"\tError en doble certificado");
        } catch (InvalidKeyException e) {
            logger.log(Level.SEVERE,"\tError en doble certificado");
        }


    }

    /**
     * Envia al usuario el saldo de la cuenta seleccionada
     * @param usuarios
     * @param enviar_objeto
     * @param recibir_objeto
     * @param enviar_dato
     * @param recibir_dato
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws NoSuchAlgorithmException
     */
    public void Cuenta_Mostrar(Usuarios usuarios, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        // DataInputStream inData = new DataInputStream(s.getInputStream());
        // DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));

        String mensaje = Cifrado_simentrico(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();
        int i = 0;
        try {
            while (usuario1 != null) {
                if (mensaje.equals(usuario1.cuenta_bancaria)){
                    System.out.println("Saldo de la cuenta:" + usuario1.dinero);
                    enviar_dato.writeInt(usuario1.dinero);
                    logger.log(Level.INFO,"\tVer saldo completada. Usuario: " + usuarios.usuario);
                    i++;
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                } else {
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                }
            }
            if (i == 0){
                System.out.println("La cuenta bancaria no existe");
                enviar_dato.writeInt(-1);
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
            logger.log(Level.WARNING,"\tError al mostrar cuenta. Usuario: " + usuarios.usuario);
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
            logger.log(Level.WARNING,"\tError al mostrar cuenta. Usuario: " + usuarios.usuario);
        }

        mostrar.close();

        Banca_online(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

    }

    public String Cifrado_simentrico(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException {
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(s.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(s.getInputStream());

        String mensajeRecibidoDescifrado = "";
        try {

            byte[] mensajeRecibido = null;

            KeyGenerator keygen = null;
            Cipher desCipher = null;


            System.out.println("Obteniendo generador de claves con cifrado DES");
            try {
                keygen = KeyGenerator.getInstance("DES");
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Generando clave");
            SecretKey key = keygen.generateKey();


            System.out.println("La clave es: " + key);


            System.out.println("Obteniendo objeto Cipher con cifraddo DES");
            try {
                desCipher = Cipher.getInstance("DES");
            } catch (NoSuchAlgorithmException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("Configurando Cipher para desencriptar");
            try {
                desCipher.init(Cipher.DECRYPT_MODE, key);
            } catch (InvalidKeyException e) {
                // TODO Auto-generated catch block
                logger.log(Level.WARNING,"\tError al cifrado simétrico");
            }

            // Enviamos la clave

            enviar_objeto.writeObject(key);


            mensajeRecibido = (byte[]) recibir_objeto.readObject();

            mensajeRecibidoDescifrado = new String(desCipher.doFinal(mensajeRecibido));
            System.out.println("El texto enviado por el cliente y descifrado por el servidor es : " + new String(mensajeRecibidoDescifrado));





        } catch (IOException ex) {
            // Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException e) {
            logger.log(Level.WARNING,"\tError al cifrado simétrico");
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            logger.log(Level.WARNING,"\tError al cifrado simétrico");
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            logger.log(Level.WARNING,"\tError al cifrado simétrico");
            throw new RuntimeException(e);
        }
        return new String(mensajeRecibidoDescifrado);
    }
}
