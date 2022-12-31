package controller;

import javafx.scene.control.TextArea;
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
import misc.Global;

import java.io.IOException;

public class SettingsController {

    private String selectedOption;
    private int selectedOption2;
    @FXML
    private Stage stage;
    private Scene scene;
    @FXML
    private ChoiceBox<String> choiceBox = new ChoiceBox<>();
    @FXML
    private ChoiceBox<Integer> choiceBox2 = new ChoiceBox<>();
    @FXML
    private TextField usernameTa;

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
    }
    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToLeaderboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../leaderboard.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}
