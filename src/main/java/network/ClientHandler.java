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

public class ClientHandler implements Runnable {

    private Socket socket;
    private int id;
    private BufferedReader in;

    public ClientHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        this.id = id;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {return this.socket;}
    public int getId() {return this.id;}
 
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
                    Player p = new Player(name, this.id);
                    Global.PLAYER.setId(id);
                    // System.out.println(p.getName());
                    SERVER.addPlayer(p);
                    SERVER.showToEveryone();
                } else if (message.equals("Ready")) {
                    String name = (String) map.get("pseudo");
                    Player p = new Player(name, this.id);
                    p.setReady(true);
                    System.out.println("[SERVER] Player"+ this.id + " is ready");
                    SERVER.addReadyPlayer(p);
                    if (SERVER.checkIfEveryoneIsReady())
                        SERVER.runMultiplayerGame();
                } else if (message.equals("Quit")) {
                    String name = (String) map.get("pseudo");
                    Player p = new Player(name, this.id);
                    SERVER.removePlayer(p);
                    SERVER.showToEveryone();
                    SERVER.disconnect(this);
                    break;
                } else if (message.equals("SendWord")) {
                    String word = (String) map.get("word");
                    System.out.println(word);
                    SERVER.sendWordToEveryone(word, this);
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
                Thread.interrupted();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Client Handler");
            }
        }
    }

}