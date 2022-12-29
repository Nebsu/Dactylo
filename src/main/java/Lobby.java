import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;

import com.google.gson.Gson;

public class Lobby {
    
    @FXML
    private Stage stage;
    private Scene scene;
    @FXML
    private Button start;
    @FXML
    private Label player1;
    private Label player2;
    private Label player3;
    private Label player4;
    private Label player5;
    private Label player6;
    private Label player7;
    private Label player8;
    private Label player9;
    private Label player10;

    @FXML
    public void initialize() {
        try {
            Socket s = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            Socket socket = new Socket(Server.SERVER_IP, Server.SERVER_PORT);
            ServerConnection connection = new ServerConnection(socket);
            new Thread(connection).start();
        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("Oh no ...");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            System.out.println("displayPlayer() Error");
        }
    }

    public void displayPlayer(ClientHandler c) {
        switch (c.getId()) {
            case 1: paintPlayer(c, player1);
            case 2: paintPlayer(c, player2);
            case 3: paintPlayer(c, player3);
            case 4: paintPlayer(c, player4);
            case 5: paintPlayer(c, player5);
            case 6: paintPlayer(c, player6);
            case 7: paintPlayer(c, player7);
            case 8: paintPlayer(c, player8);
            case 9: paintPlayer(c, player9);
            case 10: paintPlayer(c, player10);
        }
        throw new IllegalStateException("At least 2 players and maximum 10 players");
    }

    public void paintPlayer(ClientHandler cl, Label l) {
        l.setStyle("-fx-text-fill: #FFFFFF");
        l.setTextAlignment(TextAlignment.CENTER);
        l.setText(cl.toString());
    }

    public void start_multi() {
        System.out.println("start");
        Gson gson = new Gson();
        Boolean b = true;
        String json = gson.toJson(b);
    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
    
}