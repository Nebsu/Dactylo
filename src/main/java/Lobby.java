import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public class Lobby {
    
    @FXML
    private Stage stage;
    private Scene scene;
    @FXML
    private Button start;
    @FXML
    private TextArea players_list;

    @FXML
    public void initialize() {
        // Global.CLIENT.connect();
    }

    public void show_names() {
        Color color = Color.web("#000000");
        BackgroundFill background_fill = new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(background_fill);
        players_list.setBackground(background);
        // players_list.setText(Server.players_to_string());
    }

    public void start_multi() {
        System.out.println("start");
		// AddConnectionPacket packet = new AddConnectionPacket();
		// client.sendObject(packet);
    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }
    
}