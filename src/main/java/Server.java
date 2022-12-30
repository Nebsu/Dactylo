import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public class Server implements Runnable {
    
    public static final String SERVER_IP = "192.168.1.19"; 
    // public static final String SERVER_IP = "192.168.1.36"; // IP PC FIXE 
    public static final int SERVER_PORT = 4999;
    private static final Server SERVER = initServer(SERVER_PORT);
    private final ServerSocket ss;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(10);
    private static List<String> playersList = new ArrayList<>();
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

    public static void addPlayer(String name) {
        playersList.add(name);
    }

    public static List<String> getPlayersList() {return playersList;}

    @Override
    public void run() {
        this.running = true;
        System.out.println("[SERVER] Server has started on port : " + SERVER_PORT);
        System.out.println("[SERVER] Server IPV4 Adress : " + SERVER_IP);
        while (running) {
            try {
                System.out.println("[SERVER] Waiting for a client connection ...");
                Socket s = ss.accept();
                ClientHandler client = new ClientHandler(s, clients.size() + 1);
                clients.add(client);
                System.out.println("[SERVER] Client " + client.getId() + " connected");
                pool.execute(client);
                printClients();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Server");
            }
        }
    }

    public static void showToEveryone() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "playersList");
        map.put("list", playersList);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
    }

    private void printClients() {
        System.out.println("\n Print clients : ");
        for (ClientHandler client : clients) {
            System.out.println("Client " + client.getId());
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        new Thread(SERVER).start();
    }

}