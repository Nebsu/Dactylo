package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import misc.Global;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

import static misc.Global.*;

public class LeaderboardController {
    @FXML
    private TableView<LeaderboardData> list;
    @FXML
    private TableColumn<LeaderboardData, String> nameColumn;
    @FXML
    private TableColumn<LeaderboardData, String> scoreColumn;
    @FXML
    private TableColumn<LeaderboardData, String> levelColumn;

    @FXML
    private Stage stage;
    private Scene scene;

    public void loadLeaderboard() {
        try {
            File file = new File(LEADERBOARD_FILE_PATH);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String line;
            ObservableList<LeaderboardData> data = FXCollections.observableArrayList();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("-");
                LeaderboardData lbData = new LeaderboardData(parts[0], Integer.parseInt(parts[2]), Integer.parseInt(parts[1]));
                System.out.println(lbData);
                data.add(lbData);
            }
            Collections.sort(data, (o1, o2) -> o2.getScore() - o1.getScore());
            list.setItems(data);
            reader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        levelColumn.setCellValueFactory(cellData -> cellData.getValue().levelProperty());
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty());
        loadLeaderboard();
    }

    public void back(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../settings.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setTitle(Global.GAME_TITLE);
        stage.setScene(scene);
        stage.show();
    }


    class LeaderboardData {
        private String username;
        private int level;
        private int score;

        public LeaderboardData(String username, int level, int score) {
            this.username = username;
            this.level = level;
            this.score = score;
        }

        public String getUsername() {
            return username;
        }

        public int getLevel() {
            return level;
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "LeaderboardData{" +
                    "username='" + username + '\'' +
                    ", level='" + level + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }

        public ObservableValue<String> usernameProperty() {
            return new SimpleStringProperty(username);
        }

        public ObservableValue<String> levelProperty() {
            return new SimpleStringProperty(String.valueOf(level));
        }

        public ObservableValue<String> scoreProperty() {
            return new SimpleStringProperty(String.valueOf(score));
        }
    }
}
