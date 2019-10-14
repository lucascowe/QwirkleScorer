package keepitsimple.store.qwirklescorer;

import java.util.ArrayList;

class Player {
    private String name;
    private String latestTurn;
    private int totalScore;
    private int turns;
    private int location;
    private boolean selected;

    Player(String player) {
        if (player.isEmpty()) {
            player = "Player X";
        }
        this.name = player;
        this.latestTurn = "";
        totalScore = 0;
        turns = 0;
        selected = false;
    }

    public Player(String name, String latestTurn, int totalScore, int turns, int location, boolean selected) {
        this.name = name;
        this.latestTurn = latestTurn;
        this.totalScore = totalScore;
        this.turns = turns;
        this.location = location;
        this.selected = selected;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getTurn() {
        return latestTurn;
    }

    int getTotalScore() {
        return totalScore;
    }

    void addScore(String turn, int totalScore) {
        this.latestTurn = turn;
        this.totalScore += totalScore;
        this.turns++;
    }

    void resetScore() {
        this.totalScore = 0;
        this.latestTurn = "";
        this.turns = 0;
    }

    void deleteTurn(int score) {
        this.totalScore -= score;
        this.turns--;
        this.latestTurn = "";
    }

    int getTurns() {
        return this.turns;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }
}
