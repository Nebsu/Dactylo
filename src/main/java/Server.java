import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    
    public static final String SERVER_IP = "192.168.1.19"; 
    // public static final String SERVER_IP = "192.168.1.36"; // IP PC FIXE 
    public static final int SERVER_PORT = 4999;
    private static final Server SERVER = initServer(SERVER_PORT);
    private final ServerSocket ss;
    private static volatile List<Client> playersConnected = new ArrayList<>();
    private static int count = 1;
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
    public synchronized void run() {
        synchronized (this) {
            this.running = true;
            System.out.println("Server has started on port : " + SERVER_PORT);
            System.out.println("Server IPV4 Adress : " + SERVER_IP);
            while (running) {
                try {
                    Socket s = ss.accept();
                    Client cl = connectClient(s);
                    // Thread.currentThread().notifyAll();
                    this.receiveMessageFromClient(cl);
                    this.sendMessageToClient(cl, "Hi ! Welcome to my server");
                    // System.out.println();
                    // this.print_clients();
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Oh that's bad ...");
                }
            }
        }
    }

    public static void close() {
        System.exit(0);
    }

    private synchronized Client connectClient(Socket s) {
        Client newClient = new Client(s);
        newClient.setId(count++);
        playersConnected.add(newClient);
        System.out.println("Client " + newClient.getId() + " connected");
        return newClient;
    }

    private void receiveMessageFromClient(Client c) throws IOException {
        if (!playersConnected.contains(c)) throw new IllegalArgumentException("Client not found");
        InputStreamReader in = new InputStreamReader(c.getSocket().getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String str = bf.readLine();
        System.out.println(c.toString() + " : " + str);
    }

    private void sendMessageToClient(Client c, String message) throws IOException {
        PrintWriter pr = new PrintWriter(c.getSocket().getOutputStream());
        pr.println(message);
        pr.flush();
    }

    public static synchronized Client getClient() {
        System.out.println(playersConnected.get(count - 2));
        return playersConnected.get(count - 2);
    }

    private void print_clients() {
        for (Client c : playersConnected) {
            System.out.println(c.toString());
        }
        System.out.println();
    }
    
    public static synchronized void main(String[] args) {
        new Thread(SERVER).start();
    }

}