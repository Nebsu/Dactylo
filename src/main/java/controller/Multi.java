package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.util.List;
import java.util.Random;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import static misc.Global.*;
import misc.Global;
import misc.Word;

public final class Multi extends Game {

    private int health = DEFAULT_HEALTH;
    private int wordCount = DEFAULT_WORD_COUNT;
    private boolean isNewWord = false;
    private boolean isDead = false;

    @FXML private Label healthlbl = new Label();
    @FXML private Label wordsLeft = new Label();
    @FXML private TextFlow podium = new TextFlow();
    @FXML private Label title = new Label();
    @FXML private Label over = new Label();
    @FXML private TextFlow results;
    @FXML private Button restart;
    @FXML private GridPane multi;
    @FXML private GridPane endgame;
    

/**
 * It sets the color of the textfield, sets a new dictionary, makes a new list, displays the malus,
 * sets the words left, sets the health, and then it requests the list of alive players
 */
    public void initialize() {
        Lobby.getConnection().setMulti(this);
        setTextFieldColor();
        setNewDictionary();
        remakeList();
        displayMalus();
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

/**
 * It checks if the user has pressed the space bar, if so, it checks if the word is correct, if not, it
 * deducts health, if so, it adds score
 * 
 * @param event the key event
 */
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
                    if (health <= 0 && !isDead) this.gameOver();
                } else {
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
            displayMalus();
            getInput().clear();
            isNewWord = true;
        }else if (event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.BACK_SPACE && event.getCode() != KeyCode.ESCAPE && getGamestate()) {
            //If the user is typing a new word, clear the text
            if (isNewWord) {
                getInput().clear();
                isNewWord = false;
            }
        }
    }

/**
 * It sends a message to the server to send a word to the other player
 * 
 * @param word The word that the player has guessed
 */
    public void sendWord(String word) throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "SendWord");
        map.put("word", word);
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
    }

/**
 * It adds a word to the list of words to type, and if the list is full, it checks if the word typed by
 * the user is the same as the first word in the list, and if not, it removes the first word in the
 * list and subtracts the number of letters in the word from the user's health
 * 
 * @param word the word to add to the list
 */
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
                if (health <= 0 && !isDead) {
                    try {
                        this.gameOver();
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("IOException Multi");
                    }
                }
                getInput().clear();
            }
            getWords().add(new Word(word, 'n'));
            displayMalus();
            wordsLeft.setText("" + getWords().size());
        });
    }

/**
 * It sends a message to the server to tell it that the game is over, and then it requests the current players alive
 */
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
        isDead = true;
    }

/**
 * It takes a list of names and displays them on the screen
 * 
 * @param names List of names to be displayed on the podium
 */
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

/**
 * It sends a message to the server to request the current players alive
 */
    public void podiumRequest() throws IOException {
        LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
        map.put("message", "PodiumRequest");
        Gson gson = new Gson();
        String s = gson.toJson(map);
        PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
        out.println(s);
    }

/**
 * Changes the state of the game to over, and displays the podium
 * 
 * @param podium List of Strings, each String is a player's name
 */
    public void endGame(List<String> podium) {
        this.multi.setVisible(false);
        this.endgame.setVisible(true);
        this.drawResults(podium);
    }

/**
 * It draws the final results of the game on the screen
 * 
 * @param names List of names to be displayed
 */
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

/**
 * It sends a message to the server to restart the game
 * 
 * @param event the event that triggered the method
 */
    public void restart_game(ActionEvent event) {
        Platform.runLater(() -> {
            LinkedTreeMap<String, Object> map = new LinkedTreeMap<>();
            map.put("message", "Replay");
            Gson gson = new Gson();
            String s = gson.toJson(map);
            try {
                PrintWriter out = new PrintWriter(Lobby.getConnection().getSocket().getOutputStream(), true);
                out.println(s);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("IOException Multi");
            }
        });

    }

/**
 * It resets the game to its initial state
 */
    public void replay() {
        isDead = false;
        this.health = DEFAULT_HEALTH;
        this.healthlbl.setText("" + health);
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
        remakeList();
        displayMalus();
    }

}