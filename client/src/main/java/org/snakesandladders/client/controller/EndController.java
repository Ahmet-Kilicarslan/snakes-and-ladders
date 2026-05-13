package org.snakesandladders.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.snakesandladders.client.MainApp;

public class EndController {

    @FXML private Label resultLabel;
    @FXML private Label winnerLabel;

    public void initialize(String winnerName, boolean isWinner) {
        winnerLabel.setText(winnerName + " wins!");
        if (isWinner) {
            resultLabel.setText("You Won!");
        } else {
            resultLabel.setText("You Lost!");
        }
    }

    @FXML
    public void onPlayAgainClicked() {
        try {
            MainApp.showStartScreen();
        } catch (Exception e) {
            System.err.println("Error returning to start screen: " + e.getMessage());
        }
    }

    @FXML
    public void onExitClicked() {
        System.exit(0);
    }
}