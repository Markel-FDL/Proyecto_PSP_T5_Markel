import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cuentas_bancarias implements Serializable {

    Usuarios usuario;
    String cuenta_bancaria;

    int dinero;

    List<Cuentas_bancarias> cuentas_bancarias = new ArrayList<>();

    public Cuentas_bancarias(Usuarios usuario, String cuenta_bancaria, int dinero) {
        this.usuario = usuario;
        this.cuenta_bancaria = cuenta_bancaria;
        this.dinero = dinero;
    }

    public Cuentas_bancarias() {

    }

    public void Mostrar() {
        System.out.println("Usuario: " + usuario.usuario + " \nCuenta bancaria: " + cuenta_bancaria + "\nDinero: " + dinero+ "\n");
    }

    public void escribir_cuentas_bancarias(Cuentas_bancarias cuenta) throws IOException, ClassNotFoundException {



        File f = new File("Cuentas.dat");
        if (!f.isFile()) {
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

    public void Modificar_cuentas_bancarias(Cuentas_bancarias cuenta) throws IOException, ClassNotFoundException {

        cuentas_bancarias.clear();

        File f = new File("Cuentas.dat");
        if (!f.isFile()) {
            ObjectOutputStream escribir1 = new ObjectOutputStream(new FileOutputStream(f));

            escribir1.writeObject(null);
            escribir1.close();
        } else {
            ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream(f));
            Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

            try {
                while (usuario1 != null) {
                    if (Objects.equals(usuario1.cuenta_bancaria, cuenta.cuenta_bancaria)){
                        usuario1 = (Cuentas_bancarias) mostrar.readObject();
                    } else {
                        cuentas_bancarias.add(usuario1);
                        usuario1 = (Cuentas_bancarias) mostrar.readObject();
                    }
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

    public Cuentas_bancarias Comprobar_cuenta(String cuenta_bancaria) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //  DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        //  DataInputStream inData = new DataInputStream(s.getInputStream());
        String usuari;
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        Cuentas_bancarias zx = null;
        try {
            while (usuario1 != null) {
               // System.out.println("Cuenta bancaria: " + usuario1.cuenta_bancaria);
                if (Objects.equals(cuenta_bancaria, usuario1.cuenta_bancaria)){
                    zx = usuario1;
                    break;
                }
                usuario1 = (Cuentas_bancarias) mostrar.readObject();
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
        mostrar.close();
        return zx;
    }

    public Cuentas_bancarias Comprobar_cuenta2(Usuarios cuenta_bancaria) throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        //  DataOutputStream outData = new DataOutputStream(s.getOutputStream());
        //  DataInputStream inData = new DataInputStream(s.getInputStream());
        String usuari;
        ObjectInputStream mostrar = new ObjectInputStream(new FileInputStream("Cuentas.dat"));
        Cuentas_bancarias usuario1 = (Cuentas_bancarias) mostrar.readObject();

        Cuentas_bancarias zx = null;
        try {
            while (usuario1 != null) {
                // System.out.println("Cuenta bancaria: " + usuario1.cuenta_bancaria);
                if (Objects.equals(cuenta_bancaria.usuario, usuario1.usuario.usuario)){
                    zx = usuario1;
                    break;
                }
                usuario1 = (Cuentas_bancarias) mostrar.readObject();
            }
        } catch (IOException e) {
            System.out.println("Ha ocurrido un error");
        } catch (ClassNotFoundException e) {
            System.out.println("Ha habido algun error con la clase");
        }
        mostrar.close();
        return zx;
    }

    public String getCuenta_bancaria() {
        return cuenta_bancaria;
    }

    public void setCuenta_bancaria(String cuenta_bancaria) {
        this.cuenta_bancaria = cuenta_bancaria;
    }

    public int getDinero() {
        return dinero;
    }

    public void setDinero(int dinero) {
        this.dinero = dinero;
    }
}
