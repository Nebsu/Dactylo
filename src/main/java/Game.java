import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

public class Game {

    //number of times the user has hit the space bar = number of words typed
    private int hitcounter = 0;
    private int errorcounter = 0;
    private int utilcharcounter = 0;
    private int inputcounter = 0;
    private ArrayList<String> dictionary = new ArrayList<>();
    private ArrayList <String> words = new ArrayList<>();
    @FXML
    private Label text = new Label();
    @FXML
    private Scene scene;
    private Stage stage;

    public ArrayList<String> getDictionary() {
        return dictionary;
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public Label getText() {
        return text;
    }

    public int getHitcounter() {
        return hitcounter;
    }

    public void setHitcounter(int hitcounter) {
        this.hitcounter = hitcounter;
    }

    public int getErrorcounter() {
        return errorcounter;
    }

    public void setErrorcounter(int errorcounter) {
        this.errorcounter = errorcounter;
    }

    public int getInputcounter() {
        return inputcounter;
    }

    public void setInputcounter(int inputcounter) {
        this.inputcounter = inputcounter;
    }

    public int getUtilcharcounter() {
        return utilcharcounter;
    }

    public void setUtilcharcounter(int utilcharcounter) {
        this.utilcharcounter = utilcharcounter;
    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void changeGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void remakeList() {
        words.clear();
        Stream<String> stream = Stream.generate(() -> dictionary.get((int) (Math.random() * dictionary.size()))).limit(GameSettings.getWords_max_length());
        stream.forEach(words::add);
        text.setText(String.join(" ", words));
    }

    public void setNewDictionary(){
        try {
            dictionary.clear();
            BufferedReader in = new BufferedReader(new FileReader(GameSettings.getDifficulty()));
            String word;
            while ((word = in.readLine()) != null) {
                dictionary.add(word);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
