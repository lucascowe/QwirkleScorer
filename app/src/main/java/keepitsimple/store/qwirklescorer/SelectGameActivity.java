package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;
import static keepitsimple.store.qwirklescorer.MainActivity.mDatabase;

public class SelectGameActivity extends AppCompatActivity implements GameSelectRecAdapter.RecListener{

    private RecyclerView recyclerView;
    static GameSelectRecAdapter gameSelectRecAdapter;
    static Cursor mCursorGames;

    RadioGroup rgFinishWhen, rgFinishBy;
    RadioButton rbFinishWhen, rbFinishBy;
    TextView tvTargetScore, tvStartingScore;
    EditText etStartScore ,etTargetScore;
    CheckBox cbExactly;
    int radiobuttonID;

    int rbSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);

        mCursorGames = refreshGamesCursor();
        tvTargetScore = (TextView) findViewById(R.id.textViewTargetScore);
        etTargetScore = (EditText) findViewById(R.id.editTextTargetScore);
        rgFinishWhen = (RadioGroup) findViewById(R.id.radioGroupFinishWhen);
        rgFinishBy = findViewById(R.id.radioGroupFinishBy);
        cbExactly = (CheckBox) findViewById(R.id.checkBoxExactly);
        etStartScore = findViewById(R.id.editStartingScore);
        cbExactly = findViewById(R.id.checkBoxExactly);
        rgFinishBy = findViewById(R.id.radioGroupFinishBy);

        recyclerView = findViewById(R.id.recyclerViewGameSelect);
        gameSelectRecAdapter =  new GameSelectRecAdapter(mCursorGames, this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(gameSelectRecAdapter);

        initRecycler();
//        mDatabase.execSQL("UPDATE " + GameOptions.TABLE_NAME + " SET " + GameOptions.COLUMN_SELECTED +
//                "=1 WHERE " + GameOptions.COLUMN_NAME + " = 'Custom'");
//        mDatabase.delete(GameOptions.TABLE_NAME,GameOptions.COLUMN_NAME + "= 'Custom'",null);


        logGamesTableContents();

        gameSelectRecAdapter.updateCursor(refreshGamesCursor());
//
//        cv.put(DatabaseNames.GameOptions.COLUMN_SELECTED, 1);
//        gameSelectRecAdapter.updateCursor(refreshGamesCursor());


    }

    private void initRecycler() {
        // link Adapter to
        recyclerView = findViewById(R.id.recyclerViewGameSelect);
        gameSelectRecAdapter = new GameSelectRecAdapter(mCursorGames, this);

        // Set up Recycler manager to link to adapter
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(gameSelectRecAdapter);
    }

    @Override
    public void onRecClick(int position) {
        selectGame(position);
        loadGameData(position);
        gameSelectRecAdapter.updateCursor(refreshGamesCursor());
//        longClickMenu(false);
    }

    @Override
    public boolean onRecLongClick(int position) {
        return false;
    }

    private void selectGame(int position) {
        Log.i("Debug","selecting game in position " + position);
//        if ()
        // clear selection
        mDatabase.execSQL("UPDATE " + GameOptions.TABLE_NAME + " SET " + GameOptions.COLUMN_SELECTED +
                "=0 WHERE " + GameOptions.COLUMN_SELECTED + " = 1");
        // set new selection
        Log.i("RECYCLER POSITION", String.valueOf(position));
        mDatabase.execSQL("UPDATE " + GameOptions.TABLE_NAME + " SET " + GameOptions.COLUMN_SELECTED +
                "=1 WHERE " + GameOptions.COLUMN_NAME + " = '" + gameSelectRecAdapter.getGameName(position) + "'");
    }

    private Cursor refreshGamesCursor() {
        mCursorGames = MainActivity.mDatabase.rawQuery("SELECT * FROM " + DatabaseNames.GameOptions.TABLE_NAME +
                " ORDER BY " + DatabaseNames.GameOptions.COLUMN_NAME + " ASC", null);
        return mCursorGames;
    }

    public void rbClick(View view) {

        int rgSelectID = rgFinishBy.getCheckedRadioButtonId();
        rbFinishBy = findViewById(rgSelectID);
        switch (String.valueOf(rbFinishBy.getTag())) {
            case "Score": //  ID 2131230917
                tvTargetScore.setText("Target Score:");
                tvTargetScore.setVisibility(View.VISIBLE);
                etTargetScore.setVisibility(View.VISIBLE);
                cbExactly.setVisibility(View.VISIBLE);

                break;
            case "Rounds":  //  ID 2131230918
                tvTargetScore.setText("Rounds:");
                tvTargetScore.setVisibility(View.VISIBLE);
                etTargetScore.setVisibility(View.VISIBLE);
                cbExactly.setVisibility(View.INVISIBLE);

                break;
            case "Out":  //  ID 2131230919
                tvTargetScore.setVisibility(View.INVISIBLE);
                etTargetScore.setVisibility(View.INVISIBLE);
                cbExactly.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void loadGameData(int position) {
//        do {
//            Log.i("Games Table","R: " + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_NAME)) +
//                    "\t" + GameOptions.COLUMN_START_SCORE + ":" +mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_START_SCORE)) +
//                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_BY)) +
//                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_WHEN)) +
//                    "\t" + GameOptions.COLUMN_FINISH_QTY + ":" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_QTY)) +
//                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_EXACTLY)) +
//                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_KEYBOARD)) +
//                    "\t" + GameOptions.COLUMN_SELECTED + ":" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_SELECTED)));
//        } while (mCursorGames.moveToNext());
        Log.i("Count", String.valueOf(mCursorGames.getCount()));
        mCursorGames.moveToPosition(position);
        etStartScore.setText(mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_START_SCORE)));
//        Log.i("Load StartScore", mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_START_SCORE)));
        Log.i("Load Finishby", String.valueOf(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_BY))));
        Log.i("Load Finishwhen", String.valueOf(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_WHEN))));
        rbFinishBy = findViewById(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_BY)));
