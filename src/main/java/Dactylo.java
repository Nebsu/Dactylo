import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import misc.Global;

public class Dactylo extends Application {
    
/**
 * It loads the menu.fxml file, sets the title of the window, shows the window, sets the scene, and
 * sets the window to be always on top
 * 
 * @param stage The stage that will be displayed
 */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Dactylo.class.getResource("menu.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(Global.GAME_TITLE);
        stage.show();
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
        stage.setOnCloseRequest( event -> {
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
    
}