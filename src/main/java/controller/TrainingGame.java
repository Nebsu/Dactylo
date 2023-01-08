package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import misc.*;

public final class TrainingGame extends Game {

    private final ArrayList<Long> inputTimes = new ArrayList<>();
    //number of times the user has hit the space bar = number of words typed
    private int hitcounter = 0;
    private int errorcounter = 0;
    private int utilcharcounter = 0;
    Timer timer = new Timer();
    private int countdown = 60;
    private boolean isNewWord = false;
    private DecimalFormat df = new DecimalFormat("#.#");

    @FXML private Label timerlbl = new Label();
    @FXML private Label wpm = new Label();
    @FXML private Label accuracy = new Label();
    @FXML private Label regularity = new Label();
    @FXML private Scene scene;
    @FXML private Stage stage;


    /**
     * When the user types in the textfield, the program checks if the character is correct or not. If
     * it is correct, it increments the utilcharcounter. If it is wrong, it increments the errorcounter
     */
    @FXML
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        remakeList();
        setNewText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
        getInput().textProperty().addListener((observable, oldValue, newValue) -> {
            //When we reach the end of the word
            if (newValue.length() <= getWords().get(0).toString().length()) {
                if (!newValue.equals(getWords().get(0).toString().substring(0, newValue.length()))) {
                    if (!newValue.equals(" "))
                    getInput().setStyle("-fx-text-fill: #e83e3e;");
                    //increment when the character is wrong
                    errorcounter++;
                } else {
                    getInput().setStyle("-fx-text-fill: #383734;");
                    //increment when the character is correct
                    utilcharcounter++;
                    inputTimes.add(System.currentTimeMillis());
                }
            } else {
                getInput().setStyle("-fx-text-fill: #e83e3e;");
                errorcounter++;
            }
        });
    }

    /**
     * It changes the scene to the solo.fxml scene
     * 
     * @param event The event that triggered the method.
     */
    @Override
    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../solo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

/**
 * It's a timer that counts down from 60 seconds and when it reaches 0, it stops the game and
 * calculates the score
 */
    public void timerStart(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timerlbl.setText(String.valueOf(countdown));
                    countdown--;
                    if(countdown < 0){
                        inputTimes.add(System.currentTimeMillis());
                        //System.out.println("utilcharcounter: " + utilcharcounter);
                        //System.out.println("hitcounter: " + hitcounter);
                        if (utilcharcounter > 0)  utilcharcounter -= hitcounter;
                        setNewText("Press reset button to play again");
                        setWpm();
                        setAccuracy();
                        setRegularity();
                        getInput().setEditable(false);

                        //System.out.println("errorcounter: " + errorcounter);
                        //System.out.println("inputcounter: " + inputcounter);
                        setGamestate(false);
                        timer.purge();
                        timer.cancel();
                        getWords().clear();
                        getInput().setDisable(true);
                    }
                });
            }
        }, 0, 1000);
    }

/**
 * It takes the number of characters typed and divides it by 5 to get the words per minute
 */
    public void setWpm(){
        wpm.setText("" + df.format((float)utilcharcounter / 5));
    }

/**
 * It takes the number of characters typed, divides it by 5 to get the number of words typed, subtracts
 * the number of errors from that number, divides the result by the number of words typed, multiplies
 * the result by 100, and then sets the text of the accuracy TextView to the result
 */
    public void setAccuracy(){
        float grossWPM = (float)utilcharcounter / 5;
        float netWPM = grossWPM - (float)errorcounter / 5;
        float accur = (netWPM / grossWPM) * 100;
        if (accur < 0) {
            accur = 0;
        } else if (accur > 100) {
            accur = 100;
        }
        accuracy.setText("" + df.format(accur) + "%");
    }

    public void setRegularity(){
        double sum = 0;
        for (int i = 1; i < inputTimes.size(); i++) {
            double diff = inputTimes.get(i) - inputTimes.get(i-1);
            sum += diff * diff;
        }
        double variance = sum / (inputTimes.size() - 1);
        double res = Math.sqrt(variance);
        regularity.setText("" + df.format(res));
    }

/**
 * It resets the game
 */
    public void reset(){
        countdown = 60;
        hitcounter = 0;
        utilcharcounter = 0;
        errorcounter = 0;
        inputTimes.clear();
        setGamestate(false);
        timerlbl.setText("" + countdown);
        remakeList();
        if(timer != null){
            timer.cancel();
        }
        getInput().setEditable(true);
        getInput().setDisable(false);
        getInput().clear();
        // getText().setText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
        setNewText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
    }


/**
 * It checks if the user has pressed the space bar, if so, it checks if the word is correct, if not, it
 * adds the length of the word to the error counter, if so, it adds one to the hit counter, removes the
 * first word from the list and adds a new random word to the end of the list, then it refreshes the
 * text and clears the input field
 * 
 * @param event the key event that is triggered when a key is pressed
 */
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false) {
            setGamestate(true);
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.SPACE && getGamestate()) {
            String word = getInput().getText();
            if (!getWords().get(0).toString().equals(word)) errorcounter += getWords().get(0).toString().length();
            hitcounter++;
            getWords().remove(0);
            getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            //refresh the text
            setNewText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
            getInput().clear();
            //getInput().setEditable(false);
            isNewWord = true;
        }else if (event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.BACK_SPACE && event.getCode() != KeyCode.ESCAPE && getGamestate()) {
            if(isNewWord){
                getInput().clear();
                isNewWord = false;
            }
        }
    }
}