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

import misc.Global;
import network.Server;
import network.ServerConnection;

public class Lobby {
    
    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private Button start;
    @FXML private VBox vbox;

    private ServerConnection connection;

    public VBox getVbox() {return vbox;}

    @FXML
    public void initialize() {
        try {
            Socket socket = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            this.connection = new ServerConnection(socket, this);
            new Thread(connection).start();
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
            Gson gson = new Gson();
            map.put("message", "Connection");
            map.put("pseudo", "Player #");
            String message = gson.toJson(map);
            out.println(message);
        } catch (SocketException se) {
            System.out.println("[CLIENT] Client disconnected");
            Thread.interrupted();
        } catch (IOException io) {
            io.printStackTrace();
            System.err.println("Lobby IOException");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.err.println("displayPlayer() Error");
        }
    }

    public void start_multi() {
        // isReady = true;
        System.out.println("start");
    }

    public void back(ActionEvent event) throws IOException {
        // mode_multi = false;
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
    
}