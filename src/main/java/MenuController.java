import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Button solobutton;
    @FXML
    private Button multibutton;
    @FXML
    private Button settingsbutton;
    @FXML
    private Button quitbutton;
    @FXML
    private Scene scene;
    private Stage stage;


    public void click(){
        System.exit(0);
    }
    @FXML
    public void handleButtonHover(MouseEvent event){
        if(event.getSource() == solobutton) {
            solobutton.setStyle("-fx-background-color: #b7e4c7");
        } else if (event.getSource() == multibutton) {
            multibutton.setStyle("-fx-background-color: #ee964b");
        } else if (event.getSource() == settingsbutton) {
            settingsbutton.setStyle("-fx-background-color: #b7e4c7");
        } else if (event.getSource() == quitbutton) {
            quitbutton.setStyle("-fx-background-color: #ee964b");
        }
    }

    @FXML
    public void handleButtonLeave(MouseEvent event){
        if(event.getSource() == solobutton) {
            solobutton.setStyle("-fx-background-color:  #95d5b2");
        } else if (event.getSource() == multibutton) {
            multibutton.setStyle("-fx-background-color:  #e76f51");
        } else if (event.getSource() == settingsbutton) {
            settingsbutton.setStyle("-fx-background-color:  #95d5b2");
        } else if (event.getSource() == quitbutton) {
            quitbutton.setStyle("-fx-background-color:  #e76f51");
        }
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("game.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("settings.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
}