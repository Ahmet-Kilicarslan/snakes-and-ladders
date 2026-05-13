package org.snakesandladders.server;

import org.snakesandladders.common.protocol.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private int playerNumber;
    private GameSession session;
    private String playerName;

    public ClientHandler(Socket socket, int playerNumber, GameSession session) throws IOException {
        this.socket = socket;
        this.playerNumber = playerNumber;
        this.session = session;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(Message message) {
        out.println(message.toString());
    }

    public String readMessage() throws IOException {
        return in.readLine();
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        }
    }
}