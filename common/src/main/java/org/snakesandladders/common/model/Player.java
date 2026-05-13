package org.snakesandladders.common.model;

import java.io.Serializable;

public class Player implements Serializable {

    private String name;
    private int position;
    private int playerNumber;

    public Player(String name, int playerNumber) {
        this.name = name;
        this.playerNumber = playerNumber;
        this.position = 0;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}