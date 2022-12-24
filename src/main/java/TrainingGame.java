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
import java.util.*;

public class TrainingGame extends Game {
    Timer timer = new Timer();
    private int countdown = 60;
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
                    getInput().setStyle("-fx-text-fill: red;");
                    //increment when the character is wrong
                    setErrorcounter(getErrorcounter() + 1);
                } else {
                    getInput().setStyle("-fx-text-fill: #383734;");
                    //increment when the character is correct
                    setUtilcharcounter(getUtilcharcounter() + 1);
                }
            } else {
                getInput().setStyle("-fx-text-fill: red;");
                setErrorcounter(getErrorcounter() + 1);
            }
        });
    }

    @Override
    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("solo.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void timerStart(){
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timerlbl.setText(String.valueOf(countdown));
                    countdown--;
                    if(countdown < 0){
                        timer.cancel();
                        setUtilcharcounter(getUtilcharcounter());
                        setErrorcounter(getErrorcounter());
                        getText().setText("Press escape to restart");
                        setWpm();
                        setAccuracy();
                        getInput().setEditable(false);
                        System.out.println("utilcharcounter: " + getUtilcharcounter());
                        System.out.println("errorcounter: " + getErrorcounter());
                        System.out.println("inputcounter: " + getInputcounter());
                        System.out.println("hitcounter: " + getHitcounter());
                    }
                });
            }
        }, 0, 1000);
    }

    public void setWpm(){
        wpm.setText("" + df.format((float)getUtilcharcounter() / 5));
    }

    public void setAccuracy(){
        float grossWPM = (float)getUtilcharcounter() / 5;
        float netWPM = grossWPM - (float)getErrorcounter() / 5;
        float accur = (netWPM / grossWPM) * 100;
        if (accur < 0) {
            accur = 0;
        } else if (accur > 100) {
            accur = 100;
        }
        accuracy.setText("" + df.format(accur) + "%");
    }

    public void reset(){
        countdown = 60;
        setHitcounter(0);
        setUtilcharcounter(0);
        setErrorcounter(0);
        setInputcounter(0);
        setGamestate(false);
        getInput().clear();
        timerlbl.setText("" + countdown);
        remakeList();
        if(timer != null){
            timer.cancel();
        }
        getInput().setEditable(true);
    }


    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (getGamestate() == false) {
            setGamestate(true);
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            reset();
        }
        if (event.getCode() == KeyCode.SPACE) {
            String word = getInput().getText();
            if (!getWords().get(0).toString().equals(word)) setErrorcounter(getErrorcounter() + getWords().get(0).toString().length());
            setHitcounter(getHitcounter() + 1);
            getInput().setStyle("-fx-text-fill: #383734");
            getWords().remove(0);
            getWords().add(getDictionary().get(new Random().nextInt(getDictionary().size())));
            //refresh the text
            getText().setText(String.join(" ", getWords().toString().replaceAll("[\\[\\],]", "")));
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
