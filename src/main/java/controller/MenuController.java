package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

import misc.Global;

public class MenuController {

    @FXML
    private Button solobutton;
    @FXML
    private Button multibutton;
    @FXML
    private Button settingsbutton;
    @FXML
    private Button quitbutton;
    @FXML
    private Scene scene;
    private Stage stage;


/**
 * When the user clicks the button, the program will exit.
 */
    public void click(){
        System.exit(0);
    }

/**
 * If the mouse is hovering over a button, change the color of the button
 * 
 * @param event The event that triggered the handler.
 */
    @FXML
    public void handleButtonHover(MouseEvent event){
        if(event.getSource() == solobutton) {
            solobutton.setStyle("-fx-background-color: #b7e4c7");
        } else if (event.getSource() == multibutton) {
            multibutton.setStyle("-fx-background-color: #ee964b");
        } else if (event.getSource() == settingsbutton) {
            settingsbutton.setStyle("-fx-background-color: #b7e4c7");
        } else if (event.getSource() == quitbutton) {
            quitbutton.setStyle("-fx-background-color: #ee964b");
        }
    }

/**
 * When the mouse leaves a button, the button's color changes to the color it was before the mouse
 * entered it
 * 
 * @param event The event that triggered the handler.
 */
    @FXML
    public void handleButtonLeave(MouseEvent event){
        if(event.getSource() == solobutton) {
            solobutton.setStyle("-fx-background-color:  #95d5b2");
        } else if (event.getSource() == multibutton) {
            multibutton.setStyle("-fx-background-color:  #e76f51");
        } else if (event.getSource() == settingsbutton) {
            settingsbutton.setStyle("-fx-background-color:  #95d5b2");
        } else if (event.getSource() == quitbutton) {
            quitbutton.setStyle("-fx-background-color:  #e76f51");
        }
    }

/**
 * It loads the game.fxml file and sets the scene to the game.fxml file
 * 
 * @param event The event that triggered the method.
 */
    public void switchToScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It loads the lobby.fxml file and sets the scene to the lobby.fxml file
 * 
 * @param event The event that triggered the method.
 */
    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../lobby.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It loads the settings.fxml file and sets the scene to the settings.fxml file
 * 
 * @param event The event that triggered the method.
 */
    public void switchToScene3(ActionEvent event) {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("../settings.fxml"));
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                scene = new Scene(root);
                stage.setTitle(Global.GAME_TITLE);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IO Exception Menu"); 
            }
        });
 
    }

}