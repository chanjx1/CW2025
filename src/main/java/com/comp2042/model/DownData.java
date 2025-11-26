package com.comp2042.model;

public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;
    private final boolean gameOver;

    public DownData(ClearRow clearRow, ViewData viewData, boolean gameOver) {
        this.clearRow = clearRow;
        this.viewData = viewData;
        this.gameOver = gameOver;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}