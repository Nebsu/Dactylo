package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static misc.Global.*;
import misc.GameSettings;
import misc.Global;
import misc.Word;

public class Solo extends Game {

    private Timer timer;
    private int health = DEFAULT_HEALTH;
    private int wordCount = DEFAULT_WORD_COUNT;
    private int level = DEFAULT_LEVEL;
    private int score = 0;
    private double interval = 3*Math.pow(0.9, level);
    private boolean isNewWord = false;

    @FXML
    private Label healthlbl = new Label();
    @FXML
    private Label wordCountlbl = new Label();
    @FXML
    private Label levellbl = new Label();
    @FXML
    private Label wordsLeft = new Label();
    @FXML
    private TextFlow textFlow = new TextFlow();

    
/**
 * It takes a list of words, and displays them in a TextFlow object with the correct color.(￣▽￣)ノ
 */
    public void displayList(){
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
 * It writes the data to a file
 */
    public void saveData() {
        try {
            File file = new File(FILE_PATH);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            System.out.println("Saving data...");
            writer.write("default:false");
            writer.newLine();
            writer.write("health:" + health);
            writer.newLine();
            writer.write("score:" + score);
            writer.newLine();
            writer.write("wordCount:" + wordCount);
            writer.newLine();
            writer.write("level:" + level);
            writer.newLine();
            writer.write("username:" + GameSettings.getUsername());
            writer.newLine();
            writer.write("difficulty:" + GameSettings.getDifficulty());
            writer.newLine();
            writer.write("maxWords:" + GameSettings.getMaxWords());
            writer.newLine();
            writer.write("words:");
            for (Word word : getWords()) {
                writer.write(word.toString() + "," + word.getType() + ";");
            }
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * It reads a file and loads the data into the game
 */
    public void loadData() {
        try {
            File file = new File(FILE_PATH);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String key = parts[0];
                String value = parts[1];
                if (key.equals("default")) {
                    if (value.equals("true")) {
                        health = DEFAULT_HEALTH;
                        wordCount = DEFAULT_WORD_COUNT;
                        level = DEFAULT_LEVEL;
                        remakeList();
                        for (int i = 0; i < GameSettings.getWords_max_length()/2; i++) {
                            getWords().remove(i);
                        }
                        displayList();
                    }
                } else if (key.equals("wordCount")) {
                    wordCount = Integer.parseInt(value);
                } else if (key.equals("level")) {
                    level = Integer.parseInt(value);
                } else if (key.equals("health")) {
                    health = Integer.parseInt(value);
                } else if (key.equals("score")) {
                    score = Integer.parseInt(value);
                } else if (key.equals("username")) {
                    GameSettings.setUsername(value);
                } else if (key.equals("difficulty")) {
                    GameSettings.setDifficulty(value);
                } else if (key.equals("maxWords")) {
                    GameSettings.setMaxWords(Integer.parseInt(value));
                } else if (key.equals("words")) {
                    String[] words = value.split(";");
                    for (String word : words) {
                        String[] parts2 = word.split(",");
                        String wordString = parts2[0];
                        char type = parts2[1].charAt(0);
                        getWords().add(new Word(wordString, type));
                    }
                    displayList();
                }
            }
            reader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * It clears the file and writes "default:true" to it
 */
    public void clearFile() {
        try {
            File file = new File(FILE_PATH);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write("default:true");
            System.out.println("File cleared");
            writer.newLine();
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * It takes the username, score, and level from the game and writes it to a file
 */
    public void addLeaderboard(){
        try {
            File file = new File(LEADERBOARD_FILE_PATH);
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(GameSettings.getUsername() + "@" + score + "@"+ level);
            writer.newLine();
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/**
 * It sets the text field color, sets a new dictionary, loads the data, sets the word count, words
 * left, health, and level labels, and adds a listener to the text field.
 */
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        try {
            loadData();
        } catch (Exception e) {
        }
        wordCountlbl.setText(""+ (WORDS_TO_LEVEL_UP - wordCount));
        wordsLeft.setText("" + getWords().size());
        healthlbl.setText("" + health);
        levellbl.setText("" + level);
        getInput().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!getWords().isEmpty()) {
                if (newValue.length() <= getWords().get(0).toString().length()) {
                    if (!newValue.equals(getWords().get(0).toString().substring(0, newValue.length()))) {
                        // If the input is wrong
                        if (!newValue.equals(" "))
                        getInput().setStyle("-fx-text-fill: #e83e3e;");
                    } else {
                        // If the input is correct
                        getInput().setStyle("-fx-text-fill: #383734;");
                    }
                } else {
                    // If the input is longer than the word
                    getInput().setStyle("-fx-text-fill: #e83e3e;");
                }
            }
        });
    }

/**
 * If the player's health is less than or equal to 0, add the player's score to the leaderboard, clear
 * the words from the screen, disable the input field, set the game state to false, cancel the timer,
 * and display a message to the player
 * 
 * @return The boolean value of the if statement.
 */
    public boolean gameOver(){
        if (health <= 0) {
            addLeaderboard();
            getWords().clear();
            getInput().setDisable(true);
            getInput().setEditable(false);
            setGamestate(false);
            timer.cancel();
            timer.purge();
            Text text = new Text("Your reached level " + level + " and typed " + wordCount + " words!");
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            text.setFill(Color.web("#383734"));
            textFlow.getChildren().clear();
            textFlow.getChildren().add(text);
            return true;
        }
        return false;
    }

/**
 * It starts a timer that adds a word to the list every interval seconds, and if the list is full, it
 * checks if the user input is the same as the first word in the list. If it is, it removes the first
 * word in the list, if it isn't, it removes the first word in the list and subtracts health
 */
    public void timerStart(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(getWords().size() == GameSettings.getWords_max_length()){
                        String word2 = getInput().getText();
                        if (!getWords().get(0).toString().equals(word2)) {
                            // perte de vie :
                            health -= compareWords(getWords().get(0).toString(), word2);
                            getWords().remove(0);
                            getInput().clear();
                            healthlbl.setText("" + health);
                        }
                        //Check health
                        if(gameOver() == true){
                            return;
                        }
                    }
                    if (health > 0){
                        getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
                        displayList();
                        wordsLeft.setText("" + getWords().size());
                    }
                });
            }
        }, 0, (int)interval*1000);
    }

