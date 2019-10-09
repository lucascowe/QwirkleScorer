package keepitsimple.store.qwirklescorer;

import android.provider.BaseColumns;

public class DatabaseNames {

    public DatabaseNames() {
    }

    public static final class PlayersTable implements BaseColumns {
        public static final String TABLE_NAME = "players";
        public static final String COLUMN_NUMBER = "player_number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TURN = "latest_turn";
        public static final String COLUMN_TURNS = "turns";
        public static final String COLUMN_SELECTED = "selected";
    }

    public static final class ScoreHistory implements BaseColumns {
        public static final String TABLE_NAME = "score_history";
        public static final String COLUMN_ROUND = "round";
        public static final String[] COLUMN_SCORE = {"p1score","p2score","p3score","p4score"};
        public static final String[] COLUMN_TURN = {"p1turn","p2turn","p3turn","p4turn"};
    }

    public static final class Returns {
        public static final int FAIL = -1;
        public static final int SUCCESS = 1;
    }
}