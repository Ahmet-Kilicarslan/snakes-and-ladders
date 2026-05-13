package org.snakesandladders.common.model;

public class MoveResult {

    public enum EventType {
        NORMAL,
        SNAKE,
        LADDER,
        BOUNCED,
        WIN
    }

    private int diceRoll;
    private int fromPosition;
    private int toPosition;
    private EventType eventType;

    public MoveResult(int diceRoll, int fromPosition, int toPosition, EventType eventType) {
        this.diceRoll = diceRoll;
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.eventType = eventType;
    }

    public int getDiceRoll() {
        return diceRoll;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String toNetworkString() {
        return diceRoll + "," + fromPosition + "," + toPosition + "," + eventType.name();
    }

    public static MoveResult fromNetworkString(String raw) {
        String[] parts = raw.split(",");
        int diceRoll = Integer.parseInt(parts[0]);
        int fromPosition = Integer.parseInt(parts[1]);
        int toPosition = Integer.parseInt(parts[2]);
        EventType eventType = EventType.valueOf(parts[3]);
        return new MoveResult(diceRoll, fromPosition, toPosition, eventType);
    }
}