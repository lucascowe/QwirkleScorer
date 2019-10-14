package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;
import static keepitsimple.store.qwirklescorer.HistoryActivity.historyRecAdapter;

public class MainActivity extends AppCompatActivity implements RecAdapter.RecListener{

    private int turnScore;
    private int playerTurn;
    private String moveString = "";
    private TextView txvCurrentMove;
    private RecyclerView recyclerView;
    static RecAdapter recAdapter;
    private boolean endGame;
    private static SQLiteDatabase mDatabase;
    static Cursor mCursorPlayers;
    static Cursor mCursorScores;
    private DbHelp dbHelp;
    private Vibrator vibrator;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // adds a new player or rename an existing player
    private void playerName(final Boolean isNewPlayer) {
        if (!isNewPlayer || recAdapter.getItemCount() < 4) {
            final Button btnAdd, btnCancel, btnFinish;
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.add_player);
            final EditText inputEditText;
            inputEditText = dialog.findViewById(R.id.editTextDialogName);
            btnCancel = dialog.findViewById(R.id.btnDialogCancel);
            btnAdd = dialog.findViewById(R.id.btnDialogAdd);
            btnFinish = dialog.findViewById(R.id.btnDialogFinish);
            if (isNewPlayer) {
                inputEditText.setText("Player " + (recAdapter.getItemCount()+1));
                dialog.setTitle("Add player");
                dialog.setCancelable(true);
            } else {
                inputEditText.setText(getSelectedPlayerName());
                dialog.setTitle("Rename Player");
                dialog.setCancelable(true);
                btnFinish.setText("Save");
                btnAdd.setText("Delete");
            }
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isNewPlayer) {
                        deletePlayer();
                        dialog.dismiss();
                    }else {
                        addPlayer(inputEditText.getText().toString());
                        recAdapter.updateCursor(refreshPlayerCursor());
                        dialog.dismiss();
                        playerName(true);
//                        players.get(playerTurn).setSelected(true);
                    }
                }
            });
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNewPlayer) {
                        addPlayer(inputEditText.getText().toString());
                        recAdapter.updateCursor(refreshPlayerCursor());
                        dialog.dismiss();
                    } else {
                        renamePlayer(inputEditText.getText().toString(),getSelected());
//                        players.get(findSelectedPlayer()).setName(inputEditText.getText().toString());
                        Log.i("Player " + getSelected() + " changed to ", inputEditText.getText().toString());
                        recAdapter.updateCursor(refreshPlayerCursor());
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Sorry, Max players is 4", Toast.LENGTH_SHORT).show();
        }
    }

    void newPlayer(View view){
        playerName(true);
        if (recAdapter.getItemCount() == 1) {
            selectPlayer(0);
        }
    }

    void openHistory(View view){
        Intent intent = new Intent(getApplicationContext(),HistoryActivity.class);
        startActivity(intent);
    }

    public void openMenu(View view) {
        Button settingsButton = findViewById(R.id.settingsButton);
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, settingsButton);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContentValues cv;
                switch (item.getItemId()) {
                    case R.id.deleteScore:
                        int p = getSelected();
                        cv = new ContentValues();
                        // clear score from players table
                        cv.put(PlayersTable.COLUMN_TURN,"");
                        cv.put(PlayersTable.COLUMN_SCORE, (recAdapter.getPlayerScore(p) -
                                getLastScore(p)));
                        cv.put(PlayersTable.COLUMN_TURNS,
                                recAdapter.getPlayerTurnNumber(p) - 1);
                        mDatabase.update(PlayersTable.TABLE_NAME, cv,PlayersTable.COLUMN_NUMBER +
                                "=" + p,null);
                        // clear score from player history table
                        cv.clear();
                        cv.put(ScoreHistory.COLUMN_SCORE[p], 0);
                        cv.put(ScoreHistory.COLUMN_TURN[p], "");
                        mDatabase.update(ScoreHistory.TABLE_NAME, cv, ScoreHistory.COLUMN_ROUND +
                                "=" + recAdapter.getPlayerTurnNumber(p),null);
                        recAdapter.updateCursor(refreshPlayerCursor());
                        historyRecAdapter.updateCursor(refreshScoreCursor());
                        break;
                    case R.id.newGame:
                        newGame();
                        break;
                    case R.id.screenLock:
                        if (item.getTitle().equals("Disable Screen Lock")) {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                            item.setTitle("Enable Screen Lock");
                        } else {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
                            item.setTitle("Disable Screen Lock");
                        }
                        break;
                    case R.id.howTo:
                        Intent howToIntent = new Intent(getApplicationContext(),HowToActivity.class);
                        startActivity(howToIntent);
                    default:

                        Log.e("Menu","Invalid menu option");
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void deletePlayer(){
        logScoreTableContents();
        int selected = getSelected();
        // remove players turn history
        ContentValues cv = new ContentValues();
        int playerNum = recAdapter.getPlayerNumber(selected);
        Log.i("deletePlayer",Integer.toString(playerNum));
        cv.put(ScoreHistory.COLUMN_SCORE[playerNum],0);
        cv.put(ScoreHistory.COLUMN_TURN[playerNum],"");
        mDatabase.update(ScoreHistory.TABLE_NAME,cv,null,null);
        // remove player from player table
        mDatabase.delete(PlayersTable.TABLE_NAME,PlayersTable.COLUMN_LOCATION + "=" + selected,null);
        recAdapter.updateCursor(refreshPlayerCursor());
        // move to next players turn
        if (recAdapter.getItemCount() > 0) {
            playerTurn = selected % recAdapter.getItemCount();
            selectPlayer(playerTurn);
        }
        // update player locations
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + PlayersTable.TABLE_NAME +
                " WHERE " + PlayersTable.COLUMN_LOCATION + " > " + selected +
                " ORDER BY "+ PlayersTable.COLUMN_LOCATION + " ASC", null);
        if (c.moveToFirst()) {
            do {
                int loc = c.getInt(c.getColumnIndex(PlayersTable.COLUMN_LOCATION));
                String name = c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME));
                cv.clear();
                Log.i("location update","# " + c.getInt(c.getColumnIndex(PlayersTable.COLUMN_NUMBER)) +
                    " " + name + " loc " + loc + " -> " + (loc - 1));
                cv.put(PlayersTable.COLUMN_LOCATION, loc - 1);
                mDatabase.update(PlayersTable.TABLE_NAME, cv, PlayersTable.COLUMN_NUMBER +
                        " = " + c.getInt(c.getColumnIndex(PlayersTable.COLUMN_NUMBER)),null);
            } while (c.moveToNext());
        }
        if (c != null) c.close();
        recAdapter.updateCursor(refreshPlayerCursor());
        recAdapter.logPlayerTableContents();
        if (recAdapter.getItemCount() == 0) newGame();
        refreshScoreCursor();
    }

    private void newGame() {
        ContentValues cv = new ContentValues();
        cv.put(PlayersTable.COLUMN_TURNS, 0);
        cv.put(PlayersTable.COLUMN_TURN,"");
        cv.put(PlayersTable.COLUMN_SCORE, 0);
        mDatabase.update(PlayersTable.TABLE_NAME,cv,null,null);
        mDatabase.delete(ScoreHistory.TABLE_NAME,null,null);
        recAdapter.updateCursor(refreshPlayerCursor());
        refreshScoreCursor();
        Log.i("newGame","player scores cleared");
//        recAdapter.logPlayerTableContents();
    }

    private int getLastScore(int position) {
        if (mCursorScores.moveToLast()) {
            return mCursorScores.getInt(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[position]));
        }
        return 0;
    }

    private void recordTurn() {
        // check if round exists
        int selectedPlayerTurn = recAdapter.getPlayerTurnNumber(getSelected()) + 1;
        ContentValues cv = new ContentValues();
        cv.put(PlayersTable.COLUMN_TURN, moveString);
        cv.put(PlayersTable.COLUMN_SCORE, recAdapter.getPlayerScore(getSelected()) + turnScore);
        cv.put(PlayersTable.COLUMN_TURNS, selectedPlayerTurn);
        mDatabase.update(PlayersTable.TABLE_NAME,cv,PlayersTable.COLUMN_LOCATION + "=" +
                getSelected(),null);
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + ScoreHistory.TABLE_NAME +
                " WHERE " + ScoreHistory.COLUMN_ROUND + "=" + selectedPlayerTurn, null);
        if (c.moveToFirst()) {
            cv.clear();
            cv.put(ScoreHistory.COLUMN_SCORE[recAdapter.getPlayerNumber(getSelected())], turnScore);
            cv.put(ScoreHistory.COLUMN_TURN[recAdapter.getPlayerNumber(getSelected())], moveString);
            mDatabase.update(ScoreHistory.TABLE_NAME,cv,ScoreHistory.COLUMN_ROUND + "=" +
                    selectedPlayerTurn,null);
        } else {
            cv.clear();
            cv.put(ScoreHistory.COLUMN_ROUND, selectedPlayerTurn);
            cv.put(ScoreHistory.COLUMN_SCORE[recAdapter.getPlayerNumber(getSelected())], turnScore);
            cv.put(ScoreHistory.COLUMN_TURN[recAdapter.getPlayerNumber(getSelected())], moveString);
            mDatabase.insert(ScoreHistory.TABLE_NAME,null,cv);
        }
        if (c != null) c.close();
        if (!endGame) {
            playerTurn = (playerTurn + 1) % recAdapter.getItemCount();
            selectPlayer(playerTurn);
            recAdapter.updateCursor(refreshPlayerCursor());
            turnScore = 0;
            moveString = "";
            txvCurrentMove.setText("");
        } else {
            recAdapter.notifyDataSetChanged();
            TextView winnerTextView = findViewById(R.id.winnerTextView);
            Button qwirkleButton = findViewById(R.id.button5);
            Button saveButton = findViewById(R.id.button8);
            Button lastTileButton = findViewById(R.id.button4);
            winnerTextView.setText(whoWins());
            winnerTextView.setVisibility(View.VISIBLE);
            qwirkleButton.setText("Play Again");
            saveButton.setText("Exit");
            lastTileButton.setVisibility(View.INVISIBLE);
            endGame = true;
        }
        recAdapter.updateCursor(refreshPlayerCursor());
        refreshScoreCursor();
    }

    private String whoWins() {
        String winner = "";
        Cursor c = mDatabase.rawQuery("SELECT " + PlayersTable.COLUMN_NAME +
                " FROM " + PlayersTable.TABLE_NAME + " WHERE " +
                PlayersTable.COLUMN_SCORE + "= (SELECT MAX(" + PlayersTable.COLUMN_SCORE +
                ") FROM " + PlayersTable.TABLE_NAME + ")",null);
        if (c.getCount() > 1) {
            for (int ii = 0; ii < c.getCount(); ii++) {
                c.moveToPosition(ii);
                if (ii == 0) winner = c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME));
                if (ii > 0 && ii < c.getCount() - 1) winner += ", " +
                        c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME));
                else winner += "and " + c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME)) +
                        " are the Winners!!";
            }
        } else {
            winner += c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME)) + " Wins!!";
        }
        if (c != null) c.close();
        return winner;
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    endGame = true;
                    turnScore += 6;
                    // if not first move, add +
                    if (moveString.length() > 0) {
                        moveString += " + " + 6;
                    } else {
                        moveString = String.valueOf(6);
                    }
                    txvCurrentMove.setText(moveString);
                    recordTurn();
                    vibrator.vibrate(2000);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    void onClick (View view) {
        Button button = (Button) view;
        int num = Integer.parseInt(button.getTag().toString());
//        MP = MediaPlayer.create(this,R.raw.buttonpress);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        switch (num) {
            case 2:
//                dbHelp.reset(mDatabase);
//                recAdapter.updateCursor(refreshPlayerCursor());
            case 3:
            case 4:
            case 5:
//                MP.start();
                vibrator.vibrate(50);
                turnScore += num;
                // if not first move, add +
                if (moveString.length() > 0) {
                    moveString += " + " + (num);
                } else {
                    moveString = String.valueOf(num);
                }
                txvCurrentMove.setText(moveString);
                break;
            // Last Tile Played
            case 6:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This means a player has played their last tile and the game " +
                        "is over, they will get a bonus 6 points.  Is this correct?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            // Qwirkle
            case 12:
                Button qwirkleButton = findViewById(R.id.button5);
                if(!endGame) {
//                    MP.start();
                    vibrator.vibrate(50);
                    turnScore += num;
                    // if not first move, add +
                    if (moveString.length() > 0) {
                        moveString += " + QWIRKLE";
                    } else {
                        moveString = "QWIRKLE";
                    }
                    txvCurrentMove.setText(moveString);
                } else {
                    TextView winnerTextView = findViewById(R.id.winnerTextView);
                    Button saveButton = findViewById(R.id.button8);
                    Button lastTileButton = findViewById(R.id.button4);
                    winnerTextView.setVisibility(View.INVISIBLE);
                    qwirkleButton.setText("QWIRKLE");
                    saveButton.setText("SAVE");
                    lastTileButton.setVisibility(View.VISIBLE);
                    recAdapter.notifyDataSetChanged();
                    moveString = "";
                    turnScore = 0;
                    endGame = false;
                }
                break;
            // save
            case 7:
                Button saveButton = findViewById(R.id.button8);
                if (!endGame) {
                    if (turnScore > 0) {
                        vibrator.vibrate(50);
                        recordTurn();
                    }
                } else {
                    finish();
                    System.exit(0);
                }
                break;
            // clear
            case 8:
                moveString = "";
                turnScore = 0;
                vibrator.vibrate(50);
                break;
            // delete
            case 9:
                vibrator.vibrate(50);
                String[] turns = moveString.split(" ",48);
                moveString = "";
                turnScore = 0;
                for (int i = 0; i < turns.length - 1; i+= 2) {
                    if (i == 0) {
                        moveString = String.format("%s",turns[i]);
                    } else {
                        moveString = String.format("%s + %s", moveString, turns[i]);
                    }
                    if (turns[i].equals("QWIRKLE")) {
                        turnScore += 12;
                    } else {
                        Log.i("debug","passing " + turns[i]);
                        turnScore += Integer.parseInt(turns[i]);
                    }
                }
                break;
            default:
                Log.i("Error","Invalid button tag");
        }
        txvCurrentMove.setText(moveString);
    }

    private void initRecycler() {
        // link Adapter to list
        recyclerView = findViewById(R.id.recyclerView);
        recAdapter = new RecAdapter(mCursorPlayers,this);

        // Set up Recycler manager to link to adapter
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(recAdapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txvCurrentMove = findViewById(R.id.textView);
        txvCurrentMove.setText("");

        dbHelp = new DbHelp(this);
        mDatabase = dbHelp.getWritableDatabase();
        mCursorPlayers = refreshPlayerCursor();
        mCursorScores = refreshScoreCursor();

        initRecycler();
        playerName(true);
    }

    private Cursor refreshPlayerCursor() {
        mCursorPlayers = mDatabase.rawQuery("SELECT * FROM " + PlayersTable.TABLE_NAME +
                " ORDER BY " + PlayersTable.COLUMN_LOCATION + " ASC", null);
        return mCursorPlayers;
    }

    private Cursor refreshScoreCursor() {
        mCursorScores = mDatabase.rawQuery("SELECT * FROM " + ScoreHistory.TABLE_NAME, null);
        return mCursorScores;
    }

    @Override
    public void onBackPressed() {
        super.onPause();
        super.onBackPressed();
    }

    @Override
    public boolean onRecClick(int position) {
        selectPlayer(position);
        recAdapter.updateCursor(refreshPlayerCursor());
        playerTurn = position;
        return true;
    }

    private int getAvailablePlayerNumber() {
        int ii;
        Log.i("Available","getting player number");
        Cursor c = mDatabase.rawQuery("SELECT " + PlayersTable.COLUMN_NUMBER +
                " FROM " + PlayersTable.TABLE_NAME + " ORDER BY " + PlayersTable.COLUMN_NUMBER +
                " ASC", null);
        if (!c.moveToFirst()) {
            if (c != null) c.close();
            return 0;
        }
        for (ii = 0; ii < 4; ii++) {
            if (c.getInt(c.getColumnIndex(PlayersTable.COLUMN_NUMBER)) != ii) {
                Log.i("Available player number","Found " + ii);
                return ii;
            } else {
                if (!c.moveToNext()) {
                    return (ii + 1);
                }
            }
        }
        Log.i("Available player number","Last resort " + ii);
        if (c != null) c.close();
        return ii;
    }

    private void addPlayer(String name) {
        recAdapter.logPlayerTableContents();
        ContentValues cv = new ContentValues();
        cv.put(PlayersTable.COLUMN_NUMBER, getAvailablePlayerNumber());
        cv.put(PlayersTable.COLUMN_LOCATION, recAdapter.getItemCount());
        cv.put(PlayersTable.COLUMN_NAME, name);
        cv.put(PlayersTable.COLUMN_TURN, "");
        cv.put(PlayersTable.COLUMN_TURNS, 0);
        cv.put(PlayersTable.COLUMN_SCORE, 0);
        cv.put(PlayersTable.COLUMN_SELECTED, recAdapter.getItemCount() == 0 ? 1 : 0);
        mDatabase.insert(PlayersTable.TABLE_NAME,null, cv);
        recAdapter.updateCursor(refreshPlayerCursor());
        recAdapter.logPlayerTableContents();
    }

    private void renamePlayer(String name, int position) {
        mDatabase.execSQL("UPDATE " + PlayersTable.TABLE_NAME + " SET " + PlayersTable.COLUMN_NAME +
                " = '" + name + "' WHERE " + PlayersTable.COLUMN_NUMBER + "=" + recAdapter.getPlayerNumber(getSelected()));
    }

    private void selectPlayer(int position) {
        Log.i("Debug","selecting player in position " + position);
        // clear selection
        mDatabase.execSQL("UPDATE " + PlayersTable.TABLE_NAME + " SET " + PlayersTable.COLUMN_SELECTED +
                "=0 WHERE " + PlayersTable.COLUMN_SELECTED + " = 1");
        // set new selection
        mDatabase.execSQL("UPDATE " + PlayersTable.TABLE_NAME + " SET " + PlayersTable.COLUMN_SELECTED +
                "=1 WHERE " + PlayersTable.COLUMN_NUMBER + " = " + recAdapter.getPlayerNumber(position));
    }

    private int getSelected() {
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + PlayersTable.TABLE_NAME +
                " WHERE " + PlayersTable.COLUMN_SELECTED + "=1",null);
        String s = getSelectedPlayerName();
          if (c.moveToFirst()) {
            Log.i("Selected","Player found in location " +
                    c.getInt(c.getColumnIndex(PlayersTable.COLUMN_LOCATION)));
            int location = c.getInt(c.getColumnIndex(PlayersTable.COLUMN_LOCATION));
            if (c != null) c.close();
            return location;
        } else {
            Log.i("Error","Could not find selected player, default to 0");
            if (c != null) c.close();
            return 0;
        }
    }

    private String getSelectedPlayerName() {
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + PlayersTable.TABLE_NAME +
                " WHERE " + PlayersTable.COLUMN_SELECTED + "=1",null);
        if (c.moveToFirst()) {
            String name = c.getString(c.getColumnIndex(PlayersTable.COLUMN_NAME));
            if (c != null) c.close();
            return name;
        } else {
            return "";
        }
    }

    @Override
    public void onRecLongClick(int position) {
        selectPlayer(position);
        recAdapter.updateCursor(refreshPlayerCursor());
        playerTurn = position;
        playerName(false);
    }

    private void logScoreTableContents() {
        Log.i("log","Score History log");
        if (!mCursorScores.moveToFirst()) {
            Log.i("log","No history found");
            return;
        }
        do {
            Log.i("Score Table","R: " + mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_ROUND)) +
                    "\t" + mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[0])) +
                    "\t" + mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[1])) +
                    "\t" + mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[2])) +
                    "\t" + mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[3])));
        } while (mCursorScores.moveToNext());
    }

}



/*
Todo:
L 1. Save data
A 2. Load Menu from button and change S to cog icon - Done but still need cog icon
A 3. remove menu line (if it works) - Done
FIXED L 4. fix score reset bug
L 5. Edit last score
6. Edit score history
A 7. Remove starting players - Done
A 8. Player X on add player - Done
A 9. sound / haptic feedback on button press - Done
L 10. Winning player message logic

// TODO: Add random select for who starts?

 */