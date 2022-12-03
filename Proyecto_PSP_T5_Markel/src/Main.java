import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

    }
}

class Cliente {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        Seleccion();
    }

    public static void Seleccion() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        int num = 0;

        Socket cliente = new Socket("localhost", 5556);

       // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
       // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
       // DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

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
            enviar_dato.writeInt(1);
            Iniciar_sesion(cliente);
        } else if (num == 2) {
            enviar_dato.writeInt(2);
            Registrarse(cliente);
        } else if (num == 9) {
            System.exit(0);
        }


    }

    public static void Registrarse(Socket cliente) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        String nombre;
        String apellido = null;
        int edad = 0;
        String email = null;
        String usuarioo = null;
        String contrasena;

        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
       // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
       // DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
       // DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

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
                pat = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+.[a-zA-Z0-9.-]$");
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

        Random random = new Random();
        int cuenta_ban = random.nextInt(0, 500);
        String cuentas = String.valueOf(cuenta_ban);
        int dinero = 500;

        Usuarios usuario = new Usuarios(nombre, apellido, edad, email, usuarioo, new String(resumen));
        Cuentas_bancarias cuenta = new Cuentas_bancarias(usuario, cuentas, dinero);

        enviar_objeto.writeObject(usuario);
        enviar_objeto.writeObject(cuenta);


        System.out.println("Usuario y cuenta creado");

        Seleccion();

    }

    public static void Iniciar_sesion(Socket cliente) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        String usuarioo = null;
        String contrasena;

        System.out.println("Ha entrado en Inicio de sesión");
        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
       // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
       // DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

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

        String inicio = recibir_dato.readUTF();

        if (!inicio.equals("s")){
            System.out.println("No se ha podido iniciar sesión");
            Seleccion();
        } else {
            DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
            System.out.println("Te has conectado");


            System.out.println("\nQuieres ver el contrato? (s/n) (es necesario para continuar): ");
            String az = scanner.nextLine();
            if (Objects.equals(az, "s")) {
                out.writeUTF("s");
                String contrato = recibir_dato.readUTF();
                System.out.println(contrato);
                System.out.println("\nAceptas el contrato? (s/n): ");
                String contrat = scanner.nextLine();
                if (Objects.equals(contrat, "s")) {
                    out.writeUTF("s");
                } else if (contrat == "n" || contrat != "s") {
                    Seleccion();

                }

                String resu = "FiRMA VERIFICADA CON CLAVE PÚBLICA.";
                String resu2 = "FiRMA NO VERIFICADA";
                String respuesta = recibir_dato.readUTF();

                if (respuesta.equals(resu)) {
                    Menu_banca(cliente);
                } else if (respuesta.equals(resu2)) {
                    System.out.println("Firma fallida");
                    Seleccion();
                }

            } else {
                System.out.println("Volviendo al menu");
                Seleccion();
            }

        }


    }

    public static void Menu_banca(Socket cliente) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
      //  ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
      //  ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
      //  DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());


        int opcion = 0;

        try {
            do {
                System.out.println("Menu banca dentro");
                System.out.println("\n1. Ver saldo de una cuenta bancaria");
                System.out.println("2. Transferencia de dinero");
                System.out.println("Selecciona una opcion: ");
                opcion = scanner.nextInt();
                scanner.nextLine();
                if (opcion < 0 || opcion >= 3 && opcion != 9) {
                    System.out.println("Error al insertar dato");
                }
            }while (opcion < 0 || opcion >= 3 && opcion != 9);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (opcion == 1) {
            enviar_dato.writeInt(1);
            opcion = 0;
            Ver_saldo(cliente);
        } else if (opcion == 2) {
            enviar_dato.writeInt(2);
            opcion = 0;

        } else if (opcion == 9) {
            System.exit(0);
        }

    }

    public static void Ver_saldo(Socket cliente) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
       // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
       // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

        String sec_cuenta;

        String zz = recibir_dato.readUTF();

        System.out.println(zz);

        System.out.println("Inserta el numero de la cuenta: ");
        sec_cuenta = scanner.nextLine();

        Cifrado_simetrico(cliente, sec_cuenta);

        int dinero = recibir_dato.readInt();

        if (dinero == -1){
            System.out.println("La cuenta bancaria no existe");
            Menu_banca(cliente);
        }

        System.out.println("Dinero en la cuenta: " + dinero);

        Menu_banca(cliente);
    }

    public static void Cifrado_simetrico(Socket cliente, String cuenta) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
       // DataInputStream in = new DataInputStream(cliente.getInputStream());
       // DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());

        String mensaje = "";
        //String key;
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
            System.out.print("Reocgiendo mensajes\n");

            mensajeEnviadoCifrado = desCipher.doFinal(cuenta.getBytes());

            enviar_objeto.writeObject(mensajeEnviadoCifrado);


        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void Transferencia_dinero(Socket cliente) throws IOException {
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

        String sec_cuenta;

        String zz = recibir_dato.readUTF();

        System.out.println("Tu cuenta: " + zz);

        System.out.println("Inserta el numero de la cuenta a donde enviar el dinero: ");
        sec_cuenta = scanner.nextLine();




        enviar_dato.writeUTF(sec_cuenta);

    }

}








