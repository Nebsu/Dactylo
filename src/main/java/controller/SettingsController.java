package controller;

import javafx.scene.control.TextField;
import misc.GameSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;

import misc.Global;
import network.Server;

public class SettingsController {

    private String selectedOption;
    private int selectedOption2;

    @FXML private Stage stage;
    @FXML private Scene scene;
    @FXML private ChoiceBox<String> choiceBox = new ChoiceBox<>();
    @FXML private ChoiceBox<Integer> choiceBox2 = new ChoiceBox<>();
    @FXML private TextField usernameTa;
    @FXML private TextField ipTextField;

/**
 * It sets the text of the text area to the username, and then it adds a listener to the text area so
 * that when the text is changed, the username is changed to the new text
 */
    @FXML
    public void initialize() {
        usernameTa.setText(GameSettings.getUsername());
        usernameTa.textProperty().addListener((observable, oldValue, newValue) -> {
            GameSettings.setUsername(newValue);
            usernameTa.setText(newValue);
        });
        choiceBox.getItems().addAll("Easy", "Medium", "Hard");
        choiceBox.setValue(GameSettings.getDifficultyName());
        choiceBox.setOnAction(event -> {
            selectedOption = choiceBox.getValue();
            GameSettings.setDifficulty(selectedOption);
            GameSettings.setDifficultyName(selectedOption);
        });
        choiceBox2.getItems().addAll(15, 20, 25, 30);
        choiceBox2.setValue(GameSettings.getWords_max_length());
        choiceBox2.setOnAction(event -> {
            selectedOption2 = choiceBox2.getValue();
            GameSettings.setWords_max_length(selectedOption2);
        });
        ipTextField.setText(Server.getServerIP());
        ipTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            Server.setServerIP(newValue);
            ipTextField.setText(newValue);
        });
    }

/**
 * It loads the menu.fxml file and sets the scene to the menu.fxml file
 * 
 * @param event The event that triggered the method.
 */
    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It switches the scene to the leaderboard.fxml file
 * 
 * @param event The event that triggered the method.
 */
    public void switchToLeaderboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../leaderboard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
    
}