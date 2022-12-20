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
import java.util.*;

public class SoloGame {
    private ArrayList <String> dictionnary = new ArrayList<>();
    private ArrayList <String> words = new ArrayList<>();

    private int hitcounter = 0;
    private int misscounter = 0;
    private int index = 0;
    private boolean gamestarted = false;
    private boolean textreveal = false;
    Timer timer = new Timer();

    @FXML
    private Label timerlbl = new Label();
    @FXML
    private Label text = new Label();
    @FXML
    private TextField input;
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
                    } else {
                        input.setStyle("-fx-text-fill: black;");
                    }
                } else {
                    input.setStyle("-fx-text-fill: red;");
                }
            }
        });
    }

    public void remakeList(){
        words.clear();
        for (int i = 0; i < 50; i++) {
            words.add(dictionnary.get((int) (Math.random() * dictionnary.size())));
        }
        text.setText(String.join(" ", words));
    }

    public void exit() {
        System.exit(0);
    }


    public void timerStart(){
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = 60;
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timerlbl.setText(String.valueOf(i));
                    i--;
                    if(i == -1){
                        timer.cancel();
                    }
                });
            }
        }, 0, 1000);
    }

    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (gamestarted == false) {
            gamestarted = true;
            timer = new Timer();
            timerStart();
        }
        if (words.isEmpty()) {
            text.setText("You won!, hit: " + hitcounter + " miss: " + misscounter);
            return;
        }
        if (event.getCode() == KeyCode.ESCAPE && gamestarted == true) {
            hitcounter = 0;
            misscounter = 0;
            index = 0;
            gamestarted = false;
            textreveal = false;
            input.clear();
            timerlbl.setText("60");
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
                misscounter++;
                System.out.println("Incorrect");
                input.setStyle("-fx-text-fill: black");
            }
            words.remove(0);
            //update text in label without the word
            text.setText("");
            text.setText(String.join(" ", words));
            index = 0;
            input.clear();
            event.consume();
        }
    }
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) {
            input.clear();
            event.consume();
        }
    }





}
