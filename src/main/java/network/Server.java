package network;

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
    private static List<Player> playersList = new ArrayList<>();
    private static List<Player> readyPlayers = new ArrayList<>();
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

    public static List<Player> getPlayersList() {return playersList;}
    public static List<Player> getReadyPlayers() {return readyPlayers;}

    public static void addPlayer(Player p) {
        playersList.add(p);
    }

    public static void addReadyPlayer(Player p) {
        readyPlayers.add(p);
    }

    public static void removePlayer(Player p) {
        for (Player player : playersList) {
            if (player.equals(p)) {
                playersList.remove(player);
                break;
            }
        }
        if (p.isReady()) {
            for (Player player : readyPlayers) {
                if (player.equals(p)) {
                    readyPlayers.remove(player);
                    break;
                }
            }
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
                ClientHandler client = new ClientHandler(s, clients.size() + 1);
                clients.add(client);
                System.out.println("[SERVER] Client " + client.getId() + " connected");
                pool.execute(client);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Server");
            }
        }
    }

    public static void showToEveryone(boolean ready) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        List<String> names = new ArrayList<>();
        if (!ready) {
            map.put("message", "PlayersList");
            for (Player p : playersList) {
                names.add(p.getName() + "#" + p.getId());
            }
        } else {
            map.put("message", "ReadyPlayers");
            for (Player p : readyPlayers) {
                names.add(p.getName() + "#" + p.getId());
            }
        }
        map.put("list", names);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
    }

    public static boolean checkIfEveryoneIsReady() {
        int n = playersList.size();
        if (n != readyPlayers.size()) return false;
        int acc = 0;
        for (int i = 0; i < n; i++) {
            if (readyPlayers.contains(playersList.get(i))) acc++;
        }
        return acc == n;
    }

    public static void disconnect(ClientHandler cl) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "Disconnect");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            if (cl == client) {
                PrintWriter out = new PrintWriter(cl.getSocket().getOutputStream(), true);
                out.println(s);
                break;
            }
        }
    }

    public static void startGame() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "ShowStart");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
    }
    
    public static void main(String[] args) {
        new Thread(SERVER).start();
    }

}