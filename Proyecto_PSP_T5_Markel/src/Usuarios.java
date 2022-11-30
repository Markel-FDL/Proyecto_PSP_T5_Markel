import java.io.*;

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
