import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class SoloGame {
    private ArrayList <String> dictionnary = new ArrayList<>();
    private ArrayList <String> words = new ArrayList<>();
    //number of times the user has hit the space bar = number of words typed
    private int hitcounter = 0;
    private int errorcounter = 0;
    private int utilcharcounter = 0;
    private int inputcounter = 0;
    private boolean gamestarted = false;
    Timer timer = new Timer();
    private int countdown = 60;
    private DecimalFormat df = new DecimalFormat("#.#");
    @FXML
    private Label timelbl = new Label();
    @FXML
    private Label timerlbl = new Label();
    @FXML
    private Label text = new Label();
    @FXML
    private TextField input;
    @FXML
    private Label wpm = new Label();
    @FXML
    private Label accuracy = new Label();
    @FXML
    private Label regularity = new Label();
    public SoloGame() throws FileNotFoundException {
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/words.txt"));

            String word;
            while ((word = in.readLine()) != null) {
                dictionnary.add(word);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        remakeList();
        Font font = Font.font("Arial", FontWeight.BOLD,18);
        input.setFont(font);
        input.textProperty().addListener((observable, oldValue, newValue) -> {
            //When we reach the end of the word
            if (newValue.length() <= words.get(0).length()) {
                if (!newValue.equals(words.get(0).substring(0, newValue.length()))) {
                    input.setStyle("-fx-text-fill: red;");
                    //increment when the character is wrong
                    errorcounter++;
                } else {
                    input.setStyle("-fx-text-fill: #383734;");
                    //increment when the character is correct
                    utilcharcounter++;
                }
            } else {
                input.setStyle("-fx-text-fill: red;");
            }
        });
    }

    public void remakeList(){
        words.clear();
        //Randomly add 15 words to the list words
        for (int i = 0; i < 15; i++) {
            words.add(dictionnary.get((int) (Math.random() * dictionnary.size())));
        }
        text.setText(String.join(" ", words));
    }

    public void exit() {
        System.exit(0);
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
                        utilcharcounter -= hitcounter;
                        errorcounter -= hitcounter;
                        timelbl.setText(""+countdown);
                        text.setText("Press escape to restart");
                        setWpm();
                        setAccuracy();
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
        }
        accuracy.setText("" + df.format(accur) + "%");
    }

    public void reset(){
        countdown = 60;
        hitcounter = 0;
        errorcounter = 0;
        utilcharcounter = 0;
        inputcounter = 0;
        gamestarted = false;
        input.clear();
        timerlbl.setText("" + countdown);
        remakeList();
        if(timer != null){
            timer.cancel();
        }
    }


    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (gamestarted == false) {
            gamestarted = true;
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            reset();
        }
        if (event.getCode() == KeyCode.SPACE) {
            String word = input.getText();
            if (!words.get(0).equals(word)) errorcounter += words.get(0).length();
            hitcounter++;
            input.setStyle("-fx-text-fill: #383734");
            words.remove(0);
            words.add(dictionnary.get(new Random().nextInt(dictionnary.size())));
            //refresh the text
            text.setText(String.join(" ", words));
            input.clear();
            event.consume();
        }else if (event.getCode() != KeyCode.SPACE && event.getCode() != KeyCode.BACK_SPACE){
            inputcounter++;
        }
    }
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            input.clear();
            event.consume();
        }

    }
}
