import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Dactylo extends Application {
    
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
		Global.SERVER.start();
        launch();
    }
}