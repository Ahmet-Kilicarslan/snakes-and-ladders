package org.snakesandladders.client.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.snakesandladders.common.model.GameBoard;

import java.util.Map;

public class BoardRenderer {

    private static final int BOARD_SIZE = 10;
    private static final Color COLOR_LIGHT_CELL = Color.web("#F0E6D3");
    private static final Color COLOR_DARK_CELL = Color.web("#C8A97E");
    private static final Color COLOR_SNAKE_HEAD = Color.web("#E74C3C");
    private static final Color COLOR_SNAKE_TAIL = Color.web("#E74C3C");
    private static final Color COLOR_LADDER_BASE = Color.web("#2ECC71");
    private static final Color COLOR_LADDER_TOP = Color.web("#2ECC71");
    private static final Color COLOR_PLAYER1 = Color.web("#3498DB");
    private static final Color COLOR_PLAYER2 = Color.web("#E67E22");
    private static final Color COLOR_TEXT = Color.web("#2C3E50");

    private Canvas canvas;
    private GraphicsContext gc;
    private double cellSize;
    private GameBoard board;

    public BoardRenderer(Canvas canvas, GameBoard board) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.board = board;
        this.cellSize = canvas.getWidth() / BOARD_SIZE;
    }

    public void render(int player1Position, int player2Position) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCells();
        drawSnakes();
        drawLadders();
        drawPlayers(player1Position, player2Position);
    }

    private void drawCells() {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int cellNumber = getCellNumber(row, col);
                double x = col * cellSize;
                double y = row * cellSize;

                if ((row + col) % 2 == 0) {
                    gc.setFill(COLOR_LIGHT_CELL);
                } else {
                    gc.setFill(COLOR_DARK_CELL);
                }

                gc.fillRect(x, y, cellSize, cellSize);
                gc.setStroke(Color.web("#BDC3C7"));
                gc.strokeRect(x, y, cellSize, cellSize);

                gc.setFill(COLOR_TEXT);
                gc.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.22));
                gc.fillText(String.valueOf(cellNumber),
                        x + cellSize * 0.08,
                        y + cellSize * 0.28);
            }
        }
    }

    private void drawSnakes() {
        for (Map.Entry<Integer, Integer> snake : board.getSnakes().entrySet()) {
            int head = snake.getKey();
            int tail = snake.getValue();
            double[] headCoords = getCellCenter(head);
            double[] tailCoords = getCellCenter(tail);

            gc.setStroke(COLOR_SNAKE_HEAD);
            gc.setLineWidth(cellSize * 0.12);
            gc.strokeLine(headCoords[0], headCoords[1], tailCoords[0], tailCoords[1]);

            gc.setFill(COLOR_SNAKE_HEAD);
            gc.fillOval(headCoords[0] - cellSize * 0.15,
                    headCoords[1] - cellSize * 0.15,
                    cellSize * 0.30,
                    cellSize * 0.30);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.18));
            gc.fillText("S", headCoords[0] - cellSize * 0.07, headCoords[1] + cellSize * 0.07);
        }
    }

    private void drawLadders() {
        for (Map.Entry<Integer, Integer> ladder : board.getLadders().entrySet()) {
            int base = ladder.getKey();
            int top = ladder.getValue();
            double[] baseCoords = getCellCenter(base);
            double[] topCoords = getCellCenter(top);

            gc.setStroke(COLOR_LADDER_BASE);
            gc.setLineWidth(cellSize * 0.10);
            gc.strokeLine(baseCoords[0], baseCoords[1], topCoords[0], topCoords[1]);

            gc.setFill(COLOR_LADDER_BASE);
            gc.fillOval(baseCoords[0] - cellSize * 0.15,
                    baseCoords[1] - cellSize * 0.15,
                    cellSize * 0.30,
                    cellSize * 0.30);

            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.18));
            gc.fillText("L", baseCoords[0] - cellSize * 0.07, baseCoords[1] + cellSize * 0.07);
        }
    }

    private void drawPlayers(int player1Position, int player2Position) {
        if (player1Position > 0) {
            double[] coords = getCellCenter(player1Position);
            gc.setFill(COLOR_PLAYER1);
            gc.fillOval(coords[0] - cellSize * 0.22,
                    coords[1] - cellSize * 0.10,
                    cellSize * 0.28,
                    cellSize * 0.28);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.16));
            gc.fillText("P1",
                    coords[0] - cellSize * 0.20,
                    coords[1] + cellSize * 0.08);
        }

        if (player2Position > 0) {
            double[] coords = getCellCenter(player2Position);
            gc.setFill(COLOR_PLAYER2);
            gc.fillOval(coords[0] + cellSize * 0.02,
                    coords[1] - cellSize * 0.10,
                    cellSize * 0.28,
                    cellSize * 0.28);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, cellSize * 0.16));
            gc.fillText("P2",
                    coords[0] + cellSize * 0.04,
                    coords[1] + cellSize * 0.08);
        }
    }

    private int getCellNumber(int row, int col) {
        int rowFromBottom = BOARD_SIZE - 1 - row;
        if (rowFromBottom % 2 == 0) {
            return rowFromBottom * BOARD_SIZE + col + 1;
        } else {
            return rowFromBottom * BOARD_SIZE + (BOARD_SIZE - col);
        }
    }

    private double[] getCellCenter(int cellNumber) {
        int rowFromBottom = (cellNumber - 1) / BOARD_SIZE;
        int col;
        if (rowFromBottom % 2 == 0) {
            col = (cellNumber - 1) % BOARD_SIZE;
        } else {
            col = BOARD_SIZE - 1 - (cellNumber - 1) % BOARD_SIZE;
        }
        int row = BOARD_SIZE - 1 - rowFromBottom;
        double x = col * cellSize + cellSize / 2;
        double y = row * cellSize + cellSize / 2;
        return new double[]{x, y};
    }
}