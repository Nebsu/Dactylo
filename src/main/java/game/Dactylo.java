package game;

import client.Client;
import server.Server;
import packets.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dactylo extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Dactylo.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 600);
        stage.setTitle("Hello!");
        stage.show();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
        stage.setOnCloseRequest( event -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
        Server server = new Server(5000);
		server.start();
        
        Client client = new Client("localhost",5000);
		client.connect();
		
		AddConnectionPacket packet = new AddConnectionPacket();
		client.sendObject(packet);
    }
}