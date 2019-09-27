package keepitsimple.store.qwirklescorer;

class RoundScore {
    private String turn[];
    private int score[];
    final int maxPlayers = 4;

    RoundScore() {
        turn = new String[maxPlayers];
        for (String s : turn) {
            s = "";
        }
        score = new int [maxPlayers];
    }

    void addScore(int player, int score, String turn) {
        if (player < maxPlayers) {
            this.turn[player] = turn;
            this.score[player] = score;
        }
    }

    void clearScore(int position) {
        int ii = 0;
        for (ii = position; ii < maxPlayers - 1; ii++) {
            this.score[ii] = this.score[ii + 1];
            this.turn[ii] = this.turn[ii + 1];
        }
        this.score[ii] = 0;
        this.turn[ii] = "";
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
