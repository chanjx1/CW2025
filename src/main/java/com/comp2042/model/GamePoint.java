package com.comp2042.model;

public class GamePoint {
    private final int x;
    private final int y;

    public GamePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public GamePoint(GamePoint other) {
        this.x = other.x;
        this.y = other.y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public GamePoint translate(int dx, int dy) {
        return new GamePoint(this.x + dx, this.y + dy);
    }
}
