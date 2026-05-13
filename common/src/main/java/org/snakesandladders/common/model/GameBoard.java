package org.snakesandladders.common.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GameBoard implements Serializable {

    public static final int BOARD_SIZE = 100;

    private Map<Integer, Integer> snakes;
    private Map<Integer, Integer> ladders;

    public GameBoard() {
        snakes = new HashMap<>();
        ladders = new HashMap<>();
        initializeSnakes();
        initializeLadders();
    }

    private void initializeSnakes() {
        snakes.put(99, 21);
        snakes.put(90, 48);
        snakes.put(82, 42);
        snakes.put(74, 53);
        snakes.put(64, 25);
        snakes.put(54, 34);
        snakes.put(46, 6);
        snakes.put(32, 12);
    }

    private void initializeLadders() {
        ladders.put(2, 38);
        ladders.put(7, 14);
        ladders.put(8, 31);
        ladders.put(15, 26);
        ladders.put(21, 42);
        ladders.put(28, 84);
        ladders.put(36, 44);
        ladders.put(51, 67);
        ladders.put(71, 91);
        ladders.put(78, 98);
    }

    public MoveResult applyMove(int currentPosition, int diceRoll) {
        int newPosition = currentPosition + diceRoll;

        if (newPosition > BOARD_SIZE) {
            return new MoveResult(diceRoll, currentPosition, currentPosition, MoveResult.EventType.BOUNCED);
        }

        if (snakes.containsKey(newPosition)) {
            int slidePosition = snakes.get(newPosition);
            return new MoveResult(diceRoll, currentPosition, slidePosition, MoveResult.EventType.SNAKE);
        }

        if (ladders.containsKey(newPosition)) {
            int climbPosition = ladders.get(newPosition);
            return new MoveResult(diceRoll, currentPosition, climbPosition, MoveResult.EventType.LADDER);
        }

        if (newPosition == BOARD_SIZE) {
            return new MoveResult(diceRoll, currentPosition, newPosition, MoveResult.EventType.WIN);
        }

        return new MoveResult(diceRoll, currentPosition, newPosition, MoveResult.EventType.NORMAL);
    }

    public boolean isSnakeHead(int position) {
        return snakes.containsKey(position);
    }

    public boolean isLadderBase(int position) {
        return ladders.containsKey(position);
    }

    public Map<Integer, Integer> getSnakes() {
        return snakes;
    }

    public Map<Integer, Integer> getLadders() {
        return ladders;
    }

    public boolean isWinningPosition(int position) {
        return position == BOARD_SIZE;
    }
}