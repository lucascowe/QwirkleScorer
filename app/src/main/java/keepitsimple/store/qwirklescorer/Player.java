package keepitsimple.store.qwirklescorer;

import android.util.Log;

import java.util.ArrayList;

class Player {
    String name;
    ArrayList<String> turn;
    int totalScore;
    int turns;
    boolean selected;

    public Player(String player) {
        if (player.isEmpty()) {
            player = "This player needs a name";
        }
        this.name = player;
        this.turn = new ArrayList<>();
        totalScore = 0;
        turns = 0;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn.add(turn);
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(String turn, int totalScore) {
        this.turn.add(turn);
        this.totalScore += totalScore;
    }

    public void deleteTurn(int turn) {
        this.turn.remove(turn);
    }

    public String getLatestTurn() {
        if (this.turn.size() > 0) {
            return (String.valueOf(this.turn.get(this.turn.size() - 1)));
        } else {
            return "";
        }
    }

    public void modifyTurn(int turn, String newScore, int scoreChange) {
        this.turn.set(turn, newScore);
        this.totalScore += scoreChange;
        this.turns++;
        Log.i("db","turns is " + this.turns);
    }

    public int getTurns() {
        return this.turns;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
