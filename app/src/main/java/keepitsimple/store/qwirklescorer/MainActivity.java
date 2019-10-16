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
import android.widget.GridLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;

public class MainActivity extends AppCompatActivity implements RecAdapter.RecListener{

    private int turnScore;
    private int playerTurn;
    private String turnString = "";
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
    private void longClickMenu(final Boolean isNewPlayer) {
        if (!isNewPlayer || recAdapter.getItemCount() < 4) {
            final Button btnDeletePlayer, btnCancel, btnSave, btnEdit, btnDelete;
            final GridLayout gridLayout;
            final TextView headerTextView;
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.add_player);
            final EditText inputEditText;
            inputEditText = dialog.findViewById(R.id.editTextDialogName);
            gridLayout = dialog.findViewById(R.id.gridview);
            btnDeletePlayer = dialog.findViewById(R.id.btnDeletePlayer);
            btnSave = dialog.findViewById(R.id.btnSave);
            btnEdit = dialog.findViewById(R.id.btnEdit);
            btnDelete = dialog.findViewById(R.id.btnDelete);
            btnCancel = dialog.findViewById(R.id.btnCancel);
            headerTextView = dialog.findViewById(R.id.headerTextView);
            if (isNewPlayer) {
                inputEditText.setText("Player " + (recAdapter.getItemCount()+1));
                headerTextView.setText("Add player");
                dialog.setCancelable(true);
                gridLayout.setRowOrderPreserved(false);
                btnSave.setText("Add");
                btnDelete.setVisibility(View.INVISIBLE);
                btnEdit.setVisibility(View.INVISIBLE);
                btnCancel.setVisibility(View.INVISIBLE);
                btnDeletePlayer.setText(btnCancel.getText());
//                btnDeletePlayer.setTag(btnCancel.getTag());
            } else {
                inputEditText.setText(getSelectedPlayerName());
                headerTextView.setText("Rename Player");
                dialog.setCancelable(true);
            }
            // add / save button
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNewPlayer) {
                        addPlayer(inputEditText.getText().toString());
                        recAdapter.updateCursor(refreshPlayerCursor());
                        dialog.dismiss();
                    } else {
                        renamePlayer(inputEditText.getText().toString(),getSelected());
                        Log.i("Player " + getSelected() + " changed to ", inputEditText.getText().toString());
                        recAdapter.updateCursor(refreshPlayerCursor());
                        dialog.dismiss();
                    }
                }
            });
            // cancel / deletePlayer
            btnDeletePlayer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNewPlayer) {
                        dialog.dismiss();
                    }else {
                        deletePlayer();
                        dialog.dismiss();
                    }
                }
            });
            // edit last turn
            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Player player = recAdapter.getPlayer(getSelected());
//                    Cursor c = mDatabase.rawQuery("SELECT * FROM " + ScoreHistory.TABLE_NAME +
//                            " WHERE " + ScoreHistory.COLUMN_ROUND + " = " + player.getTurns(), null);
                    ContentValues cv = new ContentValues();
