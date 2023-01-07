package controller;

import com.sun.javafx.UnmodifiableArrayList;
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

public class TrainingGame extends Game {

    private ArrayList<Long> inputTimes = new ArrayList<>();
    //number of times the user has hit the space bar = number of words typed
    private int hitcounter = 0;
    private int errorcounter = 0;
    private int utilcharcounter = 0;
    private int inputcounter = 0;
    Timer timer = new Timer();
    private int countdown = 60;
    private boolean isNewWord = false;
    private DecimalFormat df = new DecimalFormat("#.#");
    @FXML
    private Label timerlbl = new Label();
    @FXML
    private Label wpm = new Label();
    @FXML
    private Label accuracy = new Label();
    @FXML
    private Label regularity = new Label();
    @FXML
    private Scene scene;
    private Stage stage;

    @FXML
    public void initialize() {
        setTextFieldColor();
        setNewDictionary();
        remakeList();
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
    // Change game mode to training
    @Override
    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../solo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    //Game loop
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
                        getText().setText("Press reset button to play again");
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

    public void setWpm(){
        wpm.setText("" + df.format((float)utilcharcounter / 5));
    }

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

    public void reset(){
        countdown = 60;
        hitcounter = 0;
        utilcharcounter = 0;
        errorcounter = 0;
        inputcounter = 0;
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
        getText().setText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
    }

    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false && event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.ESCAPE) {
            setGamestate(true);
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.SPACE && getGamestate()) {
            String word = getInput().getText();
            if (!getWords().get(0).toString().equals(word)) errorcounter += getWords().get(0).toString().length();
            hitcounter++;
            getInput().setStyle("-fx-text-fill: #383734");
            getWords().remove(0);
            getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            //refresh the text
            getText().setText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
            getInput().clear();
            //getInput().setEditable(false);
            isNewWord = true;
        }else if (event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.BACK_SPACE && event.getCode() != KeyCode.ESCAPE && getGamestate()) {
            inputcounter++;
            if(isNewWord){
                getInput().clear();
                isNewWord = false;
            }
        }
    }
}