import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
                if (num < 0 || num >= 3 || num != 9) {
                    System.out.println("Error al insertar dato");
                }
            } while (num < 0 || num > 2);
        } catch (Exception e) {
            System.out.println("Error");
        }

        if (num == 1) {
            enviar_dato.write(num);
            Iniciar_sesion(cliente);
        } else if (num == 2) {
            enviar_dato.write(num);
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
        try {
            do {
                System.out.println("\nIntroduce tu email: ");
                email = scanner.nextLine();
                pat = Pattern.compile("^(.+)@(.+)$");
                mat = pat.matcher(email);
                if (!mat.find()) {
                    System.out.println("email no valido");
                }
            } while (!mat.find());
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

        String contrato = in.readUTF();
        System.out.println(contrato);
        System.out.println("\nAceptas el contrato? (s/n): ");
        String contrat = scanner.nextLine();
        if (Objects.equals(contrat, "s")){
            // TODO: Se necesita firmado digital
        } else if (contrat == "n" || contrat != "s") {
            Seleccion();
        }

    }

    public void Menu_banca(){

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

    }

    public void servidor() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        ObjectInputStream inDatoObjeto = new ObjectInputStream(s.getInputStream());
        DataInputStream inDato = new DataInputStream(s.getInputStream());

        if (inDato.read() == 1){
            Comprobar_inicio_sesion();
            Comprobar_normas_banco();
        } else if (inDato.read() == 2) {
            Registro_servidor();
            Cliente.Seleccion();
        }

    }

    public void Registro_servidor() throws IOException, ClassNotFoundException {
        ObjectInputStream inDato = new ObjectInputStream(s.getInputStream());

        Usuarios usuario = (Usuarios) inDato.readObject();

        Usuarios usuarios = new Usuarios();

        usuarios.escribir_usuario(usuario);



    }

    public void Comprobar_inicio_sesion() throws IOException, ClassNotFoundException {
        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());

        Usuarios usuario = (Usuarios) outDato.readObject();


    }

    public void Comprobar_normas_banco() throws IOException {

        ObjectInputStream outDato = new ObjectInputStream(s.getInputStream());

        String contrato = "Normas del banco.";

        DataOutputStream enviar_norma = new DataOutputStream(s.getOutputStream());
        DataInputStream recivir_norma = new DataInputStream(s.getInputStream());

        enviar_norma.writeUTF(contrato);

        String respuesta = recivir_norma.readUTF();

        if (respuesta == "s") {

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
        // ObjectOutputStream para poder escribir en el fichero binario de directores "Directores.dat". Si no existe, se crea automaticamente.
        ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream("Usuarios.txt"));

        escribir.writeObject(usuario);

        escribir.writeObject(null);
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


