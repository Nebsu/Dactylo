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

import static misc.Global.*;
import misc.GameSettings;
import misc.Global;
import misc.Word;

public class Multi extends Game {

    private int health = DEFAULT_HEALTH;
    private int wordCount = DEFAULT_WORD_COUNT;
    private int score = 0;

    @FXML
    private Label healthlbl = new Label();
    @FXML
    private Label wordCountlbl = new Label();
    @FXML
    private Label wordsLeft = new Label();
    @FXML
    private TextFlow textFlow = new TextFlow();

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
            }  else {
                text.setFill(Color.web("#383734"));
            }
            textFlow.getChildren().add(text);
        }
    }

    // Count the number of lines in the file
    public int getLineCount() {
        int lineCount = 1;
        try {
            File file = new File(LEADERBOARD_FILE_PATH);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);

            String line;
            while ((line = reader.readLine()) != null) {
                lineCount++;
            }
            reader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lineCount;
    }

    // Add the score to the leaderboard
    public void addLeaderboard(){
        try {
            File file = new File(LEADERBOARD_FILE_PATH);
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(GameSettings.getUsername() + "-" + score);
            writer.newLine();
            writer.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set the game, loads data and displays it, input listener
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        remakeList();
        displayList();
        wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP - wordCount));
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
    }

    // // Reset the game
    // public void reset(){
    //     addLeaderboard();
    //     score = 0;
    //     health = DEFAULT_HEALTH;
    //     healthlbl.setText("" + health);
    //     wordCount = DEFAULT_WORD_COUNT;
    //     wordCountlbl.setText("" + (WORDS_TO_LEVEL_UP - wordCount));
    //     setGamestate(false);
    //     getInput().clear();
    //     remakeList();
    //     for (int i = 0; i < GameSettings.getWords_max_length()/2+1; i++) {
    //         getWords().remove(i);
    //     }
    //     displayList();
    // }

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
        if (event.getCode() == KeyCode.ESCAPE) {
            // reset();
        }
        if (event.getCode() == KeyCode.SPACE && !getWords().isEmpty()) {
            String word = getInput().getText();
            if (!getWords().get(0).toString().equals(word)) {
                health -= compareWords(getWords().get(0).toString(), word);
                healthlbl.setText(""+health);
                if (health <= 0) {
                    getText().setText("You typed " + wordCount + " words!");
                    // reset();
                }
            } else {
                score += word.length();
                if (getWords().get(0).getType() == 'b') {
                    health += word.length();
                    healthlbl.setText("" + health);
                }
            }
            wordCount++;
            if (wordCount == Global.WORDS_TO_LEVEL_UP) {
                wordCount = 0;
                wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP));
            }
            getWords().remove(0);
            if (getWords().size() < GameSettings.getWords_max_length()/2) {
                getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            }
            wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP - wordCount));
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

}