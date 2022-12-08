import javax.crypto.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

    }
}

/*class Streams {


    ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
    ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
    DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
    DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

    public Streams() throws IOException {

    }

}*/
class Cliente {
    static Scanner scanner = new Scanner(System.in);



    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        Socket cliente = new Socket("localhost", 5557);

        FileHandler fh;

        Logger logger = Logger.getLogger("MyLog");

        fh = new FileHandler("./log_actividad_cliente.log", true);

        logger.setUseParentHandlers(false);
        SimpleFormatter formato = new SimpleFormatter();
        fh. setFormatter(formato);

        logger.setLevel(Level.ALL);
        logger.addHandler(fh);

        ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
        ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

        Seleccion(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
    }

    public static void Seleccion(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        int num = 0;




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
            Iniciar_sesion(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (num == 2) {
            enviar_dato.writeInt(2);
            Registrarse(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (num == 9) {
            System.exit(0);
        }


    }

    public static void Registrarse(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        String nombre;
        String apellido = null;
        int edad = 0;
        String email = null;
        String usuarioo = null;
        String contrasena;

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

        do {
            try {
                System.out.println("\nEdad: ");
                edad = scanner.nextInt();
                scanner.nextLine();
                if (edad < 0 || edad > 110) {
                    System.out.println("No puedes ingresar esa edad");
                }

        } catch (Exception e) {
            System.out.println("Error");
            scanner.nextLine();
        }
        } while (edad < 0 || edad > 110);

        Pattern pat;
        Matcher mat;
        boolean s = true;
        try {
            do {
                s = true;
                System.out.println("\nIntroduce tu email: ");
                email = scanner.nextLine();
                pat = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
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

        Seleccion(cliente, enviar_objeto,recibir_objeto, enviar_dato, recibir_dato);

    }

    public static void Iniciar_sesion(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws NoSuchAlgorithmException, IOException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        String usuarioo = null;
        String contrasena;

        System.out.println("Ha entrado en Inicio de sesión");


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
            Seleccion(cliente, enviar_objeto,recibir_objeto, enviar_dato, recibir_dato);
        } else {
            System.out.println("Te has conectado");


            System.out.println("\nQuieres ver el contrato? (s/n) (es necesario para continuar): ");
            String az = scanner.nextLine();
            if (Objects.equals(az, "s")) {
                enviar_dato.writeUTF("s");
                String contrato = recibir_dato.readUTF();
                System.out.println(contrato);
                System.out.println("\nAceptas el contrato? (s/n): ");
                String contrat = scanner.nextLine();
                if (Objects.equals(contrat, "s")) {
                    enviar_dato.writeUTF("s");
                } else if (contrat == "n" || contrat != "s") {
                    enviar_dato.writeUTF("n");
                    System.out.println("Vuelves al menú");
                    Seleccion(cliente, enviar_objeto,recibir_objeto, enviar_dato, recibir_dato);
                }
                String resu = "FiRMA VERIFICADA CON CLAVE PÚBLICA.";
                String resu2 = "FiRMA NO VERIFICADA";
                String respuesta = recibir_dato.readUTF();

                if (respuesta.equals(resu)) {
                    System.out.println("Vuelves al menú");
                    Menu_banca(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
                } else if (respuesta.equals(resu2)) {
                    System.out.println("Firma fallida");
                    Seleccion(cliente, enviar_objeto,recibir_objeto, enviar_dato, recibir_dato);
                }

            } else {
                System.out.println("Volviendo al menu");
                enviar_dato.writeUTF("n");
                Seleccion(cliente, enviar_objeto,recibir_objeto, enviar_dato, recibir_dato);
            }

        }


    }

    public static void Menu_banca(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {

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
            Ver_saldo(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (opcion == 2) {
            enviar_dato.writeInt(2);
            opcion = 0;
            Transferencia_dinero(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        } else if (opcion == 9) {
            System.exit(0);
        }

    }

    public static void Ver_saldo(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        // DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        // DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

        String sec_cuenta;

        String zz = recibir_dato.readUTF();

        System.out.println(zz);

        System.out.println("Inserta el numero de la cuenta: ");
        sec_cuenta = scanner.nextLine();

        Cifrado_simetrico(cliente, sec_cuenta, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

        int dinero = recibir_dato.readInt();

        if (dinero == -1){
            System.out.println("La cuenta bancaria no existe");
            Menu_banca(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
        }

        System.out.println("Dinero en la cuenta: " + dinero);

        Menu_banca(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);
    }

    public static void Cifrado_simetrico(Socket cliente, String cuenta, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        // DataInputStream in = new DataInputStream(cliente.getInputStream());
        // DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());

        //String mensaje = "";
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

    public static void Transferencia_dinero(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        // DataOutputStream enviar_dato = new DataOutputStream(cliente.getOutputStream());
        // DataInputStream recibir_dato = new DataInputStream(cliente.getInputStream());

        String sec_cuenta;


        String zz = recibir_dato.readUTF();

        System.out.println("Tu cuenta del que saldra el dinero: " + zz);

        //Cifrado_simetrico(cliente, zz);

        System.out.println("Inserta el numero de la cuenta a donde enviar el dinero: ");
        sec_cuenta = scanner.nextLine();

        int din = 0;
        do {
            try {
                System.out.println("Dinero de la transferencia");
                din = scanner.nextInt();
                scanner.nextLine();
            } catch (Exception e) {
                System.out.println("Error en el dato insertado");
                din = 0;
                scanner.nextLine();

            }
        } while (din <= 0 || din > 10000);


        enviar_dato.writeInt(din);

        enviar_dato.writeInt(1);

        enviar_dato.writeUTF(sec_cuenta);

        String num = Cifrado_simentrico_trans(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);

        System.out.println(num);

        System.out.println("Ingresa el numero que aparece para completar la transacción: ");
        String as = scanner.nextLine();

        if (num.equals(as)){
            enviar_dato.writeInt(1);
        } else {
            enviar_dato.writeInt(2);
        }

        Menu_banca(cliente, enviar_objeto, recibir_objeto, enviar_dato, recibir_dato);



    }
    public static void Cifrado_simetrico_trans(Socket cliente, String cuenta, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException, IOException {
        // DataInputStream in = new DataInputStream(cliente.getInputStream());
        // DataOutputStream out = new DataOutputStream(cliente.getOutputStream());
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());

        //String mensaje = "";
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

            desCipher.init(Cipher.DECRYPT_MODE, key);
            System.out.print("Inserta \n");

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

    public static String Cifrado_simentrico_trans(Socket cliente, ObjectOutputStream enviar_objeto, ObjectInputStream recibir_objeto, DataOutputStream enviar_dato, DataInputStream recibir_dato) throws IOException {
        String mensajeRecibidoDescifrado = "";
        // ObjectInputStream recibir_objeto = new ObjectInputStream(cliente.getInputStream());
        // ObjectOutputStream enviar_objeto = new ObjectOutputStream(cliente.getOutputStream());
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


            //System.out.println("Fin de la conexion");



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








