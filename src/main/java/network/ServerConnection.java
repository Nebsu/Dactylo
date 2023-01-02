package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import javafx.application.Platform;
import javafx.scene.text.Text;

import controller.Lobby;

@SuppressWarnings("unchecked")

public class ServerConnection implements Runnable {

    private Socket socket;
    private Lobby lobby;
    private BufferedReader in;
    private List<String> playersNamesList;
    
    public ServerConnection(Socket socket, Lobby lobby) throws IOException {
        this.socket = socket;
        this.lobby = lobby;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public List<String> getPlayersNamesList() {return this.playersNamesList;}

    @Override
    public void run() {
        try {
            while (true) {
                String request = in.readLine();
                Gson gson = new Gson();
                LinkedTreeMap<String, Object> map = gson.fromJson(request, LinkedTreeMap.class);
                String message = (String) map.get("message");
                if (message.equals("PlayersList")) {
                    this.playersNamesList = (List<String>) map.get("list");
                    this.drawNames();
                } else if (message.equals("ReadyPlayers")) {
                    this.playersNamesList = (List<String>) map.get("list");
                    this.showReadyPlayers();
                } else if (message.equals("Quit")) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ServerConnection IOException");
        } finally {
            try {
                in.close();
                this.socket.close();
                Thread.interrupted();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ServerConnection IOException");
            }
        }
    }

    private void drawNames() {
        Platform.runLater(() -> {
            lobby.getVbox().getChildren().clear();
            for (String name : playersNamesList) {
                lobby.getVbox().getChildren().add(new Text(name));
            }
        });
    }

    private void showReadyPlayers() {
        Platform.runLater(() -> {

        });
    }

}