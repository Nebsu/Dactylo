import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Solo extends Game {
    private Timer timer;
    private int health = 100;
    private int wordCount = 0;
    private int level = 1;
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

    public void makeList(){
        textFlow.getChildren().clear();
        textFlow.setTextAlignment(TextAlignment.CENTER);;
        for(Word word: getWords()) {
            Text text = new Text(word.toString() + " ");
            text.setTranslateY(7);
            text.setFont(javafx.scene.text.Font.font("System", 20));
            if (word.isBonus()){
                text.setFill(Color.web("#95d5b2"));
            }  else {
                text.setFill(Color.web("#383734"));
            }
            textFlow.getChildren().add(text);
        }
    }
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        remakeList();
        for (int i = 0; i < getWords().size()/2; i++) {
            getWords().remove(i);
        }
        //remakeList
        makeList();
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
                    makeList();
                    wordsLeft.setText("" + getWords().size());
                });
            }
        }, 0, (int)interval*1000);
    }

    public void reset(){
        health = 100;
        healthlbl.setText("" + health);
        wordCount = 0;
        wordCountlbl.setText("" + wordCount);
        level = 1;
        levellbl.setText("" + level);
        interval = 3*Math.pow(0.9, level);
        setGamestate(false);
        getInput().clear();
        remakeList();
        for (int i = 0; i < getWords().size()/2; i++) {
            getWords().remove(i);
        }
        makeList();
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
            if (getWords().get(0).isBonus()) {
                System.out.println("bonus");
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
            makeList();
            getInput().clear();
            getInput().setEditable(false);
        }else if (event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.BACK_SPACE){
            setInputcounter(getInputcounter() + 1);
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
