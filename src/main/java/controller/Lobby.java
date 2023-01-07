package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
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
    @FXML private TextFlow flow;

    private Socket socket;
    private PrintWriter out;
    private static ServerConnection connection;
    private static boolean visited = false;
    private static ActionEvent start;

    public static Scene getScene() {return connection.getLobby().scene;}
    public static ServerConnection getConnection() {return connection;}

    @FXML
    public void initialize() {
        while (!visited) {
            try {
                this.socket = new Socket(Server.getServerIP(), Server.SERVER_PORT);
                this.out = new PrintWriter(socket.getOutputStream(), true);
                connection = new ServerConnection(socket, this);
                new Thread(connection).start();
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
                Gson gson = new Gson();
                map.put("message", "Connection");
                map.put("pseudo", PLAYER.getName());
                String message = gson.toJson(map);
                out.println(message);
                visited = true;
            } catch (SocketException se) {
                System.out.println("[CLIENT] Client disconnected");
            } catch (IOException io) {
                io.printStackTrace();
                System.err.println("Lobby IOException");
            }
        }
    }

    public void ready_to_play(ActionEvent event) throws IOException {
        PLAYER.setReady(true);
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        Gson gson = new Gson();
        map.put("message", "Ready");
        String message = gson.toJson(map);
        out.println(message);
        System.out.println("Ready to play");
        start = event;
        this.ready.setVisible(false);
    }

    public void startMulti() throws IOException {
        ActionEvent event = start;
        Parent root = FXMLLoader.load(getClass().getResource("../multi.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void drawNames(List<String> playersNamesList) {
        Platform.runLater(() -> {
            this.flow.getChildren().clear();
            for (String name : playersNamesList) {
                Text t = new Text(name);
                t.setStyle("-fx-font-size: 30px;");
                t.setStyle("-fx-font-weight: bold;");
                t.setStyle("-fx-font-color: #ffffff;");
                this.flow.getChildren().add(t);
            }
        });
    }

}