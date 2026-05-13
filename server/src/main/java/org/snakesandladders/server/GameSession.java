package org.snakesandladders.server;

import org.snakesandladders.common.model.GameBoard;
import org.snakesandladders.common.model.MoveResult;
import org.snakesandladders.common.model.Player;
import org.snakesandladders.common.protocol.Message;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class GameSession implements Runnable {

    private ClientHandler player1;
    private ClientHandler player2;
    private GameBoard board;
    private Player[] players;
    private int currentTurn;
    private Random random;

    public GameSession(Socket socket1, Socket socket2) throws IOException {
        board = new GameBoard();
        players = new Player[2];
        currentTurn = 0;
        random = new Random();
        player1 = new ClientHandler(socket1, 1, this);
        player2 = new ClientHandler(socket2, 2, this);
    }

    @Override
    public void run() {
        try {
            initializePlayers();
            runGameLoop();
        } catch (IOException e) {
            System.err.println("Session error: " + e.getMessage());
            broadcastMessage(new Message(Message.OPPONENT_DISCONNECTED, ""));
        } finally {
            player1.close();
            player2.close();
        }
    }

    private void initializePlayers() throws IOException {
        player1.sendMessage(new Message(Message.PLAYER_ASSIGNED, "1"));
        String raw1 = player1.readMessage();
        if (raw1 == null) throw new IOException("Player 1 disconnected during setup");
        String name1 = Message.parse(raw1).getData();
        player1.setPlayerName(name1);
        players[0] = new Player(name1, 1);
        System.out.println("Player 1 registered: " + name1);

        player2.sendMessage(new Message(Message.PLAYER_ASSIGNED, "2"));
        String raw2 = player2.readMessage();
        if (raw2 == null) throw new IOException("Player 2 disconnected during setup");
        String name2 = Message.parse(raw2).getData();
        player2.setPlayerName(name2);
        players[1] = new Player(name2, 2);
        System.out.println("Player 2 registered: " + name2);

        broadcastMessage(new Message(Message.WAIT, name1 + "," + name2));
    }

    private void runGameLoop() throws IOException {
        notifyTurn();

        while (true) {
            ClientHandler currentHandler = currentTurn == 0 ? player1 : player2;

            String raw = currentHandler.readMessage();
            if (raw == null) {
                broadcastMessage(new Message(Message.OPPONENT_DISCONNECTED, ""));
                break;
            }

            Message incoming = Message.parse(raw);

            if (incoming.getType().equals(Message.ROLL_DICE)) {
                int dice = random.nextInt(6) + 1;
                Player currentPlayer = players[currentTurn];

                MoveResult result = board.applyMove(currentPlayer.getPosition(), dice);
                currentPlayer.setPosition(result.getToPosition());

                String resultData = currentTurn + "," + result.toNetworkString();
                broadcastMessage(new Message(Message.POSITION_UPDATE, resultData));

                System.out.println(currentPlayer.getName() + " rolled " + dice
                        + " moved to " + result.getToPosition()
                        + " event: " + result.getEventType());

                if (result.getEventType() == MoveResult.EventType.WIN) {
                    broadcastMessage(new Message(Message.GAME_OVER,
                            String.valueOf(currentTurn + 1)));
                    handleRestart();
                    return;
                }

                currentTurn = (currentTurn + 1) % 2;
                notifyTurn();
            }

            if (incoming.getType().equals(Message.CHAT)) {
                String senderName = currentTurn == 0
                        ? player1.getPlayerName()
                        : player2.getPlayerName();
                broadcastMessage(new Message(Message.CHAT,
                        senderName + ": " + incoming.getData()));
            }
        }
    }

    private void handleRestart() throws IOException {
        player1.sendMessage(new Message(Message.RESTART_REQUEST, ""));
        player2.sendMessage(new Message(Message.RESTART_REQUEST, ""));

        String response1 = player1.readMessage();
        String response2 = player2.readMessage();

        if (response1 == null || response2 == null) {
            return;
        }

        boolean p1Wants = Message.parse(response1).getType().equals(Message.RESTART_ACCEPTED);
        boolean p2Wants = Message.parse(response2).getType().equals(Message.RESTART_ACCEPTED);

        if (p1Wants && p2Wants) {
            resetGame();
            broadcastMessage(new Message(Message.RESTART_ACCEPTED, ""));
            runGameLoop();
        } else {
            broadcastMessage(new Message(Message.GAME_OVER, "NO_RESTART"));
        }
    }

    private void resetGame() {
        board = new GameBoard();
        players[0].setPosition(0);
        players[1].setPosition(0);
        currentTurn = 0;
    }

    private void notifyTurn() {
        ClientHandler current = currentTurn == 0 ? player1 : player2;
        ClientHandler waiting = currentTurn == 0 ? player2 : player1;
        current.sendMessage(new Message(Message.YOUR_TURN, ""));
        waiting.sendMessage(new Message(Message.WAIT, ""));
    }

    private void broadcastMessage(Message message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
    }
}
