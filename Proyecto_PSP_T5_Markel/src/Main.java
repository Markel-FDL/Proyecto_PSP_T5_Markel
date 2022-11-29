import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

    }
}

class Cliente {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Seleccion();
    }

    public static void Seleccion() throws IOException, NoSuchAlgorithmException {
        int num = 0;

        Socket cliente = new Socket("localhost", 5553);

        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());

        try {
            do {
                System.out.println("Bienvenido a la banca online");
                System.out.println("\n1. Iniciar sesión");
                System.out.println("2. Registrarse");
                System.out.println("Selecciona una opción (9 salir): ");
                num = scanner.nextInt();
                scanner.nextLine();
                if (num < 0 || num >= 3 && num != 9) {
                    System.out.println("Error al insertar dato");
                }
            } while (num < 0 || num > 2);
        } catch (Exception e) {
            System.out.println("Error");
        }

        if (num == 1) {
            enviar_dato.writeInt(num);
            Iniciar_sesion(cliente);
        } else if (num == 2) {
            enviar_dato.writeInt(num);
            Registrarse(cliente);
        } else if (num == 9) {
            System.exit(0);
        }


    }

    public static void Registrarse(Socket cliente) throws NoSuchAlgorithmException, IOException {
        String nombre;
        String apellido = null;
        int edad = 0;
        String email = null;
        String usuarioo = null;
        String contrasena;

        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());

        System.out.println("Menu de registro");

        try {
            do {
                System.out.println("\nNombre: ");
                nombre = scanner.nextLine();
                if (Objects.equals(nombre, "")) {
                    System.out.println("No puedes dejar el nombre vacío");
                }
            } while (Objects.equals(nombre, ""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            do {
                System.out.println("\nApellido: ");
                apellido = scanner.nextLine();
                if (Objects.equals(apellido, "")) {
                    System.out.println("No puedes dejar el apellido vacío");
                }
            } while (Objects.equals(apellido, ""));
        } catch (Exception e) {
            System.out.println("Error");
        }

        try {
            do {
                System.out.println("\nEdad: ");
                edad = scanner.nextInt();
                scanner.nextLine();
                if (edad < 0 || edad > 110) {
                    System.out.println("No puedes ingresar esa edad");
                }
            } while (edad < 0 || edad > 110);
        } catch (Exception e) {
            System.out.println("Error");
        }

        Pattern pat;
        Matcher mat;
        boolean s = true;
        try {
            do {
                System.out.println("\nIntroduce tu email: ");
                email = scanner.nextLine();
                pat = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
                mat = pat.matcher(email);
                if (!mat.find()) {
                    System.out.println("email no valido");
                    s = false;
                }
            } while (!s);
        } catch (Exception e) {
            System.out.println("Error");
        }

        try {
            do {
                System.out.println("\nUsuario: ");
                usuarioo = scanner.nextLine();
                if (Objects.equals(usuarioo, "")) {
                    System.out.println("No puedes dejar el usuario vacío");
                }
            } while (Objects.equals(usuarioo, ""));
        } catch (Exception e) {
            System.out.println("Error");
        }

        MessageDigest md = MessageDigest.getInstance("SHA");
        byte[] resumen = new byte[0];
        try {
            do {
                System.out.println("\nIntroduce tu contraseña: ");
                contrasena = scanner.nextLine();
                if (Objects.equals(contrasena, "")) {
                    System.out.println("No puedes dejar el apellido vacío");
                }
            } while (Objects.equals(contrasena, ""));
            byte[] contra = contrasena.getBytes();
            md.update(contra);
            resumen = md.digest();
        } catch (Exception e) {
            System.out.println("Error");
        }

        Usuarios usuario = new Usuarios(nombre, apellido, edad, email, usuarioo, new String(resumen));

        enviar_objeto.writeObject(usuario);


        System.out.println("Usuario creado");

        Seleccion();

    }

    public static void Iniciar_sesion(Socket cliente) throws NoSuchAlgorithmException, IOException {
        String usuarioo = null;
        String contrasena;

        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());

        System.out.println("Inicio de sesión");

        try {
            do {
                System.out.println("\nUsuario: ");
                usuarioo = scanner.nextLine();
                if (Objects.equals(usuarioo, "")) {
                    System.out.println("No puedes dejar el usuario vacío");
                }
            } while (Objects.equals(usuarioo, ""));
        } catch (Exception e) {
            System.out.println("Error");
        }

        MessageDigest md = MessageDigest.getInstance("SHA");
        byte[] resumen = new byte[0];
        try {
            do {
                System.out.println("\nIntroduce tu contraseña: ");
                contrasena = scanner.nextLine();
                if (Objects.equals(contrasena, "")) {
                    System.out.println("No puedes dejar el apellido vacío");
                }
            } while (Objects.equals(contrasena, ""));
            byte[] contra = contrasena.getBytes();
            md.update(contra);
            resumen = md.digest();
        } catch (Exception e) {
            System.out.println("Error");
        }

        Usuarios usuario = new Usuarios(usuarioo, new String(resumen));

        enviar_objeto.writeObject(usuario);

        DataInputStream in = new DataInputStream(cliente.getInputStream());
        DataOutputStream out = new DataOutputStream(cliente.getOutputStream());

        String contrato = in.readUTF();
        System.out.println(contrato);
        System.out.println("\nAceptas el contrato? (s/n): ");
        String contrat = scanner.nextLine();
        if (Objects.equals(contrat, "s")) {
            out.writeUTF(contrat);
        } else if (contrat == "n" || contrat != "s") {
            Seleccion();

        }

        String resu = "FiRMA VERIFICADA CON CLAVE PÚBLICA.";
        String resu2 = "FiRMA NO VERIFICADA";
        String respuesta = in.readUTF();

        if (respuesta.equals(resu)){
            Menu_banca();
        } else if (respuesta.equals(resu2)) {
            System.out.printf("Firma fallida");
            Seleccion();
        }

    }

    public static void Menu_banca() {

    }


}

