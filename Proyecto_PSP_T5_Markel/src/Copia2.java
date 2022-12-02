import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Objects;

public class Copia2 {
    public class Hilos implements Runnable{

        Socket s;
        ServerSocket ss;

        public Hilos(Socket s, ServerSocket ss) {
            this.s = s;
            this.ss = ss;
        }

        @Override
        public void run() {
            try {
                servidor();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }


        }

        public void servidor() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
            // ObjectInputStream inDatoObjeto = new ObjectInputStream(s.getInputStream());
            DataInputStream inDato = new DataInputStream(s.getInputStream());

            System.out.println("Dato");
            int i = inDato.readInt();
            System.out.println("Dato2");

            if (i == 1){
                Comprobar_inicio_sesion();
            } else if (i == 2) {
                Registro_servidor();
                servidor();
            }

        }

        public void Registro_servidor() throws IOException, ClassNotFoundException {
            ObjectInputStream inDato = new ObjectInputStream(s.getInputStream());

            Usuarios usuario = (Usuarios) inDato.readObject();
            Cuentas_bancarias cuenta = (Cuentas_bancarias) inDato.readObject();

            Usuarios usuarios = new Usuarios();
            Cuentas_bancarias cuentas = new Cuentas_bancarias();

            usuarios.escribir_usuario(usuario);

            cuentas.escribir_cuentas_bancarias(cuenta);

            System.out.println("Usuario creado");



        }

        public void Comprobar_inicio_sesion() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
            ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());
            DataOutputStream enviar_dato = new DataOutputStream(s.getOutputStream());
            DataInputStream recivir_norma = new DataInputStream(s.getInputStream());
            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Usuarios.dat"));

            int es = 0;
            System.out.println("Ha entrado en Inicio de sesión");
            Usuarios usuario = (Usuarios) outDato.readObject();
            Usuarios usuarios = (Usuarios) mostrar.readObject();

            try {
                while (usuarios != null){
                    if (Objects.equals(usuarios.usuario, usuario.usuario) && Objects.equals(usuarios.contrasena, usuario.contrasena)) {
                        String a = "s";
                        enviar_dato.writeUTF("s");
                        System.out.println("Concuerda");
                        String ds = recivir_norma.readUTF();

                        String contrato = "Normas del banco.";

                        enviar_dato.writeUTF(contrato);

                        String respuesta = recivir_norma.readUTF();

                        if (respuesta.equals("s")) {
                            Firmado_digital(usuarios);
                        }
                        es++;
                        break;
                    }
                    usuarios = (Usuarios) mostrar.readObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }
            mostrar.close();

            enviar_dato.writeUTF("d");
            servidor();

        }

        public void Comprobar_normas_banco() throws IOException {

            //   ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());
            //   DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());





        }

        public void Firmado_digital(Usuarios usuario) throws IOException {
            // DataInputStream inData = new DataInputStream(s.getInputStream());
            DataOutputStream outData = new DataOutputStream(s.getOutputStream());

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
                    outData.writeUTF("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                    Banca_online(s, usuario);
                } else {
                    System.out.println("FiRMA NO VERIFICADA");
                    outData.writeUTF("FiRMA NO VERIFICADA");
                    servidor();
                }

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (SignatureException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeyException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (NoSuchPaddingException e) {
                throw new RuntimeException(e);
            } catch (IllegalBlockSizeException e) {
                throw new RuntimeException(e);
            } catch (BadPaddingException e) {
                throw new RuntimeException(e);
            }


        }

        public void Banca_online(Socket s, Usuarios usuario) throws IOException, NoSuchAlgorithmException, ClassNotFoundException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
            DataInputStream in = new DataInputStream(s.getInputStream());
            // DataOutputStream outData = new DataOutputStream(s.getOutputStream());

            System.out.println("Banca online dentro");
            int is = in.readInt();
            System.out.println("banco");
            if (is == 1){
                Mostrar_cuentas(usuario);

            } else if (is == 2) {
                Registro_servidor();
                servidor();
            }
        }

        public void Mostrar_cuentas(Usuarios usuario) throws IOException, ClassNotFoundException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
            DataOutputStream outData = new DataOutputStream(s.getOutputStream());
            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
            Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

            try {
                while (usuario1 != null) {
                    if (usuario1.usuario.usuario.equals(usuario.usuario)) {
                        System.out.println("Cuenta bancaria: " + usuario1.cuenta_bancaria);
                        outData.writeUTF(usuario1.cuenta_bancaria);
                        usuario1 = (Cuentas_bancarias) mostrar.readObject();
                    }
                    //usuario1 = (Cuentas_bancarias) mostrar.readObject();
                }
            } catch (IOException e) {
                e.printStackTrace();
                // System.out.println("Ha ocurrido un error");
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }
            mostrar.close();
            Cuenta_Mostrar(usuario);

        }

        public void Cuenta_Mostrar(Usuarios usuarios) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
            DataInputStream inData = new DataInputStream(s.getInputStream());
            DataOutputStream outData = new DataOutputStream(s.getOutputStream());
            //    ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            //    ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));

            String mensaje = Cifrado_simentrico();

            Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

            try {
                while (usuario1 != null) {
                    if (mensaje.equals(usuario1.cuenta_bancaria) && usuario1.usuario.toString().equals(usuarios.usuario)){
                        System.out.println("Saldo de la cuenta:" + usuario1.dinero);
                        outData.writeInt(usuario1.dinero);
                        usuario1 = (Cuentas_bancarias) mostrar.readObject();
                    } else {
                        usuario1 = (Cuentas_bancarias) mostrar.readObject();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.println("Ha ocurrido un error");
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }
            mostrar.close();

            Banca_online(s, usuarios);


        }

        public String Cifrado_simentrico() {
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
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
                oos.writeObject(key);


                mensajeRecibido = (byte[]) ois.readObject();

                mensajeRecibidoDescifrado = new String(desCipher.doFinal(mensajeRecibido));
                System.out.println("El texto enviado por el cliente y descifrado por el servidor es : " + new String(mensajeRecibidoDescifrado));



                // cierra los paquetes de datos, el socket y el servidor
                ois.close();
                oos.close();

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
}
