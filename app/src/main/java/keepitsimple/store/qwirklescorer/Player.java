package keepitsimple.store.qwirklescorer;

import java.util.ArrayList;

class Player {
    private String name;
    private ArrayList<String> turn;
    private int totalScore;
    private int turns;
    private boolean selected;

    Player(String player) {
        if (player.isEmpty()) {
            player = "Player X";
        }
        this.name = player;
        this.turn = new ArrayList<>();
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

    String getTurn(int p) {
        return turn.get(p);
    }

    int getTotalScore() {
        return totalScore;
    }

    void addScore(String turn, int totalScore) {
        this.turn.add(turn);
        this.totalScore += totalScore;
        this.turns++;
    }

    void deleteTurn(int turn) {
        this.turn.remove(turn);
    }

    String getLatestTurn() {
        if (this.turn.size() > 0) {
            return (String.valueOf(this.turn.get(this.turn.size() - 1)));
        } else {
            return "";
        }
    }

    void modifyTurn(int turn, String newScore, int scoreChange) {
        this.turn.set(turn, newScore);
        this.totalScore += scoreChange;
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