//        Log.i("Load Finishby", String.valueOf(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_BY))));
        rbFinishBy.setChecked(true);
        rbFinishWhen = findViewById(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_WHEN)));
        rbFinishWhen.setChecked(true);
        cbExactly.setChecked(mCursorGames.getInt(mCursorGames.getColumnIndex(GameOptions.COLUMN_EXACTLY)) == 1 ? true : false);
    }

    public void buttonSave(View view) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseNames.GameOptions.COLUMN_NAME, "Qwirkle");
        cv.put(DatabaseNames.GameOptions.COLUMN_START_SCORE, Integer.valueOf(String.valueOf(etStartScore.getText())));
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_BY, rgFinishBy.getCheckedRadioButtonId()); //2131230919); // Out of tiles
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_WHEN, rgFinishWhen.getCheckedRadioButtonId()); //2131230920); // 1st Out
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_QTY, Integer.valueOf(String.valueOf(etTargetScore.getText())));
        cv.put(DatabaseNames.GameOptions.COLUMN_EXACTLY, cbExactly.isChecked() == true ? 1 : 0);
        cv.put(DatabaseNames.GameOptions.COLUMN_KEYBOARD, "Numeric");
        cv.put(DatabaseNames.GameOptions.COLUMN_SELECTED, gameSelectRecAdapter.getItemCount() == 0 ? 1 : 0);
        mDatabase.insert(DatabaseNames.GameOptions.TABLE_NAME,null, cv);
        gameSelectRecAdapter.updateCursor(refreshGamesCursor());
    }

    public void resetDefaultGames(View view) {
        ContentValues cv = new ContentValues();

        cv.put(DatabaseNames.GameOptions.COLUMN_NAME, "Custom");
        cv.put(DatabaseNames.GameOptions.COLUMN_SELECTED, 1);
        mDatabase.insert(DatabaseNames.GameOptions.TABLE_NAME,null, cv);

        cv.put(DatabaseNames.GameOptions.COLUMN_NAME, "Qwirkle");
        cv.put(DatabaseNames.GameOptions.COLUMN_START_SCORE, Integer.valueOf(String.valueOf(etStartScore.getText())));
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_BY, rgFinishBy.getCheckedRadioButtonId()); //2131230919); // Out of tiles
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_WHEN, rgFinishWhen.getCheckedRadioButtonId()); //2131230920); // 1st Out
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_QTY, Integer.valueOf(String.valueOf(etTargetScore.getText())));
//        cv.put(DatabaseNames.GameOptions.COLUMN_EXACTLY, cbExactly.isChecked() == true ? 1 : 0);
        cv.put(DatabaseNames.GameOptions.COLUMN_KEYBOARD, "Qwirkle");
        cv.put(DatabaseNames.GameOptions.COLUMN_SELECTED, 0);
        mDatabase.insert(DatabaseNames.GameOptions.TABLE_NAME,null, cv);

        cv.put(DatabaseNames.GameOptions.COLUMN_NAME, "Dhumbal");
        cv.put(DatabaseNames.GameOptions.COLUMN_START_SCORE, Integer.valueOf(String.valueOf(etStartScore.getText())));
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_BY, rgFinishBy.getCheckedRadioButtonId()); //2131230919); // Out of tiles
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_WHEN, rgFinishWhen.getCheckedRadioButtonId()); //2131230920); // 1st Out
        cv.put(DatabaseNames.GameOptions.COLUMN_FINISH_QTY, Integer.valueOf(String.valueOf(etTargetScore.getText())));
        cv.put(DatabaseNames.GameOptions.COLUMN_EXACTLY, cbExactly.isChecked() == true ? 1 : 0);
        cv.put(DatabaseNames.GameOptions.COLUMN_KEYBOARD, "Numeric");
        cv.put(DatabaseNames.GameOptions.COLUMN_SELECTED, 0);
        mDatabase.insert(DatabaseNames.GameOptions.TABLE_NAME,null, cv);

        gameSelectRecAdapter.updateCursor(refreshGamesCursor());
    }

    public void buttonPlay(View view) {

    }

    private void logGamesTableContents() {
        Log.i("log","Score History log");
        if (!mCursorGames.moveToFirst()) {
            Log.i("log","No history found");
            return;
        }
        do {
            Log.i("Games Table","R: " + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_NAME)) +
                    "\t" + GameOptions.COLUMN_START_SCORE + ":" +mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_START_SCORE)) +
                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_BY)) +
                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_WHEN)) +
                    "\t" + GameOptions.COLUMN_FINISH_QTY + ":" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_FINISH_QTY)) +
                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_EXACTLY)) +
                    "\t" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_KEYBOARD)) +
                    "\t" + GameOptions.COLUMN_SELECTED + ":" + mCursorGames.getString(mCursorGames.getColumnIndex(GameOptions.COLUMN_SELECTED)));
        } while (mCursorGames.moveToNext());
    }




