import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Objects;
import java.util.Random;

public class Hilos implements Runnable{

    Socket s;
    ServerSocket ss;

    public Hilos(Socket s, ServerSocket ss) throws IOException {
        this.s = s;
        this.ss = ss;
    }

    @Override
    public void run() {
        try {
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

    public void Registro_servidor(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {

        Usuarios usuario = (Usuarios) recibir_objeto.readObject();
        Cuentas_bancarias cuenta = (Cuentas_bancarias) recibir_objeto.readObject();

        Usuarios usuarios = new Usuarios();
        Cuentas_bancarias cuentas = new Cuentas_bancarias();

        usuarios.escribir_usuario(usuario);

        cuentas.escribir_cuentas_bancarias(cuenta);

        System.out.println("Usuario creado");

        servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

    }

    public void Comprobar_inicio_sesion(ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Usuarios.dat"));

        int es = 0;
        System.out.println("Ha entrado en Inicio de sesión");
        Usuarios usuario = (Usuarios) recibir_objeto.readObject();
        Usuarios usuarios = (Usuarios) mostrar.readObject();

        try {
            while (usuarios != null){
                if (Objects.equals(usuarios.usuario, usuario.usuario) && Objects.equals(usuarios.contrasena, usuario.contrasena)) {
                    String a = "s";
                    enviar_dato.writeUTF("s");
                    System.out.println("Concuerda");
                    String ds = recibir_dato.readUTF();

                    String contrato = "Normas del banco.";

                    enviar_dato.writeUTF(contrato);

                    String respuesta = recibir_dato.readUTF();

                    if (respuesta.equals("s")) {
                        Firmado_digital(usuario, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                    }
                    es++;
                    break;
                }
                usuarios = (Usuarios) mostrar.readObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }

        enviar_dato.writeUTF("d");
        servidor(enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);


    }

    public void Comprobar_normas_banco() throws IOException {

        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());
        DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());





    }

    public void Firmado_digital(Usuarios usuarios, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException {

        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
//SE CREA EL PAR DE CLAVES PRIVADA Y PÚBLICA
            KeyPair par = keyGen.generateKeyPair();
            PrivateKey clavepriv = par.getPrivate();
            PublicKey clavepub = par.getPublic();
//FIRMA CON CLAVE PRIVADA EL MENSAJE
//AL OBJETO Signature SE LE SUMINISTRAN LOS DATOS A FIRMAR
            Signature dsa = Signature.getInstance("SHA256withDSA");
            dsa.initSign(clavepriv);
            String mensaje = "Contrato aceptado";
            dsa.update(mensaje.getBytes());
            byte[] firma = dsa.sign(); //MENSAJE FIRMADO
//EL RECEPTOR DEL MENSAJE
//VERIFICA CON LA CLAVE PUIBLICA EL MENSAJE FIRMADO
//AL OBJETO signature sE LE suministralos datos a verificar
            Signature verificadsa = Signature.getInstance("SHA256withDSA");
            verificadsa.initVerify(clavepub);
            verificadsa.update(mensaje.getBytes());
            boolean check = verificadsa.verify(firma);
            if (check) {
                System.out.println("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                enviar_dato.writeUTF("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                Banca_online(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
            } else {
                System.out.println("FiRMA NO VERIFICADA");
                enviar_dato.writeUTF("FiRMA NO VERIFICADA");
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
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
        mostrar.close();


        if (i == 1){
            Cuenta_Mostrar(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (i == 2) {
            Transferir_dinero(usuarios, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }

    }

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
            Cuentas_bancarias cuentaa = cuentas.Comprobar_cuenta(cuenta);
            if (cuentaa == null){
                Cuentas_bancarias cuen = cuentas.Comprobar_cuenta2(usuario);
                int di = cuen.getDinero();
                int zc = di - dinero;
                cuen.setDinero(zc);
                cuentas.Modificar_cuentas_bancarias(cuen);
            } else {
                cuentaa.setDinero(dinero);
                Cuentas_bancarias cuen = cuentas.Comprobar_cuenta2(usuario);
                int di = cuen.getDinero();
                int zc = di - dinero;
                cuen.setDinero(zc);
                cuentas.Modificar_cuentas_bancarias(cuen);
                cuentas.Modificar_cuentas_bancarias(cuentaa);
            }
        } else if (d == 2){
            System.out.println("No se corresponde. Vuelta al menú");
            Banca_online(usuario ,enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }


    }

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
            e.printStackTrace();
        } catch (IOException | ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }


    }


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
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
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
            // //////////////////////////////////////////////////////////////
            // CONVERTIR CLAVE A STRING Y VISUALIZAR/////////////////////////
            // obteniendo la version codificada en base 64 de la clave

            System.out.println("La clave es: " + key);

            // //////////////////////////////////////////////////////////////
            // CREAR CIFRADOR Y PONER EN MODO DESCIFRADO//////////////////
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
                e.printStackTrace();
            }

            // Enviamos la clave

            enviar_objeto.writeObject(key);


            mensajeRecibido = (byte[]) recibir_objeto.readObject();

            mensajeRecibidoDescifrado = new String(desCipher.doFinal(mensajeRecibido));
            System.out.println("El texto enviado por el cliente y descifrado por el servidor es : " + new String(mensajeRecibidoDescifrado));



            // cierra los paquetes de datos, el socket y el servidor
            //ois.close();
            //oos.close();

            System.out.println("Fin de la conexion");



        } catch (IOException ex) {
            // Logger.getLogger(hilo.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new String(mensajeRecibidoDescifrado);
    }
}
