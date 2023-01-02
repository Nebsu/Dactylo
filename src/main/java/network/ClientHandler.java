package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import misc.Global;

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
                    Player p = new Player(name, id);
                    Global.PLAYER.setId(id);
                    // System.out.println(p.getName());
                    Server.addPlayer(p);
                    Server.showToEveryone(false);
                } else if (message.equals("Ready")) {
                    String name = (String) map.get("pseudo");
                    String id = (String) map.get("id");
                    int idInt = Integer.parseInt(id);
                    Player p = new Player(name, idInt);
                    p.setReady(true);
                    System.out.println("[SERVER] "+ Global.PLAYER + " is ready");
                    Server.addReadyPlayer(p);
                    Server.showToEveryone(true);
                } else if (message.equals("Quit")) {
                    String name = (String) map.get("pseudo");
                    String id = (String) map.get("id");
                    int idInt = Integer.parseInt(id);
                    Player p = new Player(name, idInt);
                    p.setReady(true);
                    Server.removePlayer(p);
                    Server.showToEveryone(false);
                    Server.showToEveryone(true);
                    Server.disconnect(this);
                    break;
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