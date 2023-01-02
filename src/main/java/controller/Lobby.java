package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import static misc.Global.*;
import misc.Global;
import network.Server;
import network.ServerConnection;

public class Lobby {
    
    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Button ready;
    @FXML private VBox vbox;

    private Socket socket;
    private PrintWriter out;
    private ServerConnection connection;
    private static boolean visited = false;


    public VBox getVbox() {return vbox;}

    @FXML
    public void initialize() {
        try {
            if (visited) return;
            visited = true;
            this.socket = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.connection = new ServerConnection(socket, this);
            new Thread(connection).start();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
            Gson gson = new Gson();
            map.put("message", "Connection");
            map.put("pseudo", PLAYER.getName());
            String message = gson.toJson(map);
            out.println(message);
        } catch (SocketException se) {
            System.out.println("[CLIENT] Client disconnected");
            Thread.interrupted();
        } catch (IOException io) {
            io.printStackTrace();
            System.err.println("Lobby IOException");
        }
    }

    public void ready_to_play() {
        PLAYER.setReady(true);
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        Gson gson = new Gson();
        map.put("message", "Ready");
        map.put("pseudo", PLAYER.getName());
        String message = gson.toJson(map);
        out.println(message);
        System.out.println("Ready to play");
    }

    public void start_multi(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../multi.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void back(ActionEvent event) throws IOException {
        if (PLAYER.isReady()) this.quitLobby();
        PLAYER.setReady(false);
        Parent root = FXMLLoader.load(getClass().getResource("../menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
    
    private void quitLobby() {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        Gson gson = new Gson();
        map.put("message", "Quit");
        map.put("pseudo", PLAYER.getName());
        String message = gson.toJson(map);
        out.println(message);
        System.out.println("You quit the online lobby");
        try {
            out.close();
            socket.close();
        } catch (IOException io) {
            io.printStackTrace();
            System.err.println("Lobby IOException");
        }
    }

}