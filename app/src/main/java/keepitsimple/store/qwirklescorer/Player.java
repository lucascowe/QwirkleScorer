package keepitsimple.store.qwirklescorer;

class Player {
    private final int number;
    private String name;
    private String latestTurn;
    private int totalScore;
    private int turns;
    private int location;
    private boolean selected;

    Player(int number, String name, String latestTurn, int totalScore, int turns, int location, boolean selected) {
        this.number = number;
        this.name = name;
        this.latestTurn = latestTurn;
        this.totalScore = totalScore;
        this.turns = turns;
        this.location = location;
        this.selected = selected;
    }

    int getNumber() {
        return number;
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

    void deleteTurn(int score, String lastTurn) {
        this.totalScore -= score;
        this.turns--;
        this.latestTurn = lastTurn;
    }

    int getTurns() {
        return this.turns;
    }

    int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    boolean isSelected() {
        return selected;
    }

    void setSelected(boolean selected) {
        this.selected = selected;
    }
}
