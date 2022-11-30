import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Usuarios implements Serializable{

        String nombre;
        String apellido;
        int edad;
        String email;
        String usuario;
        String contrasena;

        int cuenta_bancaria;

        int dinero;

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

        public Usuarios(String usuario, int cuenta_bancaria, int dinero){
            this.usuario = usuario;
            this.cuenta_bancaria = cuenta_bancaria;
            this.dinero = dinero;
        }

        public void Mostrar(){
            System.out.println("Nombre: " + nombre + " \nApellido: " + apellido + "\nEdad: " + edad + "\nEmail: " + email + "\nUsuario: " + usuario + "\ncontrasena: " + contrasena);
        }

       public void escribir_usuario(Usuarios usuario) throws IOException, ClassNotFoundException {
            List<Usuarios> usuarios = new ArrayList<Usuarios>();

            ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream("Usuarios.dat"));

            escribir.writeObject(usuario);

            escribir.close();

            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Usuarios.dat"));
            Usuarios usuario1 = (Usuarios) mostrar.readObject();

            try {
                while (usuario1 != null){
                    usuarios.add(usuario1);
                    usuario1 = (Usuarios) mostrar.readObject();
                }
            } catch (IOException e) {
                System.out.println("Ha ocurrido un error");
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }
            mostrar.close();


            usuarios.add(usuario);

            ObjectOutputStream escribir2 = new ObjectOutputStream(new FileOutputStream("Usuarios.dat"));

            int i_d = 0;
            for (Usuarios d : usuarios) {
                escribir2.writeObject(d);
                d.Mostrar();
                i_d++;
            }
            escribir2.writeObject(null);
            escribir2.close();

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

        public void escribir_cuentas_bancarias(Usuarios usuario) throws IOException, ClassNotFoundException {
            List<Usuarios> cuentas_bancarias = new ArrayList<Usuarios>();
            Random random = new Random();

            File file = new File("Cuentas.dat");

            if (file.length() == 0){

            } else {
                ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream(file));
                Usuarios usuario1 = (Usuarios) mostrar.readObject();

                try {
                    while (usuario1 != null){
                        cuentas_bancarias.add(usuario1);
                        usuario1 = (Usuarios) mostrar.readObject();
                    }
                } catch (IOException e) {
                    System.out.println("Ha ocurrido un error");
                } catch (ClassNotFoundException e) {
                    System.out.println("Ha habido algun error con la clase");
                }
            }


            int cuenta = random.nextInt(0, 500);
            int dinero = 500;

            cuentas_bancarias.add(new Usuarios(usuario.usuario, cuenta, dinero));

            ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream("Cuentas.dat"));
            int i_d = 0;
            for (Usuarios d : cuentas_bancarias) {
                escribir.writeObject(d);
                d.Mostrar();
                i_d++;
            }
            escribir.writeObject(null);
            escribir.close();
        }

}
