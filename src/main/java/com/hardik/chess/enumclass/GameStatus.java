package com.hardik.chess.enumclass;

public enum GameStatus {
    INVALID_MOVE("invalid move"),
    CONTINUE("continue"),
    WINNER("winner"),
    KING_ON_CHECK("king on check");

    String value;

    GameStatus(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}
