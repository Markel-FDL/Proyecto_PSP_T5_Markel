import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Objects;

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
                            Firmado_digital();
                        }
                        es++;
                        break;
                    }
                    usuarios = (Usuarios) mostrar.readObject();
                }
            } catch (IOException e) {
                System.out.println("Ha ocurrido un error");
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }

            enviar_dato.writeUTF("d");
            servidor();


        }

        public void Comprobar_normas_banco() throws IOException {

            ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());
            DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());





        }

        public void Firmado_digital() throws IOException {
            DataInputStream inData = new DataInputStream(s.getInputStream());
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
                    Banca_online();
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
            }


        }

        public void Banca_online() throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
            DataInputStream inData = new DataInputStream(s.getInputStream());
            DataOutputStream outData = new DataOutputStream(s.getOutputStream());

            System.out.println("Banca online dentro");
            int i = inData.readInt();
            System.out.println("Dato2");

            if (i == 1){
                Mostrar_cuentas();

            } else if (i == 2) {
                Registro_servidor();
                servidor();
            }
        }

    public void Mostrar_cuentas() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        try {
            while (usuario1 != null) {
                System.out.println("Cuenta bancaria: " + usuario1.usuario);
                outData.writeInt(usuario1.cuenta_bancaria);
                usuario1 = (Cuentas_bancarias) mostrar.readObject();
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
        mostrar.close();
        Cuenta_Mostrar();

    }

    public void Cuenta_Mostrar() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        DataInputStream inData = new DataInputStream(s.getInputStream());
        DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));

        int cuenta = inData.readInt();

        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        try {
            while (usuario1 != null) {
                if (cuenta == usuario1.cuenta_bancaria){
                    System.out.println("Saldo de la cuenta:" + usuario1.dinero);
                    outData.writeInt(usuario1.dinero);
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

        Banca_online();



    }
}
