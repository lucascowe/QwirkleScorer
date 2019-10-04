package keepitsimple.store.qwirklescorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecAdapter.RecListener{

    int turnScore, playerTurn;
    String moveString = "";
    TextView txvCurrentMove;
    RecyclerView recyclerView;
    RecAdapter recAdapter;
    boolean endGame;
    static public ArrayList<Player> players;
    static public ArrayList<RoundScore> scoreHistory;
    MediaPlayer MP;
    Vibrator vibrator;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playerName(Boolean isNewPlayer) {
        if (!isNewPlayer || players.size() < 4) {
            final Button btnAdd, btnCancel, btnFinish;
            final Boolean newPlayer = isNewPlayer;
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.add_player);
            final EditText inputEditText;
            inputEditText = dialog.findViewById(R.id.editTextDialogName);
            btnCancel = dialog.findViewById(R.id.btnDialogCancel);
            btnAdd = dialog.findViewById(R.id.btnDialogAdd);
            btnFinish = dialog.findViewById(R.id.btnDialogFinish);
            if (isNewPlayer) {
                inputEditText.setText("Player " + Integer.toString(players.size()+1));
                dialog.setTitle("Add player");

                dialog.setCancelable(true);
            } else {
                inputEditText.setText(players.get(findSelectedPlayer()).getName());
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
                    if (btnAdd.getText().toString() == "Delete") {
                        deletePlayer();
                        dialog.dismiss();
                    }else {
                        players.add(new Player(inputEditText.getText().toString()));
                        recAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                        playerName(true);
                        players.get(playerTurn).setSelected(true);
                    }
                }
            });
            btnFinish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (newPlayer) {
                        players.add(new Player(inputEditText.getText().toString()));
                        recAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    } else {
                        players.get(findSelectedPlayer()).setName(inputEditText.getText().toString());
                        Log.i("Player " + findSelectedPlayer() + " changed to", inputEditText.getText().toString());
                        recAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });
            dialog.show();
        } else {
            Toast.makeText(this, "Sorry, Max players is 4", Toast.LENGTH_SHORT).show();
        }
    }

    int findSelectedPlayer() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isSelected()) {
                return i;
            }
        }
        return 0;
    }
    public void addPlayer(View view){
        playerName(true);
        if (players.size() == 1) {
            players.get(0).setSelected(true);
        }
    }
    public void openHistory(View view){
        Intent intent = new Intent(getApplicationContext(),HistoryActivity.class);
        startActivity(intent);
    }

    public void openMenu(View view) {
        Button settingsButton = (Button) findViewById(R.id.settingsButton);
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, settingsButton);
        popupMenu.getMenuInflater().inflate(R.menu.main_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.clearScores:
                        scoreHistory.clear();
                        for (Player p : players) {
                            p.resetScore();
                        }
                        recAdapter.notifyDataSetChanged();
                        break;
                    case R.id.deleteScore:
                        int p = 0;
                        int lastScoreAmount = 0;
                        if (players.size() > 0) {
                            p = findSelectedPlayer();
                            lastScoreAmount = scoreHistory.get(players.get(p).getTurns() - 1).getScore(p);
                            players.get(p).deleteTurn(lastScoreAmount);
                            recAdapter.notifyDataSetChanged();
                        }
                        scoreHistory.get(scoreHistory.size() -1).clearScore(p);
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


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        super.onOptionsItemSelected(item);
//
//        switch (item.getItemId()) {
//
//            case R.id.clearScores:
//                scoreHistory.clear();
//                for (Player p : players) {
//                    p.resetScore();
//                }
//                recAdapter.notifyDataSetChanged();
//                break;
//            case R.id.deleteScore:
//                int p = 0;
//                int lastScoreAmount = 0;
//                if (players.size() > 0) {
//                    p = findSelectedPlayer();
//                    lastScoreAmount = scoreHistory.get(players.get(p).getTurns() - 1).getScore(p);
//                    players.get(p).deleteTurn(lastScoreAmount);
//                    recAdapter.notifyDataSetChanged();
//                }
//                scoreHistory.get(scoreHistory.size() -1).clearScore(p);
//                break;
//            case R.id.screenLock:
//                if (item.getTitle().equals("Disable Screen Lock")) {
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                    item.setTitle("Enable Screen Lock");
//                } else {
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
//                    item.setTitle("Disable Screen Lock");
//                }
//                break;
//            case R.id.howTo:
//                Intent howToIntent = new Intent(getApplicationContext(),HowToActivity.class);
//                startActivity(howToIntent);
//            default:
//
//                Log.e("Menu","Invalid menu option");
//        }
//        return true;
//    }
    public void deletePlayer(){
        if (players.size() > 0) {
            int s = findSelectedPlayer();
            players.remove(s);
            for (RoundScore r : scoreHistory) {
                r.clearScore(s);
            }
            if (s < players.size()) {
                players.get(s).setSelected(true);
            } else {
                s--;
                players.get(s).setSelected(true);
            }
            playerTurn = s;
            recAdapter.notifyDataSetChanged();
        }
    }
    public void saveTurn() {
        players.get(playerTurn).addScore(moveString, turnScore);
        if (players.get(playerTurn).getTurns() > scoreHistory.size()) {
            scoreHistory.add(new RoundScore());
        }
        scoreHistory.get(players.get(playerTurn).getTurns() - 1).addScore(playerTurn, turnScore,moveString);
        if (!endGame) {
            players.get(playerTurn).setSelected(false);
            playerTurn = (playerTurn + 1) % players.size();
            players.get(playerTurn).setSelected(true);
            recAdapter.notifyDataSetChanged();
            turnScore = 0;
            moveString = "";
            txvCurrentMove.setText("");
        } else {
            recAdapter.notifyDataSetChanged();
            TextView winnerTextView = (TextView) findViewById(R.id.winnerTextView);
            Button qwirkleButton = (Button) findViewById(R.id.button5);
            Button saveButton = (Button) findViewById(R.id.button8);
            Button lastTileButton = (Button) findViewById(R.id.button4);
            winnerTextView.setText(whoWins());
            winnerTextView.setVisibility(View.VISIBLE);
            qwirkleButton.setText("Play Again");
            saveButton.setText("Exit");
            lastTileButton.setVisibility(View.INVISIBLE);
            endGame = true;
        }
    }

    String whoWins() {
        String winner = "";
        int highestScore = 0;
        int numberOfWinners = 0;
        for (Player p : players) {
            if (p.getTotalScore() > highestScore) {
                highestScore = p.getTotalScore();
                winner = p.getName();
                numberOfWinners = 1;
            } else if (p.getTotalScore() == highestScore) {
                winner += " and " + p.getName();
                numberOfWinners++;
            }
        }
        if (numberOfWinners > 1) {
            winner += " are the Winners!!";
        } else {
            winner += " Wins!!";
        }
        return winner;
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    endGame = true;
                    turnScore += 6;
                    // if not first move, add +
                    if (moveString.length() > 0) {
                        moveString += " + " + String.valueOf(6);
                    } else {
                        moveString = String.valueOf(6);
                    }
                    txvCurrentMove.setText(moveString);
                    saveTurn();
                    vibrator.vibrate(2000);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void onClick (View view) {
        Button button = (Button) view;
        int num = Integer.parseInt(button.getTag().toString());
        MP = MediaPlayer.create(this,R.raw.buttonpress);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        switch (num) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                MP.start();
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
                    MP.start();
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
                    scoreHistory.clear();
                    for (Player p : players) {
                        p.resetScore();
                    }
                    players.get(playerTurn).setSelected(false);
                    playerTurn = 0;
                    players.get(playerTurn).setSelected(true);
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
                        MP.start();
                        vibrator.vibrate(50);
                        saveTurn();
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

    public void initRecycler() {
        // link Adapter to list
        recyclerView = findViewById(R.id.recyclerView);
        recAdapter = new RecAdapter(players,this);

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

        players = new ArrayList<>();
//        players.add(new Player("Player 1"));
//        players.add(new Player("Player 2"));
//        players.add(new Player("Player 3"));
//        players.add(new Player("Player 4"));
//        playerTurn = 0;
//        players.get(playerTurn).setSelected(true);
        scoreHistory = new ArrayList<>();
        initRecycler();
        playerName(true);
    }

    @Override
    public void onBackPressed() {
        super.onPause();
        super.onBackPressed();
    }

    @Override
    public boolean onRecClick(int position) {
        players.get(findSelectedPlayer()).setSelected(false);
        players.get(position).setSelected(true);
        recAdapter.notifyDataSetChanged();
        playerTurn = position;
        return true;
    }

    @Override
    public void onRecLongClick(int position) {
        players.get(findSelectedPlayer()).setSelected(false);
        players.get(position).setSelected(true);
        recAdapter.notifyDataSetChanged();
        playerTurn = position;
        playerName(false);
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

 */