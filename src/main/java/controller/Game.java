package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import misc.GameSettings;
import misc.Global;
import misc.Word;

public sealed class Game permits TrainingGame, Solo, Multi {
    
    private boolean gamestate = false;
    private final ArrayList<Word> dictionary = new ArrayList<>();
    private final ArrayList <Word> words = new ArrayList<>();

    @FXML private TextFlow textFlow = new TextFlow();
    @FXML private Scene scene;
    @FXML private Stage stage;
    @FXML private TextField input = new TextField();

    public final ArrayList<Word> getDictionary() {
        return dictionary;
    }

    public final ArrayList<Word> getWords() {
        return words;
    }

    public final TextFlow getText() {
        return textFlow;
    }

    public final void setText(TextFlow text) { this.textFlow = text; }

    public final TextField getInput() {
        return input;
    }

    public final boolean getGamestate() { return gamestate; }

    public final void setGamestate(boolean gamestate) { this.gamestate = gamestate; }

/**
 * It sets the background color of the text field
 */
    public final void setTextFieldColor(){
        Color color = Color.web("#95d5b2");
        BackgroundFill background_fill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        getInput().setBackground(background);
    }

/**
 * It loads the menu.fxml file and sets it as the current scene
 * 
 * @param event The event that triggered the method.
 */
    public final void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It changes the scene to the game scene
 * 
 * @param event The event that triggered the method.
 */
    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It generates a random word from the dictionary, and adds it to the list of words
 */
    public final void remakeList() {
        words.clear();
        Stream<Word> stream;
        if (GameSettings.getDifficultyName().equals("Easy")){
            stream = Stream.generate(() -> dictionary.get((int) (Math.random() * dictionary.size()))).limit(GameSettings.getWords_max_length());
        } else {
            stream = Stream.generate(() -> dictionary.get((int) (Math.random() * dictionary.size()))).filter(s -> s.toString().length() > 4).limit(GameSettings.getWords_max_length());
        }
        stream.forEach(s -> {
            words.add(s);
        });
    }

/**
 * It reads a file and adds each line to a list
 */
    public final void setNewDictionary(){
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

/**
 * This function takes a string as an argument and sets the text of the TextFlow object to the string.
 * 
 * @param newText The text to be displayed in the TextFlow.
 */
    public final void setNewText(String newText) {
        Text text = new Text(newText);
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            text.setFill(Color.web("#383734"));
            textFlow.getChildren().clear();
            textFlow.getChildren().add(text);
    }

/**
 * It takes a list of words, and displays them in a TextFlow object
 */
    public final void displayList(){
        textFlow.getChildren().clear();
        textFlow.setTextAlignment(TextAlignment.CENTER);;
        for(Word word: getWords()) {
            Text text = new Text(word.toString() + " ");
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            if (word.getType() == 'b') {
                text.setFill(Color.web("#95d5b2"));
            }  else {
                text.setFill(Color.web("#383734"));
            }
            textFlow.getChildren().add(text);
        }
    }

    /**
     * It takes a list of words, and for each word, it creates a Text object, sets the font, and adds
     * it to a TextFlow object
     */
    public final void displayMalus(){
        textFlow.getChildren().clear();
        textFlow.setTextAlignment(TextAlignment.CENTER);;
        for(Word word: getWords()) {
            Text text = new Text(word.toString() + " ");
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            if (word.getType() == 'b') {
                text.setFill(Color.web("#95d5b2"));
            } else if (word.getType() == 'm') {
                text.setFill(Color.web("#ff0000"));
            } else {
                text.setFill(Color.web("#383734"));
            }
            textFlow.getChildren().add(text);
        }
    }

    /**
     * It compares two words and returns the number of differences between them
     * 
     * @param word1 The first word to compare
     * @param word2 the word that you want to compare to
     * @return The difference between the two words.
     */
    public final int compareWords(String word1, String word2){
        int difference = 0;
        if(word1.length() > word2.length()){
            for(int i = 0; i < word2.length(); i++){
                if(word1.charAt(i) != word2.charAt(i)){
                    difference++;
                }
            }
            difference += word1.length() - word2.length();
        } else {
            for(int i = 0; i < word1.length(); i++){
                if(word1.charAt(i) != word2.charAt(i)){
                    difference++;
                }
            }
            difference += word2.length() - word1.length();
        }
        return difference;
    }
}