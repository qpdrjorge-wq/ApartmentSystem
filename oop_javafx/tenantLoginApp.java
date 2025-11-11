package com.example.oop_javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import java.io.IOException;

public class tenantLoginApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        SceneManager.setStage(stage);
        SceneManager.switchScene("loginPage.fxml");
        stage.setTitle("The Pavillion");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
