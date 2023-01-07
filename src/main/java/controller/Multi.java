package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import static misc.Global.*;
import misc.Global;
import misc.Word;

public class Multi extends Game {

    private int health = DEFAULT_HEALTH;
    private int wordCount = DEFAULT_WORD_COUNT;
    private int score = 0;

    @FXML private Label healthlbl = new Label();
    @FXML private Label wordsLeft = new Label();
    @FXML private TextFlow textFlow = new TextFlow();
    @FXML private TextFlow podium = new TextFlow();
    @FXML private Label title = new Label();
    @FXML private Label over = new Label();
    @FXML private TextFlow results;
    @FXML private Button restart;
    @FXML private GridPane multi;
    @FXML private GridPane endgame;

    // Set the game, loads data and displays it, input listener
    public void initialize() {
        Lobby.getConnection().setMulti(this);
        setTextFieldColor();
        setNewDictionary();
        remakeList();
        displayList();
        wordsLeft.setText("" + getWords().size());
        healthlbl.setText("" + health);
        getInput().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!getWords().isEmpty()) {
                if (newValue.length() <= getWords().get(0).toString().length()) {
                    if (!newValue.equals(getWords().get(0).toString().substring(0, newValue.length()))) {
                        getInput().setStyle("-fx-text-fill: #e83e3e;");
                    } else {
                        getInput().setStyle("-fx-text-fill: #383734;");
                    }
                } else {
                    getInput().setStyle("-fx-text-fill: #e83e3e;");
                }
            }
        });
        try {
            Thread.sleep(1000);
            this.podiumRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("InterruptedException Multi");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException Multi"); 
        }
    }

    // Display the words with the right color
    public void displayList(){
        textFlow.getChildren().clear();
        textFlow.setTextAlignment(TextAlignment.CENTER);;
        for(Word word: getWords()) {
            Text text = new Text(word.toString() + " ");
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            if (word.getType() == 'b') {
                text.setFill(Color.web("#95d5b2"));
            } else if (word.getType() == 'm') {
                text.setFill(Color.web("#ff1111"));
            }  else {
                text.setFill(Color.web("#383734"));
            }
            textFlow.getChildren().add(text);
        }
    }

    // Return number of difference between two words with different length
    public int compareWords(String word1, String word2){
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

    // Do the action when the user presses space
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false) {
            setGamestate(true);
        }
        if (event.getCode() == KeyCode.SPACE && !getWords().isEmpty()) {
            String word = getInput().getText();
            try {
                if (!getWords().get(0).toString().equals(word)) {
                    health -= compareWords(getWords().get(0).toString(), word);
                    healthlbl.setText(""+health);
                    if (health <= 0) this.gameOver();
                } else {
                    score += word.length();
                    if (getWords().get(0).getType() == 'b') {
                        health += word.length();
                        healthlbl.setText("" + health);
                    } else if (getWords().get(0).getType() == 'm') {
                            this.sendWord(word.toString());
                    }
                } 
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IOException Multi");
            }
            wordCount++;
            if (wordCount == Global.WORDS_TO_LEVEL_UP) {
                wordCount = 0;
            }
            getWords().remove(0);
            if (getWords().size() < Global.MAX_LIST_SIZE_MULTI/2) {
                getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            }
            wordsLeft.setText("" + getWords().size());
            getInput().setStyle("-fx-text-fill: #383734");
            //refresh the text
            displayList();
            getInput().clear();
            getInput().setEditable(false);
        }
    }

    // Remove the space after confirming the word
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            getInput().clear();
            getInput().setEditable(true);
            event.consume();
        }
    }

    private void sendWord(String word) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "SendWord");
        map.put("word", word);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
    }

    public void addWordFromUser(String word) {
        Platform.runLater(() -> {
            if(getWords().size() == Global.MAX_LIST_SIZE_MULTI) {
                // forcer la validation du mot courant :
                String word2 = getInput().getText();
                if (!getWords().get(0).toString().equals(word2)) {
                    // perte de vie :
                    health -= compareWords(getWords().get(0).toString(), word2);
                    getWords().remove(0);
                    // health -= getWords().get(0).toString().length();
                    healthlbl.setText("" + health);
                }
                // Game over : 
                if (health <= 0) {
                    try {
                        this.gameOver();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("IOException Multi");
                    }
                }
            }
            getWords().add(new Word(word, 'n'));
            displayList();
            getInput().clear();
            wordsLeft.setText("" + getWords().size());
        });
    }

    public void gameOver() throws IOException {
        getInput().setVisible(false);
        over.setVisible(true);
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "GameOver");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
        podiumRequest();
    }

    public void drawPodium(List<String> names) {
        Platform.runLater(() -> {
            podium.getChildren().clear();
            for (String name : names) {
                Text t = new Text(name);
                t.setStyle("-fx-font-size: 30px;");
                t.setStyle("-fx-font-weight: bold;");
                t.setStyle("-fx-font-color: #ffffff;");
                podium.getChildren().add(t);
            }
        });
    }

    public void podiumRequest() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "PodiumRequest");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
    }

    public void endGame(List<String> podium) {
        this.multi.setVisible(false);
        this.endgame.setVisible(true);
        this.drawResults(podium);
    }

    public void drawResults(List<String> names) {
        Platform.runLater(() -> {
            results.getChildren().clear();
            for (String name : names) {
                Text t = new Text(name);
                t.setStyle("-fx-font-size: 30px;");
                t.setStyle("-fx-font-weight: bold;");
                t.setStyle("-fx-font-color: #ffffff;");
                results.getChildren().add(t);
            }
        });
    }

    public void restart_game(ActionEvent event) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "Replay");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
    }

    public void replay() {
        this.score = 0;
        this.health = DEFAULT_HEALTH;
        try {
            Thread.sleep(1000);
            this.podiumRequest();
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.err.println("InterruptedException Multi");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("IOException Multi"); 
        }
        this.multi.setVisible(true);
        this.endgame.setVisible(false);
        this.over.setVisible(false);
        getInput().setVisible(true);
        getInput().clear();
        getInput().setEditable(true);
    }

}