/**
 * It resets the game to its default state
 */
    public void reset(){
        clearFile();
        score = 0;
        health = DEFAULT_HEALTH;
        healthlbl.setText("" + health);
        wordCount = DEFAULT_WORD_COUNT;
        wordCountlbl.setText("" + (WORDS_TO_LEVEL_UP - wordCount));
        level = DEFAULT_LEVEL;
        levellbl.setText("" + level);
        interval = 3*Math.pow(0.9, level);
        setGamestate(false);
        getInput().setEditable(true);
        getInput().setDisable(false);
        getInput().clear();
        remakeList();
        for (int i = 0; i < GameSettings.getWords_max_length()/2+1; i++) {
            getWords().remove(i);
        }
        displayList();
        if(timer != null){
            timer.cancel();
        }
    }

/**
 * It compares two words and returns the number of differences between them
 * 
 * @param word1 The first word to compare
 * @param word2 the word that you want to compare to
 * @return The difference between the two words.
 */
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

/**
 * It checks if the user has pressed the space bar, if so it checks if the word is correct, if not it
 * subtracts the difference between the two words from the health, if so it adds the length of the word
 * to the score
 * 
 * @param event The key event that is triggered when the user presses a key
 */
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false) {
            setGamestate(true);
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.SPACE && getGamestate()) {
            String word = getInput().getText();
            // Check if the word is correct
            if (!getWords().get(0).toString().equals(word)) {
                //If the word is incorrect lose the difference between the two words in health
                health -= compareWords(getWords().get(0).toString(), word);
                healthlbl.setText(""+health);
                //Check health
                if(gameOver() == true){
                    return;
                }
            } else {
                score += word.length();
                if (getWords().get(0).getType() == 'b') {
                    //If the word is a bonus word
                    health += word.length();
                    healthlbl.setText("" + health);
                }
            }
            wordCount++;
            // Check level up
            if (wordCount == Global.WORDS_TO_LEVEL_UP) {
                //Level up + increase speed
                wordCount = 0;
                wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP));
                level++;
                levellbl.setText(""+level);
                interval = 3*Math.pow(0.9, level);
            }
            if (getWords().size() < GameSettings.getWords_max_length()/2) {
                //If the list is too short add more words
                getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            }
            wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP - wordCount));
            wordsLeft.setText("" + getWords().size());
            getInput().setStyle("-fx-text-fill: #383734");
            //refresh the text
            isNewWord = true;
            if (health > 0){
                getWords().remove(0);
                getInput().clear();
                displayList();
            }
            if (isNewWord) {
                //If the user is typing a new word, clear the text
                getInput().clear();
                isNewWord = false;
            }
        }
    }
}