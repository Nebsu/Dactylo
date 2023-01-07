package network;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import controller.Lobby;
import controller.Multi;

@SuppressWarnings("unchecked")

public class ServerConnection implements Runnable {

    private final Socket socket;
    private final BufferedReader in;
    private final Lobby lobby;
    private Multi multi;
    
    public ServerConnection(Socket socket, Lobby lobby) throws IOException {
        this.socket = socket;
        this.lobby = lobby;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {return this.socket;}
    public Lobby getLobby() {return this.lobby;}
    public void setMulti(Multi m) {multi = m;}

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                Gson gson = new Gson();
                LinkedTreeMap<String, Object> map = gson.fromJson(request, LinkedTreeMap.class);
                String message = (String) map.get("message");
                if (message.equals("PlayersList")) {
                    List<String> playersNamesList = (List<String>) map.get("list");
                    lobby.drawNames(playersNamesList);
                } else if (message.equals("Begin")) {
                    Platform.runLater( () -> {
                        try {
                            this.lobby.startMulti();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("ServerConnection IOException");
                        }
                    });
                } else if (message.equals("GetWord")) {
                    String word = (String) map.get("word");
                    System.out.println(word);
                    multi.addWordFromUser(word);
                } else if (message.equals("Alive")) {
                    List<String> alivePlayers = (List<String>) map.get("names");
                    multi.drawPodium(alivePlayers);
                } else if (message.equalsIgnoreCase("End")) {  
                    List<String> results = (List<String>) map.get("podium");
                    multi.endGame(results);
                } else if (message.equals("Replay")) {
                    multi.replay();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ServerConnection IOException");
        } finally {
            try {
                in.close();
                this.socket.close();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ServerConnection IOException");
            }
        }
    }

}