//    private int getSelected() {
//        Cursor c = MainActivity.mDatabase.rawQuery("SELECT * FROM " + DatabaseNames.GameOptions.TABLE_NAME +
//                " WHERE " + DatabaseNames.COLUMN_SELECTED + "=1",null);
//        String s = getSelectedPlayerName();
//        if (c.moveToFirst()) {
//            Log.i("Selected","Player found in location " +
//                    c.getInt(c.getColumnIndex(DatabaseNames.COLUMN_LOCATION)));
//            int location = c.getInt(c.getColumnIndex(DatabaseNames.COLUMN_LOCATION));
//            if (c != null) c.close();
//            return location;
//        } else {
//            Log.i("Error","Could not find selected player, default to 0");
//            if (c != null) c.close();
//            return 0;
//        }
//    }
//
//    private void deletePlayer(){
//        int selected = getSelected();
//        // remove players turn history
//        ContentValues cv = new ContentValues();
//        String gameName = gameSelectRecAdapter.getGameName(selected);
//        Log.i("deletePlayer",Integer.toString(playerNum));
//        cv.put(ScoreHistory.COLUMN_SCORE[playerNum],0);
//        cv.put(ScoreHistory.COLUMN_TURN[playerNum],"");
//        mDatabase.update(ScoreHistory.TABLE_NAME,cv,null,null);
//        // remove player from player table
//        mDatabase.delete(PlayersTable.TABLE_NAME,PlayersTable.COLUMN_LOCATION + "=" + selected,null);
//        recAdapter.updateCursor(refreshPlayerCursor());
//        // move to next players turn
//        if (recAdapter.getItemCount() > 0) {
//            playerTurn = selected % recAdapter.getItemCount();
//            selectPlayer(playerTurn);
//        }
//        // update player locations
//        Cursor c = mDatabase.rawQuery("SELECT * FROM " + PlayersTable.TABLE_NAME +
//                " WHERE " + PlayersTable.COLUMN_LOCATION + " > " + selected +
//                " ORDER BY "+ PlayersTable.COLUMN_LOCATION + " ASC", null);
//        if (c.moveToFirst()) {
//            do {
//                int loc = c.getInt(c.getColumnIndex(PlayersTable.COLUMN_LOCATION));
//                String name = c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME));
//                cv.clear();
//                Log.i("location update","# " + c.getInt(c.getColumnIndex(PlayersTable.COLUMN_NUMBER)) +
//                        " " + name + " loc " + loc + " -> " + (loc - 1));
//                cv.put(PlayersTable.COLUMN_LOCATION, loc - 1);
//                mDatabase.update(PlayersTable.TABLE_NAME, cv, PlayersTable.COLUMN_NUMBER +
//                        " = " + c.getInt(c.getColumnIndex(PlayersTable.COLUMN_NUMBER)),null);
//            } while (c.moveToNext());
//        }
//        if (c != null) c.close();
//        recAdapter.updateCursor(refreshPlayerCursor());
//        recAdapter.logPlayerTableContents();
//        if (recAdapter.getItemCount() == 0) newGame();
//        refreshScoreCursor();
//    }





}