package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

public final class Server implements Runnable {
    
    private static String SERVER_IP = "kyan.sportday.fr"; // default IP
    public static final int SERVER_PORT = 4999; // default port
    public static final Server SERVER = initServer(SERVER_PORT);
    private final ServerSocket ss;
    private boolean running = false;
    private boolean inGame = false;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final ExecutorService pool = Executors.newFixedThreadPool(10); // thread pool
    private final List<Player> playersList = new ArrayList<>();
    private final List<Player> readyPlayers = new ArrayList<>();
    private final List<Player> alivePlayers = new ArrayList<>();
    private final List<Player> podium = new ArrayList<>(); // final results

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

    public List<Player> getPlayersList() {return new ArrayList<>(playersList);}

    public List<Player> getReadyPlayers() {return new ArrayList<>(readyPlayers);}

    public static String getServerIP() {return SERVER_IP;}

    public static void setServerIP(String serverIP) {
        SERVER_IP = serverIP;
        System.out.println("[SERVER] Server New IPV4 Adress : " + SERVER_IP);
    }

    public void addPlayer(Player p) {
        playersList.add(p);
    }

    public void addReadyPlayer(Player p) {
        readyPlayers.add(p);
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
                if (inGame) {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println("[SERVER] You are not allowed to join !\nGame is already started");
                    s.close();
                    continue;
                }
                if (clients.size() == 10) {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    out.println("[SERVER] You are not allowed to join !\nServer is full");
                    s.close();
                    continue;
                }
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

/**
 * It sends a message to all the clients to start the game
 * 
 * @param isReplay boolean
 */
    public void runMultiplayerGame(boolean isReplay) throws IOException {
        System.out.println("\n[SERVER] MULTIPLAYER GAME STARTED");
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        if (!isReplay) map.put("message", "Begin");
        else map.put("message", "Replay");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
        for (Player p : readyPlayers) {
            alivePlayers.add(p);
        }
        this.inGame = true;
        this.podium.clear();
    }

/**
 * It takes a list of players, converts it to a JSON string, and sends it to all the clients
 */
    public void showToEveryone() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        List<String> names = new ArrayList<>();
        map.put("message", "PlayersList");
        for (Player p : playersList) {
            names.add(p.getName() + p.getId()+"\n\n");
        }
        map.put("list", names);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
    }

/**
 * It takes a list of players, converts it to a JSON string, and sends it to all the clients
 */
    public void updatePodium() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "Alive");
        List<String> names = new ArrayList<>();
        for (int i=0; i<alivePlayers.size(); i++) {
            names.add(alivePlayers.get(i).getName() + alivePlayers.get(i).getId() + "\n");
        }
        map.put("names", names);   
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
    }

/**
 * It sends a word to everyone except the sender
 * 
 * @param word the word to send
 * @param sender the client who sent the word
 */
    public void sendWordToEveryone(String word, ClientHandler sender) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "GetWord");
        map.put("word", word);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            if (client != sender) { // on n'envoie pas le mot à soi-même :
                PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
                out.println(s);
            }
        }
    }

/**
 * It removes a player from the list of alive players, adds it to the podium, and if there's only one
 * player left, it ends the game
 * 
 * @param player the player that is being killed
 */
    public void killPlayer(Player player) throws IOException {
        alivePlayers.remove(player);
        player.kill();
        podium.add(player);
        if (alivePlayers.size() == 1) {
            System.out.println("\n[SERVER] GAME ENDED");
            podium.add(alivePlayers.get(0));
            Collections.reverse(podium);
            try {
                // show end creen and final podium :
                this.endGame();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Server");
            }
        }
    }

/**
 * It sends a message to all the clients that the game has ended, and then clears the list of players
 * who are ready to play and the list of players who are alive
 */
    public void endGame() throws IOException {
        // Signal for end screen :
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "End");
        List<String> names = new ArrayList<>();
        for (int i=0; i<podium.size(); i++) {
            names.add("#" + (i+1) + " " + podium.get(i).getName() + podium.get(i).getId() + "\n\n");
        }
        map.put("podium", names);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        for (ClientHandler client : clients) {
            PrintWriter out = new PrintWriter(client.getSocket().getOutputStream(), true);
            out.println(s);
        }
        inGame = false;
        readyPlayers.clear();
        alivePlayers.clear();
    }

/**
 * This function is called when the server is shutting down. It sets the running variable to false,
 * shuts down the thread pool, and closes the server socket
 */
    public void shutdown() {
        try {
            this.running = false;
            this.pool.shutdown();
            this.ss.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IO Exception Server");
        }
    }

/**
 * If the number of ready players is less than or equal to one, return false. Otherwise, if the number of
 * ready players is equal to the number of players, return true.
 * @return The method returns a boolean value.
 */
    public boolean checkIfEveryoneIsReady() {
        if (readyPlayers.size() <= 1) return false;
        int n = playersList.size();
        if (n != readyPlayers.size()) return false;
        int acc = 0;
        for (int i = 0; i < n; i++) {
            if (readyPlayers.contains(playersList.get(i))) acc++;
        }
        return acc == n;
    }

    public static void main(String[] args) {
        new Thread(SERVER).start();
    }

}