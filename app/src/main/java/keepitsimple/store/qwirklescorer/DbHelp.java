package keepitsimple.store.qwirklescorer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;

class DbHelp extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "qwirkle_game.db";
    private static final int VERSION = 1;

    public DbHelp(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PlayersTable.TABLE_NAME + " (" +
                PlayersTable.COLUMN_NUMBER + " INT(1), " +
                PlayersTable.COLUMN_LOCATION + " INT(1), " +
                PlayersTable.COLUMN_NAME + " VARCHAR, " +
                PlayersTable.COLUMN_SCORE + " INT(4), " +
                PlayersTable.COLUMN_TURNS + " INT(3), " +
                PlayersTable.COLUMN_TURN + " VARCHAR, " +
                PlayersTable.COLUMN_SELECTED + " INT(1))");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + ScoreHistory.TABLE_NAME + " (" +
                ScoreHistory.COLUMN_ROUND + " INT(3), " +
                ScoreHistory.COLUMN_SCORE[0] + " INT(4), " +
                ScoreHistory.COLUMN_TURN[0] + " VARCHAR, " +
                ScoreHistory.COLUMN_SCORE[1] + " INT(4), " +
                ScoreHistory.COLUMN_TURN[1] + " VARCHAR, " +
                ScoreHistory.COLUMN_SCORE[2] + " INT(4), " +
                ScoreHistory.COLUMN_TURN[2] + " VARCHAR, " +
                ScoreHistory.COLUMN_SCORE[3] + " INT(4), " +
                ScoreHistory.COLUMN_TURN[3] + " VARCHAR)");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + GameOptions.TABLE_NAME + " (" +
                GameOptions.COLUMN_NAME + " VARCHAR, " +
                GameOptions.COLUMN_START_SCORE + " INT(4), " +
                GameOptions.COLUMN_FINISH_BY + " INT(5), " +
                GameOptions.COLUMN_FINISH_WHEN + " INT(5), " +
                GameOptions.COLUMN_FINISH_QTY + " INT(4), " +
                GameOptions.COLUMN_EXACTLY + " INT(1), " +
                GameOptions.COLUMN_KEYBOARD + " VARCHAR, " +
                GameOptions.COLUMN_SELECTED + " INT(1))");
    }

    public void updateGameOptions(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + GameOptions.TABLE_NAME);
        db.execSQL("CREATE TABLE IF NOT EXISTS " + GameOptions.TABLE_NAME + " (" +
                GameOptions.COLUMN_NAME + " VARCHAR, " +
                GameOptions.COLUMN_START_SCORE + " INT(4), " +
                GameOptions.COLUMN_FINISH_BY + " INT(5), " +
                GameOptions.COLUMN_FINISH_WHEN + " INT(5), " +
                GameOptions.COLUMN_FINISH_QTY + " INT(4), " +
                GameOptions.COLUMN_EXACTLY + " INT(1), " +
                GameOptions.COLUMN_KEYBOARD + " VARCHAR, " +
                GameOptions.COLUMN_SELECTED + " INT(1))");

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        Log.i("DELETE", "DROPING TABLES");
        db.execSQL("DROP TABLE IF EXISTS " + PlayersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ScoreHistory.TABLE_NAME);
        onCreate(db);
    }

    void reset(SQLiteDatabase db) {
        Log.i("DELETE", "DROPING TABLES");
        db.execSQL("DROP TABLE IF EXISTS " + PlayersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ScoreHistory.TABLE_NAME);
        onCreate(db);
    }
}
