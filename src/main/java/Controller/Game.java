package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import misc.GameSettings;
import misc.Global;
import misc.Word;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Game {
    private boolean gamestate = false;
    private ArrayList<Word> dictionary = new ArrayList<>();
    private ArrayList <Word> words = new ArrayList<>();
    @FXML
    private Label text = new Label();
    @FXML
    private Scene scene;
    private Stage stage;
    @FXML
    private TextField input = new TextField();

    public ArrayList<Word> getDictionary() {
        return dictionary;
    }

    public ArrayList<Word> getWords() {
        return words;
    }

    public Label getText() {
        return text;
    }

    public void setText(Label text) { this.text = text; }

    public TextField getInput() {
        return input;
    }

    public boolean getGamestate() { return gamestate; }

    public void setGamestate(boolean gamestate) { this.gamestate = gamestate; }

    public void setTextFieldColor(){
        Color color = Color.web("#95d5b2");
        BackgroundFill background_fill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        getInput().setBackground(background);
    }
    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void remakeList() {
        words.clear();
        Stream<Word> stream;
        if (GameSettings.getDifficultyName().equals("Easy")){
            stream = Stream.generate(() -> dictionary.get((int) (Math.random() * dictionary.size()))).limit(GameSettings.getWords_max_length());
        } else {
            stream = Stream.generate(() -> dictionary.get((int) (Math.random() * dictionary.size()))).filter(s -> s.toString().length() > 4).limit(GameSettings.getWords_max_length());
        }
        stream.forEach(s -> {
            words.add(s);
            text.setText(text.getText() + s + " ");
        });
    }

    public void setNewDictionary(){
        try {
            dictionary.clear();
            BufferedReader in = new BufferedReader(new FileReader(GameSettings.getDifficulty()));
            String word;
            while ((word = in.readLine()) != null) {
                dictionary.add(new Word(word));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
