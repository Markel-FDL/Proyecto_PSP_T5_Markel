import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

        public static void main(String[] args) throws IOException {
            ServerSocket servidor = new ServerSocket(5557);


            while (true) {
                Socket scServidor = servidor.accept();
                Hilos hilo = new Hilos(scServidor, servidor);
                Thread t1 = new Thread(hilo);
                t1.start();
            }
        }
}
