import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoloGame {
    private ArrayList <String> lines = new ArrayList<>();
    private int hitcounter = 0;
    private int misscounter = 0;
    private int index = 0;
    @FXML
    private Label text;
    @FXML
    private TextField input;
    public SoloGame() throws FileNotFoundException {
        try {
            BufferedReader in = new BufferedReader(new FileReader("src/main/resources/words.txt"));
            //add in (lines)
            String word;
            while ((word = in.readLine()) != null) {
                lines.add(word);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Add the list of words in the label
    public void addWords() {
        for (String line : lines) {
            text.setText(text.getText() + line + " ");
        }
    }

    // when space is pressed, check if the word is in the list
    public void checkWord(KeyEvent event) {
        if (lines.isEmpty()) {
            text.setText("You won!, hit: " + hitcounter + " miss: " + misscounter);
            return;
        }
        if (event.getCode() == event.getCode().SPACE) {
            String word = input.getText();
            if (lines.get(0).equals(word)) {
                hitcounter++;
                System.out.println("Correct");
                input.setStyle("-fx-text-fill: black");
            } else {
                misscounter++;
                System.out.println("Incorrect");
                input.setStyle("-fx-text-fill: black");
            }
            lines.remove(0);
            //update text in label without the word
            text.setText("");
            for (String line : lines) {
                text.setText(text.getText() + line + " ");
            }
            index = 0;
            input.clear();
            event.consume();
        } else {
            String cur = (lines.get(0).charAt(index) + "").toLowerCase();
            if ((event.getCode().toString().toLowerCase()).equals(cur)) {
                input.setStyle("-fx-text-fill: green");
                index++;
            } else {
                input.setStyle("-fx-text-fill: red");
            }
        }
    }
    public void removeSpace(KeyEvent event) {
        if (event.getCode() == event.getCode().SPACE) {
            input.clear();
            event.consume();
        }
    }





}
