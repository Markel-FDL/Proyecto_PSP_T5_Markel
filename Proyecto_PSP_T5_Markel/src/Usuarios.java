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
            System.out.println("Nombre: " + nombre + " \nApellido: " + apellido + "\nEdad: " + edad + "\nEmail: " + email + "\nUsuario: " + usuario + "\ncontrasena: " + contrasena + "\n");
        }

    /**
     * Guarda el usuario recibido desde el servidor en el fichero de usuarios. Si el fichero no existe lo crea.
     * @param usuario
     * @throws IOException
     * @throws ClassNotFoundException
     */
       public void escribir_usuario(Usuarios usuario) throws IOException, ClassNotFoundException {
            List<Usuarios> usuarios = new ArrayList<Usuarios>();

           File f = new File("Usuarios.dat");
           if(!f.isFile()) {

               ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream(f));

               escribir.writeObject(null);
               escribir.close();
           } else {
               ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream(f));
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
           }

               usuarios.add(usuario);

               ObjectOutputStream escribir2 = new ObjectOutputStream(new FileOutputStream(f));

               int i_d = 0;
               System.out.println("\nUsuarios\n");
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






}
