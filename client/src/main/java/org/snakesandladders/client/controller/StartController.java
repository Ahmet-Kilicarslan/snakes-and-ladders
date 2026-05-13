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
    private static int playerNumber;
    private static String player1Name;
    private static String player2Name;

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
                System.out.println("Attempting connection...");
                connection = new ServerConnection(ip, 5000);
                System.out.println("Socket connected.");

                String raw = connection.readMessage();
                System.out.println("First message: " + raw);
                if (raw == null) throw new IOException("Server closed connection.");

                Message incoming = Message.parse(raw);

                if (incoming.getType().equals(Message.WAIT)) {
                    String waitText = incoming.getData();
                    Platform.runLater(() -> statusLabel.setText(waitText));
                    raw = connection.readMessage();
                    System.out.println("Second message: " + raw);
                    if (raw == null) throw new IOException("Server closed connection.");
                    incoming = Message.parse(raw);
                }

                if (incoming.getType().equals(Message.PLAYER_ASSIGNED)) {
                    playerNumber = Integer.parseInt(incoming.getData());
                    connection.sendMessage(new Message(Message.PLAYER_ASSIGNED, name));
                    int assignedNumber = playerNumber;
                    Platform.runLater(() -> statusLabel.setText(
                            "Assigned as Player " + assignedNumber + ". Waiting for opponent..."));
                }

                System.out.println("Waiting for both ready...");
                raw = connection.readMessage();
                System.out.println("Next message: " + raw);
                if (raw == null) throw new IOException("Server closed connection.");
                Message bothReady = Message.parse(raw);

                if (bothReady.getType().equals(Message.WAIT)) {
                    String[] names = bothReady.getData().split(",");
                    player1Name = names[0];
                    player2Name = names[1];
                    GameController.setGameData(connection, playerNumber, player1Name, player2Name);
                    Platform.runLater(() -> {
                        try {
                            MainApp.showGameScreen();
                        } catch (Exception e) {
                            statusLabel.setText("Failed to load game screen.");
                        }
                    });
                }

            } catch (IOException e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Could not connect: " + e.getMessage());
                    connectButton.setDisable(false);
                });
            }
        });

        connectThread.setDaemon(true);
        connectThread.start();
    }
}