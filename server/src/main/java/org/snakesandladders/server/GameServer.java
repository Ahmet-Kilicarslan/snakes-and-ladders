package org.snakesandladders.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameServer {

    private static final int PORT = 5000;
    private static final BlockingQueue<Socket> waitingClients = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        System.out.println("Snakes and Ladders Server starting on port " + PORT);
        startMatchmaker();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                waitingClients.add(clientSocket);
                System.out.println("Clients waiting: " + waitingClients.size());
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void startMatchmaker() {
        Thread matchmaker = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("Matchmaker waiting for 2 players...");
                    Socket player1 = waitingClients.take();
                    System.out.println("Got player 1, waiting for player 2...");
                    Socket player2 = waitingClients.take();
                    System.out.println("Got player 2, starting session...");

                    GameSession session = new GameSession(player1, player2);
                    Thread sessionThread = new Thread(session);
                    sessionThread.start();
                } catch (IOException e) {
                    System.err.println("Matchmaker error: " + e.getMessage());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        matchmaker.setDaemon(true);
        matchmaker.start();
    }
}