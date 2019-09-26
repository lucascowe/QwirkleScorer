package keepitsimple.store.qwirklescorer;

class RoundScore {
    private String turn[];
    private int score[];
    final int maxPlayers = 4;

    RoundScore() {
        turn = new String[maxPlayers];
        score = new int [maxPlayers];
    }

    void addScore(int player, int score, String turn) {
        if (player < maxPlayers) {
            this.turn[player] = turn;
            this.score[player] = score;
        }
    }

    int getScore(int player) {
        if (player < maxPlayers) {
            return this.score[player];
        }
        return -1;
    }

    String getTurn(int player) {
        if (player < maxPlayers) {
            return this.turn[player];
        }
        return "";
    }
}
