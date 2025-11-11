package com.example.oop_javafx;

import javafx.fxml.FXML;
import java.io.IOException;
import javafx.event.ActionEvent;



public class firstPgController {

    @FXML
    private void logInButton (ActionEvent event) throws IOException {
        SceneManager.switchScene("loginPage.fxml");
    }


}
