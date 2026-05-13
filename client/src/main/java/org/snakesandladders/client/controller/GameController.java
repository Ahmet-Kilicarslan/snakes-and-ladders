package org.snakesandladders.client.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.snakesandladders.client.MainApp;
import org.snakesandladders.client.network.ServerConnection;
import org.snakesandladders.client.ui.BoardRenderer;
import org.snakesandladders.common.model.GameBoard;
import org.snakesandladders.common.model.MoveResult;
import org.snakesandladders.common.protocol.Message;

public class GameController {

    @FXML private Canvas boardCanvas;
    @FXML private Button rollButton;
    @FXML private Label statusLabel;
    @FXML private Label player1Label;
    @FXML private Label player2Label;
    @FXML private Label player1PosLabel;
    @FXML private Label player2PosLabel;
    @FXML private VBox chatBox;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField chatInput;

    private static ServerConnection connection;
    private static int playerNumber;
    private static String player1Name;
    private static String player2Name;

    private BoardRenderer renderer;
    private GameBoard board;
    private int player1Position = 0;
    private int player2Position = 0;

    public static void setGameData(ServerConnection conn, int pNumber,
                                   String p1Name, String p2Name) {
        connection = conn;
        playerNumber = pNumber;
        player1Name = p1Name;
        player2Name = p2Name;
    }

    @FXML
    public void initialize() {
        board = new GameBoard();
        renderer = new BoardRenderer(boardCanvas, board);
        renderer.render(player1Position, player2Position);

        player1Label.setText(player1Name);
        player2Label.setText(player2Name);
        player1PosLabel.setText("Position: 0");
        player2PosLabel.setText("Position: 0");

        rollButton.setDisable(true);
        statusLabel.setText("Waiting for game to start...");

        startListening();
    }

    private void startListening() {
        Thread listenerThread = new Thread(() -> {
            try {
                while (true) {
                    String raw = connection.readMessage();
                    if (raw == null) break;
                    handleMessage(Message.parse(raw));
                }
            } catch (Exception e) {
                Platform.runLater(() -> statusLabel.setText("Connection lost."));
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void handleMessage(Message message) {
        switch (message.getType()) {
            case Message.YOUR_TURN -> Platform.runLater(() -> {
                rollButton.setDisable(false);
                statusLabel.setText("Your turn! Roll the dice.");
            });

            case Message.WAIT -> Platform.runLater(() -> {
                rollButton.setDisable(true);
                statusLabel.setText("Waiting for opponent...");
            });

            case Message.POSITION_UPDATE -> Platform.runLater(() -> {
                String[] parts = message.getData().split(",", 2);
                int movedPlayer = Integer.parseInt(parts[0]);
                MoveResult result = MoveResult.fromNetworkString(parts[1]);

                if (movedPlayer == 0) {
                    player1Position = result.getToPosition();
                    player1PosLabel.setText("Position: " + player1Position);
                } else {
                    player2Position = result.getToPosition();
                    player2PosLabel.setText("Position: " + player2Position);
                }

                renderer.render(player1Position, player2Position);
                showMoveEvent(movedPlayer == 0 ? player1Name : player2Name, result);
            });

            case Message.GAME_OVER -> {
                String data = message.getData();
                if (!data.equals("NO_RESTART")) {
                    int winnerNumber = Integer.parseInt(data);
                    String winnerName = winnerNumber == 1 ? player1Name : player2Name;
                    boolean isWinner = winnerNumber == playerNumber;
                    Platform.runLater(() -> {
                        try {
                            MainApp.showEndScreen(winnerName, isWinner);
                        } catch (Exception e) {
                            statusLabel.setText("Game over.");
                        }
                    });
                }
            }

            case Message.OPPONENT_DISCONNECTED -> Platform.runLater(() -> {
                rollButton.setDisable(true);
                statusLabel.setText("Opponent disconnected.");
            });

            case Message.CHAT -> Platform.runLater(() -> {
                addChatMessage(message.getData());
            });

            case Message.RESTART_REQUEST -> Platform.runLater(() -> {
                connection.sendMessage(new Message(Message.RESTART_ACCEPTED, ""));
            });
        }
    }

    private void showMoveEvent(String playerName, MoveResult result) {
        String event = switch (result.getEventType()) {
            case SNAKE -> playerName + " hit a snake! Slid from "
                    + result.getFromPosition() + " to " + result.getToPosition();
            case LADDER -> playerName + " climbed a ladder! From "
                    + result.getFromPosition() + " to " + result.getToPosition();
            case BOUNCED -> playerName + " rolled too high, stayed at "
                    + result.getToPosition();
            case WIN -> playerName + " reached 100 and wins!";
            default -> playerName + " rolled " + result.getDiceRoll()
                    + " and moved to " + result.getToPosition();
        };
        statusLabel.setText(event);
        addChatMessage(event);
    }

    private void addChatMessage(String text) {
        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 12px; -fx-padding: 2 4 2 4;");
        chatBox.getChildren().add(msg);
        chatScrollPane.setVvalue(1.0);
    }

    @FXML
    public void onRollClicked() {
        rollButton.setDisable(true);
        connection.sendMessage(new Message(Message.ROLL_DICE, ""));
    }

    @FXML
    public void onSendChatClicked() {
        String text = chatInput.getText().trim();
        if (!text.isEmpty()) {
            connection.sendMessage(new Message(Message.CHAT, text));
            chatInput.clear();
        }
    }
}