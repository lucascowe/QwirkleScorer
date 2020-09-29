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

public class SelectGameActivity extends AppCompatActivity implements GameSelectRecAdapter.RecListener{

    private RecyclerView recyclerView;
    static GameSelectRecAdapter gameSelectRecAdapter;
    static Cursor mCursorGames;

    RadioGroup rg;
    RadioButton rb;
    TextView tvTarget;
    EditText etTarget;
    CheckBox cbExactly;
    int radiobuttonID;

    int rbSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game);
        tvTarget = (TextView) findViewById(R.id.textViewTargetScore);
        etTarget = (EditText) findViewById(R.id.editTextTargetScore);

        recyclerView = findViewById(R.id.recyclerViewGameSelect);
//        gameSelectRecAdapter =  new RecAdapter(mCursorGames, this);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(gameSelectRecAdapter);

        initRecycler();
    }

    private void initRecycler() {
        // link Adapter to
        recyclerView = findViewById(R.id.recyclerViewGameSelect);
        gameSelectRecAdapter = new GameSelectRecAdapter(MainActivity.mCursorGames, this);

        // Set up Recycler manager to link to adapter
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(gameSelectRecAdapter);
    }

    @Override
    public void onRecClick(int position) {

    }

    @Override
    public boolean onRecLongClick(int position) {
        return false;
    }




    public void rbClick(View view) {
        RadioGroup rg = findViewById(R.id.radioGroupFinishBy);
        radiobuttonID = rg.getCheckedRadioButtonId();
        rb = (RadioButton) findViewById(radiobuttonID);
        cbExactly = (CheckBox) findViewById(R.id.checkBoxExactly);
//        Toast.makeText(this, String.valueOf(rb.getTag()), Toast.LENGTH_LONG).show();

        switch (String.valueOf(rb.getTag())) {
            case "Score": //  ID 2131230917
                Toast.makeText(this, String.valueOf(radiobuttonID), Toast.LENGTH_LONG).show();
                tvTarget.setText("Target Score:");
                tvTarget.setVisibility(View.VISIBLE);
                etTarget.setVisibility(View.VISIBLE);
                cbExactly.setVisibility(View.VISIBLE);
                etTarget.setText(String.valueOf(radiobuttonID));
                break;
            case "Rounds":  //  ID 2131230918
                Toast.makeText(this, String.valueOf(radiobuttonID), Toast.LENGTH_LONG).show();
                tvTarget.setText("Rounds:");
                tvTarget.setVisibility(View.VISIBLE);
                cbExactly.setVisibility(View.INVISIBLE);
                etTarget.setText(String.valueOf(radiobuttonID));
                break;
            case "Out":  //  ID 2131230919
                Toast.makeText(this, String.valueOf(radiobuttonID), Toast.LENGTH_LONG).show();
                tvTarget.setVisibility(View.INVISIBLE);
                etTarget.setVisibility(View.INVISIBLE);
                cbExactly.setVisibility(View.INVISIBLE);
                etTarget.setText(String.valueOf(radiobuttonID));
                rbSaved = radiobuttonID;
                break;
        }
    }

    public void buttonSave(View view) {
        RadioButton rbSelected = (RadioButton) findViewById(rbSaved);
        Toast.makeText(this, String.valueOf(rbSaved), Toast.LENGTH_LONG).show();
        rbSelected.setChecked(true);
//        etTarget.setText(String.valueOf());


        cbExactly = (CheckBox) findViewById(R.id.checkBoxExactly);
        boolean exactly = cbExactly.isChecked();
        Toast.makeText(this, String.valueOf(exactly), Toast.LENGTH_LONG).show();
    }

    public void buttonPlay(View view) {

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