//                    if (c.moveToFirst()) {
//                        turnScore = c.getInt(c.getColumnIndex(ScoreHistory.COLUMN_SCORE[player.getNumber()]));
//                    } else {
//                        Log.e("ScoreHistory","Couldn't find turn " + player.getTurns() +
//                                " for player " + player.getNumber());
//                    }
                    turnScore = getLastScore(player.getNumber());
                    cv.put(PlayersTable.COLUMN_TURNS, player.getTurns() - 1);
                    cv.put(PlayersTable.COLUMN_SCORE, player.getTotalScore() - turnScore);
                    mDatabase.update(PlayersTable.TABLE_NAME, cv, PlayersTable.COLUMN_NUMBER +
                            " = " + player.getNumber(), null);
                    recAdapter.updateCursor(refreshPlayerCursor());
                    turnString = player.getTurn();
                    txvCurrentMove.setText(turnString);
                    dialog.dismiss();
                }
            });
            // delete turn
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTurn();
                    dialog.dismiss();
                }
            });
            // cancel button
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Sorry, Max players is 4", Toast.LENGTH_SHORT).show();
        }
    }

    void newPlayer(View view){
        longClickMenu(true);
        if (recAdapter.getItemCount() == 1) {
            selectPlayer(0);
        }
    }

    void deleteTurn() {
        ContentValues cv;
        Player player = recAdapter.getPlayer(getSelected());
        cv = new ContentValues();
        // clear score from player history table
        cv.clear();
        cv.put(ScoreHistory.COLUMN_SCORE[player.getNumber()], 0);
        cv.put(ScoreHistory.COLUMN_TURN[player.getNumber()], "");
        mDatabase.update(ScoreHistory.TABLE_NAME, cv, ScoreHistory.COLUMN_ROUND +
                "=" + player.getTurns(),null);
        refreshScoreCursor();
        // clear score from players table
        cv.clear();
        cv.put(PlayersTable.COLUMN_TURN,getLastTurn(player.getNumber()));
        cv.put(PlayersTable.COLUMN_SCORE, (player.getTotalScore() -
                getLastScore(player.getNumber())));
        cv.put(PlayersTable.COLUMN_TURNS, player.getTurns() - 1);
        mDatabase.update(PlayersTable.TABLE_NAME, cv,PlayersTable.COLUMN_NUMBER +
                "=" + player.getNumber(),null);
        recAdapter.updateCursor(refreshPlayerCursor());
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
                        deleteTurn();
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

    private int getLastScore(int playerNumber) {
        if (mCursorScores.moveToLast()) {
            return mCursorScores.getInt(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_SCORE[playerNumber]));
        }
        return 0;
    }

    private String getLastTurn(int playerNumber) {
        if (mCursorScores.moveToLast()) {
            return mCursorScores.getString(mCursorScores.getColumnIndex(ScoreHistory.COLUMN_TURN[playerNumber]));
        }
        return "";
    }

    private void recordTurn() {
        // add turn to player table
        Player player = recAdapter.getPlayer(getSelected());
        player.addScore(turnString, turnScore);
        ContentValues cv = new ContentValues();
        cv.put(PlayersTable.COLUMN_TURN, player.getTurn());
        cv.put(PlayersTable.COLUMN_SCORE, player.getTotalScore());
        cv.put(PlayersTable.COLUMN_TURNS, player.getTurns());
        mDatabase.update(PlayersTable.TABLE_NAME,cv,PlayersTable.COLUMN_LOCATION + "=" +
                getSelected(),null);
        // check if round exists in history then add or append row
        Cursor c = mDatabase.rawQuery("SELECT * FROM " + ScoreHistory.TABLE_NAME +
                " WHERE " + ScoreHistory.COLUMN_ROUND + "=" + player.getTurns(), null);
        cv.clear();
        cv.put(ScoreHistory.COLUMN_SCORE[player.getNumber()], turnScore);
        cv.put(ScoreHistory.COLUMN_TURN[player.getNumber()], turnString);
        if (c.moveToFirst()) {
            mDatabase.update(ScoreHistory.TABLE_NAME,cv,ScoreHistory.COLUMN_ROUND + "=" +
                    player.getTurns(),null);
        } else {
            cv.put(ScoreHistory.COLUMN_ROUND, player.getTurns());
            mDatabase.insert(ScoreHistory.TABLE_NAME,null,cv);
        }
        if (c != null) c.close();
        // if the game is not over move to next players turn
        if (!endGame) {
            playerTurn = (playerTurn + 1) % recAdapter.getItemCount();
            selectPlayer(playerTurn);
            recAdapter.updateCursor(refreshPlayerCursor());
            turnScore = 0;
            turnString = "";
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
        } else if (c.moveToFirst()) {
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
                    if (turnString.length() > 0) {
                        turnString += " + " + 6;
                    } else {
                        turnString = String.valueOf(6);
                    }
                    txvCurrentMove.setText(turnString);
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
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        switch (num) {
            case 2:
            case 3:
            case 4:
            case 5:
                vibrator.vibrate(50);
                turnScore += num;
                // if not first move, add +
                if (turnString.length() > 0) {
                    turnString += " + " + (num);
                } else {
                    turnString = String.valueOf(num);
                }
                txvCurrentMove.setText(turnString);
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
                    if (turnString.length() > 0) {
                        turnString += " + QWIRKLE";
                    } else {
                        turnString = "QWIRKLE";
                    }
                    txvCurrentMove.setText(turnString);
                } else {
                    TextView winnerTextView = findViewById(R.id.winnerTextView);
                    Button saveButton = findViewById(R.id.button8);
                    Button lastTileButton = findViewById(R.id.button4);
                    winnerTextView.setVisibility(View.INVISIBLE);
                    qwirkleButton.setText("QWIRKLE");
                    saveButton.setText("SAVE");
                    lastTileButton.setVisibility(View.VISIBLE);
                    recAdapter.notifyDataSetChanged();
                    turnString = "";
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
                turnString = "";
                turnScore = 0;
                vibrator.vibrate(50);
                break;
            // delete
            case 9:
                vibrator.vibrate(50);
                String[] turns = turnString.split(" ",48);
                turnString = "";
                turnScore = 0;
                for (int i = 0; i < turns.length - 1; i+= 2) {
                    if (i == 0) {
                        turnString = String.format("%s",turns[i]);
                    } else {
                        turnString = String.format("%s + %s", turnString, turns[i]);
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
        txvCurrentMove.setText(turnString);
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
        longClickMenu(true);
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
        longClickMenu(false);
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