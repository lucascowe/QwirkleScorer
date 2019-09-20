import java.util.ArrayList;

class Player {
    String name;
    ArrayList<Integer> score;
    int totalScore;

    void Player(String player) {
        name = player;
        score = new ArrayList<>();
        totalScore = 0;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void addScore(int score) {
        this.score.add(score);
        this.totalScore += totalScore;
    }

    public void deleteTurn(int turn) {
        this.score.remove(turn);
    }

    public void modifyTurn(int turn, int newScore) {
        this.score.set(turn, newScore);
    }
}
