import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    
    public static final String SERVER_IP = "192.168.1.19"; 
    // public static final String SERVER_IP = "192.168.1.36"; // IP PC FIXE 
    public static final int SERVER_PORT = 4999;
    private static final Server SERVER = initServer(SERVER_PORT);
    private final ServerSocket ss;
    private static List<ClientHandler> playersConnected = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private boolean running = false;

    private Server(int port) throws IOException {
        this.ss = new ServerSocket(port);
    }

    private static Server initServer(int port) {
        try {
            return new Server(port);
        } catch (IOException io) {
            return null;
        }
    }

    @Override
    public void run() {
        this.running = true;
        System.out.println("[SERVER] Server has started on port : " + SERVER_PORT);
        System.out.println("[SERVER] Server IPV4 Adress : " + SERVER_IP);
        while (running) {
            try {
                System.out.println("[SERVER] Waiting for a client connection ...");
                Socket s = ss.accept();
                ClientHandler client = new ClientHandler(s, playersConnected, playersConnected.size() + 1);
                playersConnected.add(client);
                System.out.println("[SERVER] Client " + client.getId() + " connected");
                pool.execute(client);
                // Thread thread = new Thread(client);
                // thread.start();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Oh that's bad ...");
            }
        }
    }
    
    public static void main(String[] args) {
        new Thread(SERVER).start();
    }

}