class Servidor{
    public static void main(String[] args) throws IOException {
        ServerSocket servidor = new ServerSocket(5553);


        while (true) {
            Socket scServidor = servidor.accept();
            Hilo hilo = new Hilo(scServidor, servidor);
            Thread t1 = new Thread(hilo);
            t1.start();
        }
    }



}


class Hilo implements Runnable{

    Socket s;
    ServerSocket ss;

    public Hilo(Socket s, ServerSocket ss) {
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
        ObjectInputStream inDatoObjeto = new ObjectInputStream(s.getInputStream());
        DataInputStream inDato = new DataInputStream(s.getInputStream());

        int i = inDato.readInt();
        System.out.println("Dato");

        if (i == 1){
            Comprobar_inicio_sesion();
            Comprobar_normas_banco();
        } else if (i == 2) {
            Registro_servidor();
            servidor();
        }

    }

    public void Registro_servidor() throws IOException, ClassNotFoundException {
        ObjectInputStream inDato = new ObjectInputStream(s.getInputStream());

        Usuarios usuario = (Usuarios) inDato.readObject();

        Usuarios usuarios = new Usuarios();

        usuarios.escribir_usuario(usuario);

        System.out.println("Usuario creado");



    }

    public void Comprobar_inicio_sesion() throws IOException, ClassNotFoundException {
        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());

        Usuarios usuario = (Usuarios) outDato.readObject();

        try {
            while (usuario != null){
 /*               if (usuario.usuario ==  && usuario.contrasena == ){

                }*/
                usuario = (Usuarios) outDato.readObject();
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }


    }

    public void Comprobar_normas_banco() throws IOException {

        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());

        String contrato = "Normas del banco.";

        DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());
        DataInputStream recivir_norma = new DataInputStream(s.getInputStream());

        enviar_norma.writeUTF(contrato);

        String respuesta = recivir_norma.readUTF();

        if (respuesta.equals("s")) {
            Firmado_digital();
        }


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
            Signature dsa = Signature.getInstance("SHA1withDSA");
            dsa.initSign(clavepriv);
            String mensaje = "Contrato aceptado";
            dsa.update(mensaje.getBytes());
            byte[] firma = dsa.sign(); //MENSAJE FIRMADO
//EL RECEPTOR DEL MENSAJE
//VERIFICA CON LA CLAVE PUIBLICA EL MENSAJE FIRMADO
//AL OBJETO signature sE LE suministralos datos a verificar
            Signature verificadsa = Signature.getInstance("SHA1withDSA");
            verificadsa.initVerify(clavepub);
            verificadsa.update(mensaje.getBytes());
            boolean check = verificadsa.verify(firma);
            String resu = "FiRMA VERIFICADA CON CLAVE PÚBLICA.";
            String resu2 = "FiRMA NO VERIFICADA";
            if (check) {
                System.out.println("FiRMA VERIFICADA CON CLAVE PÚBLICA.");
                outData.writeUTF(resu);
            } else {
                System.out.println("FiRMA NO VERIFICADA");
                outData.writeUTF(resu2);
            }

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (SignatureException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }


    }

}

class Usuarios implements Serializable{

    String nombre;
    String apellido;
    int edad;
    String email;
    String usuario;
    String contrasena;

    public Usuarios(String nombre, String apellido, int edad, String email, String usuario, String contrasena){
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.email = email;
        this.usuario = usuario;
        this.contrasena = contrasena;
    }
    public Usuarios(){

    }
    
    public Usuarios(String usuario, String contrasena){
        this.usuario = usuario;
        this.contrasena = contrasena;
    }

    public void Mostrar(){
        System.out.println("Nombre: " + nombre + " \nApellido: " + apellido + "\nEdad: " + edad + "\nEmail: " + email + "\nUsuario: " + usuario + "\ncontrasena: " + contrasena);
    }

    public void escribir_usuario(Usuarios usuario) throws IOException {
        ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream("Usuarios.dat"));

        escribir.writeObject(usuario);

        escribir.close();

    }

    public void leer_usuario() throws IOException, ClassNotFoundException {

        // ObjectInputStream para mostrar por pantalla los directores
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Usuarios.dat"));
        Usuarios usuario = (Usuarios) mostrar.readObject();
        // Mostramos por pantalla todos los directores
        try {
            while (usuario != null){
                usuario.Mostrar();
                usuario = (Usuarios) mostrar.readObject();
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
    }

}


