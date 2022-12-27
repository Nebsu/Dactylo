package Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import misc.GameSettings;
import misc.Global;
import misc.Word;

import java.io.*;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static misc.Global.*;

public class Solo extends Game {
    private Timer timer;
    private int health = DEFAULT_HEALTH;
    private int wordCount = DEFAULT_WORD_COUNT;
    private int level = DEFAULT_LEVEL;
    private double interval = 3*Math.pow(0.9, level);

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
            writer.write("wordCount:" + wordCount);
            writer.newLine();
            writer.write("level:" + level);
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
                    System.out.println("Level: " + level);
                } else if (key.equals("health")) {
                    health = Integer.parseInt(value);
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
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        try {
            loadData();
        } catch (Exception e) {
        }
        wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP - wordCount));
        wordsLeft.setText("" + getWords().size());
        healthlbl.setText("" + health);
        levellbl.setText("" + level);
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

    public void timerStart(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(getWords().size() == GameSettings.getWords_max_length()){
                        health -= getWords().get(0).toString().length();
                        healthlbl.setText("" + health);
                        if (health <= 0) {
                            timer.cancel();
                            timer.purge();
                            getText().setText("Your reached level " + level + " and typed " + wordCount + " words!");
                            reset();
                        }
                    } else {
                        getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
                    }
                    displayList();
                    wordsLeft.setText("" + getWords().size());
                });
            }
        }, 0, (int)interval*1000);
    }

    public void reset(){
        clearFile();
        health = DEFAULT_HEALTH;
        healthlbl.setText("" + health);
        wordCount = DEFAULT_WORD_COUNT;
        wordCountlbl.setText("" + (WORDS_TO_LEVEL_UP - wordCount));
        level = DEFAULT_LEVEL;
        levellbl.setText("" + level);
        interval = 3*Math.pow(0.9, level);
        setGamestate(false);
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

    //return number of difference between two words with different length
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
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false) {
            setGamestate(true);
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            reset();
        }
        if (event.getCode() == KeyCode.SPACE && !getWords().isEmpty()) {
            String word = getInput().getText();
            if (!getWords().get(0).equals(word)) {
                health -=  compareWords(getWords().get(0).toString(), word);
                healthlbl.setText(""+health);
                if (health <= 0) {
                    timer.cancel();
                    timer.purge();
                    getText().setText("Your reached level " + level + " and typed " + wordCount + " words!");
                    reset();
                }
            }
            if (getWords().get(0).getType() == 'b') {
                health += 5;
                healthlbl.setText("" + health);
            }
            wordCount++;
            if (wordCount == Global.WORDS_TO_LEVEL_UP) {
                wordCount = 0;
                wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP));
                level++;
                levellbl.setText(""+level);
                interval = 3*Math.pow(0.9, level);
                if (getWords().size() < GameSettings.getWords_max_length()/2) {
                    getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
                }
            }
            getWords().remove(0);
            wordCountlbl.setText(""+ (Global.WORDS_TO_LEVEL_UP - wordCount));
            wordsLeft.setText("" + getWords().size());
            getInput().setStyle("-fx-text-fill: #383734");
            //refresh the text
            displayList();
            getInput().clear();
            getInput().setEditable(false);
        }
    }
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            getInput().clear();
            getInput().setEditable(true);
            event.consume();
        }
    }
}
