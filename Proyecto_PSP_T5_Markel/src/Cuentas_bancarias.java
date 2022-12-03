import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Cuentas_bancarias implements Serializable{

    Usuarios usuario;
    String cuenta_bancaria;

    int dinero;

    public Cuentas_bancarias(Usuarios usuario, String cuenta_bancaria, int dinero){
        this.usuario = usuario;
        this.cuenta_bancaria = cuenta_bancaria;
        this.dinero = dinero;
    }
    public Cuentas_bancarias(){

    }

    public void Mostrar(){
        System.out.println("Usuario: " + usuario.usuario + " \nCuenta bancaria: " + cuenta_bancaria + "\nDinero: " + dinero);
    }

    public void escribir_cuentas_bancarias(Cuentas_bancarias cuenta) throws IOException, ClassNotFoundException {
        List<Cuentas_bancarias> cuentas_bancarias = new ArrayList<>();


        File f = new File("Cuentas.dat");
        if(!f.isFile()) {
            ObjectOutputStream escribir1 = new ObjectOutputStream(new FileOutputStream(f));

            escribir1.writeObject(null);
            escribir1.close();
        } else {
            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream(f));
            Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

            try {
                while (usuario1 != null) {
                    cuentas_bancarias.add(usuario1);
                    usuario1 = (Cuentas_bancarias) mostrar.readObject();
                }
            } catch (IOException e) {
                System.out.println("Ha ocurrido un error");
            } catch (ClassNotFoundException e) {
                System.out.println("Ha habido algun error con la clase");
            }
            mostrar.close();
        }

            cuentas_bancarias.add(cuenta);

            ObjectOutputStream escribir = new ObjectOutputStream(new FileOutputStream(f));
            int i_d = 0;
            System.out.println("\ncuentas_bancarias\n");
            for (Cuentas_bancarias d : cuentas_bancarias) {
                escribir.writeObject(d);
                d.Mostrar();
                i_d++;
            }
            escribir.writeObject(null);
            escribir.close();
    }

}
