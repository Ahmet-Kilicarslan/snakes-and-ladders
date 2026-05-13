package org.snakesandladders.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.snakesandladders.client.MainApp;
import org.snakesandladders.client.network.ServerConnection;
import org.snakesandladders.common.protocol.Message;

import java.io.IOException;

public class StartController {

    @FXML private TextField nameField;
    @FXML private TextField ipField;
    @FXML private Button connectButton;
    @FXML private Label statusLabel;

    private static ServerConnection connection;
    private static String playerName;
    private static int playerNumber;

    @FXML
    public void onConnectClicked() {
        String name = nameField.getText().trim();
        String ip = ipField.getText().trim();

        if (name.isEmpty() || ip.isEmpty()) {
            statusLabel.setText("Please enter your name and server IP.");
            return;
        }

        connectButton.setDisable(true);
        statusLabel.setText("Connecting...");

        Thread connectThread = new Thread(() -> {
            try {
                connection = new ServerConnection(ip, 5000);
                String raw = connection.readMessage();
                Message first = Message.parse(raw);

                if (first.getType().equals(Message.WAIT)) {
                    Platform.runLater(() -> statusLabel.setText(first.getData()));
                }

                raw = connection.readMessage();
                Message assigned = Message.parse(raw);

                if (assigned.getType().equals(Message.PLAYER_ASSIGNED)) {
                    playerNumber = Integer.parseInt(assigned.getData());
                    playerName = name;
                    connection.sendMessage(new Message(Message.PLAYER_ASSIGNED, name));

                    raw = connection.readMessage();
                    Message bothReady = Message.parse(raw);

                    if (bothReady.getType().equals(Message.WAIT)) {
                        String[] names = bothReady.getData().split(",");
                        GameController.setGameData(connection, playerNumber,
                                names[0], names[1]);
                        Platform.runLater(() -> {
                            try {
                                MainApp.showGameScreen();
                            } catch (Exception e) {
                                statusLabel.setText("Failed to load game screen.");
                            }
                        });
                    }
                }
            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Could not connect to server.");
                    connectButton.setDisable(false);
                });
            }
        });

        connectThread.setDaemon(true);
        connectThread.start();
    }
}