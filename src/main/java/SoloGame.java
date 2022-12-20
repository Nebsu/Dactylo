import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public class SoloGame {
    private ArrayList <String> dictionnary = new ArrayList<>();
    private ArrayList <String> words = new ArrayList<>();

    private int hitcounter = 0;
    private int errorcounter = 0;
    private int utilcharcounter = 0;
    private int inputcounter = 0;
    private int index = 0;
    private boolean gamestarted = false;
    private boolean textreveal = false;
    Timer timer = new Timer();
    private int countdown = 0;
    private int time = 0;
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
        if(textreveal == false) {
            remakeList();
            textreveal = true;
        }

        input.textProperty().addListener((observable, oldValue, newValue) -> {
            //If the input doesn't match the substring word change color to red
            if (!words.isEmpty()) {
                if (newValue.length() <= words.get(0).length()) {
                    if (!newValue.equals(words.get(0).substring(0, newValue.length()))) {
                        input.setStyle("-fx-text-fill: red;");
                        errorcounter++;
                    } else {
                        input.setStyle("-fx-text-fill: black;");
                        if (!newValue.equals(" ")){
                            utilcharcounter++;
                        }
                    }
                } else {
                    input.setStyle("-fx-text-fill: red;");
                }
            }
        });
    }

    public void remakeList(){
        words.clear();
        dictionnary.stream().limit(25).forEach(words::add);
        Collections.shuffle(words);
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
                    countdown++;
                    if(words.isEmpty()){
                        timelbl.setText("");
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    public void setWpm(){
        wpm.setText("" + df.format(((float)utilcharcounter / 5) / ((float)countdown / 60)));
    }

    public void setAccuracy(){
        accuracy.setText(String.valueOf((int) ((utilcharcounter / (double) inputcounter) * 100)));
    }


    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (gamestarted == false) {
            gamestarted = true;
            timer = new Timer();
            timerStart();
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            countdown = 0;
            hitcounter = 0;
            errorcounter = 0;
            utilcharcounter = 0;
            inputcounter = 0;
            index = 0;
            gamestarted = false;
            textreveal = false;
            input.clear();
            timerlbl.setText("" + countdown);
            remakeList();
            if(timer != null){
                timer.cancel();
            }
        }
        if (event.getCode() == KeyCode.SPACE) {
            String word = input.getText();
            if (words.get(0).equals(word)) {
                hitcounter++;
                System.out.println("Correct");
                input.setStyle("-fx-text-fill: black");
            } else {
                System.out.println("Incorrect");
                input.setStyle("-fx-text-fill: black");
                errorcounter += words.get(0).length();
            }
            words.remove(0);
            //update text in label without the word
            text.setText("");
            text.setText(String.join(" ", words));
            index = 0;
            input.clear();
            event.consume();
        }else if (event.getCode() == KeyCode.ALPHANUMERIC){
            inputcounter++;
        }
    }
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            if (words.isEmpty()) {
                timer.cancel();
                time = countdown;
                timelbl.setText(""+time);
                text.setText("You won!, hit: " + utilcharcounter + " miss: " + errorcounter);
                setWpm();
                setAccuracy();
            }
            input.clear();
            event.consume();
        }

    }
}
