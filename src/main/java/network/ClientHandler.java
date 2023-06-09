package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import misc.Global;
import static network.Server.SERVER;

@SuppressWarnings("unchecked")

public final class ClientHandler implements Runnable {

    private final Socket socket;
    private final int id;
    private final BufferedReader in;
    private Player player;

    public ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {return this.socket;}
    public int getId() {return this.id;}
 
    /**
     * It reads the request from the client, and then depending on the request does the corresponding action.
     */
    @Override
    public void run() {
        try {
            String request;
            while ((request = in.readLine()) != null) {
                Gson gson = new Gson();
                LinkedTreeMap<String, Object> map = gson.fromJson(request, LinkedTreeMap.class);
                String message = (String) map.get("message");
                if (message.equals("Connection")) {
                    String name = (String) map.get("pseudo");
                    this.player = new Player(name, this.id);
                    Global.PLAYER.setId(this.id);
                    SERVER.addPlayer(this.player);
                    SERVER.showToEveryone();
                } else if (message.equals("Ready")) {
                    this.player.setReady(true);
                    System.out.println("[SERVER] Player"+ this.id + " is ready");
                    SERVER.addReadyPlayer(this.player);
                    if (SERVER.checkIfEveryoneIsReady())
                        SERVER.runMultiplayerGame(false);
                } else if (message.equals("PodiumRequest")) {
                    SERVER.updatePodium();
                } else if (message.equals("SendWord")) {
                    String word = (String) map.get("word");
                    System.out.println(word);
                    SERVER.sendWordToEveryone(word, this);
                } else if (message.equals("GameOver")) {
                    SERVER.killPlayer(this.player);
                } else if (message.equals("Replay")) {
                    System.out.println("[SERVER] Player"+ this.id + " is ready");
                    SERVER.addReadyPlayer(this.player);
                    if (SERVER.checkIfEveryoneIsReady())
                        SERVER.runMultiplayerGame(true);
                }
            }
        } catch (SocketException se) {
            System.out.println("[SERVER] Client " + id + " disconnected");
        } catch (IOException e) {
            System.err.println("IO Exception Client Handler");
            System.err.println(e.getStackTrace());
        } finally {
            try {
                in.close();
                this.socket.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Client Handler");
            }
        }
    }

}