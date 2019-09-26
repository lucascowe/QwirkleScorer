package keepitsimple.store.qwirklescorer;

import java.util.ArrayList;

class Player {
    private String name;
    private String latestTurn;
    private int totalScore;
    private int turns;
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

    void deleteTurn() {
        // TODO: need to convert turn to numbers and subtract from total
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
