package org.snakesandladders.common.protocol;

import java.io.Serializable;

public class Message implements Serializable {

    public static final String ROLL_DICE = "ROLL_DICE";
    public static final String POSITION_UPDATE = "POSITION_UPDATE";
    public static final String GAME_OVER = "GAME_OVER";
    public static final String WAIT = "WAIT";
    public static final String YOUR_TURN = "YOUR_TURN";
    public static final String PLAYER_ASSIGNED = "PLAYER_ASSIGNED";
    public static final String OPPONENT_DISCONNECTED = "OPPONENT_DISCONNECTED";
    public static final String RESTART_REQUEST = "RESTART_REQUEST";
    public static final String RESTART_ACCEPTED = "RESTART_ACCEPTED";
    public static final String CHAT = "CHAT";

    private String type;
    private String data;

    public Message(String type, String data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public static Message parse(String raw) {
        int index = raw.indexOf(":");
        if (index == -1) {
            return new Message(raw, "");
        }
        String type = raw.substring(0, index);
        String data = raw.substring(index + 1);
        return new Message(type, data);
    }

    @Override
    public String toString() {
        return type + ":" + data;
    }
}