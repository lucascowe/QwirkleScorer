package keepitsimple.store.qwirklescorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecAdapter.RecListener{

    int turnScore, playerTurn;
    String moveString = "";
    TextView txvCurrentMove;
    RecyclerView recyclerView;
    RecAdapter recAdapter;
    static public ArrayList<Player> players;
    static public ArrayList<RoundScore> scoreHistory;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void playerName(Boolean isNewPlayer) {
        Button btnAdd, btnCancel, btnFinish;
        final Boolean newPlayer = isNewPlayer;
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.add_player);
        final EditText inputEditText;
        inputEditText = dialog.findViewById(R.id.editTextDialogName);
        btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        btnAdd = dialog.findViewById(R.id.btnDialogAdd);
        btnFinish = dialog.findViewById(R.id.btnDialogFinish);
        if (isNewPlayer) {
            inputEditText.setText("");
            dialog.setTitle("Add player");
            dialog.setCancelable(true);
        } else {
            inputEditText.setText(players.get(findSelectedPlayer()).getName());
            dialog.setTitle("Rename Player");
            dialog.setCancelable(true);
            btnFinish.setText("Save");
            btnAdd.setVisibility(View.INVISIBLE);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    players.add(new Player(inputEditText.getText().toString()));
                    recAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                    playerName(true);
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
    }

    int findSelectedPlayer() {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isSelected()) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.newPlayer:
                playerName(true);
                if (players.size() == 1) {
                    players.get(0).setSelected(true);
                }
                break;
            case R.id.rename:
                if (players.size() > 0) {
                    playerName(false);
                }
                break;
            case R.id.delete:
                if (players.size() > 0) {
                    for (int i = 0; i < players.size(); i++) {
                        if (players.get(i).isSelected()) {
                            players.remove(i);
                            break;
                        }
                    }
                    recAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.deleteAll:
                players.clear();
                recAdapter.notifyDataSetChanged();
                break;
            case R.id.edit:
                Intent intent = new Intent(getApplicationContext(),HistoryActivity.class);
//                intent.putExtra("Players", players);

                startActivity(intent);
                break;
            default:
                Log.e("Menu","Invalid menu option");
        }
        return true;
    }

    public void saveTurn() {
        players.get(playerTurn).addScore(moveString, turnScore);
        if (players.get(playerTurn).getTurns() > scoreHistory.size()) {
            scoreHistory.add(new RoundScore());

        }
        scoreHistory.get(players.get(playerTurn).getTurns() - 1).addScore(playerTurn, turnScore,moveString);
        players.get(playerTurn).setSelected(false);
        playerTurn = (playerTurn + 1) % players.size();
        players.get(playerTurn).setSelected(true);
        recAdapter.notifyDataSetChanged();
        turnScore = 0;
        moveString = "";
        txvCurrentMove.setText("");
    }

    public void onClick (View view) {
        Button button = (Button) view;
        int num = Integer.parseInt(button.getTag().toString());
        switch (num) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                turnScore += num;
                // if not first move, add +
                if (moveString.length() > 0) {
                    moveString += " + " + (num);
                } else {
                    moveString = String.valueOf(num);
                }
                txvCurrentMove.setText(moveString);
                break;
            case 6:
                turnScore += 12;
                // if not first move, add +
                if (moveString.length() > 0) {
                    moveString += " + QWIRKLE";
                } else {
                    moveString = "QWIRKLE";
                }
                txvCurrentMove.setText(moveString);
                break;
            // save
            case 7:
                saveTurn();
                break;
            // clear
            case 8:
                moveString = "";
                turnScore = 0;
                break;
            // delete
            case 9:
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
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
        players.add(new Player("Player 4"));
        playerTurn = 0;
        players.get(playerTurn).setSelected(true);
        scoreHistory = new ArrayList<>();
        initRecycler();
        playerName(true);
    }

    @Override
    public void onRecClick(int position) {

    }

    @Override
    public boolean onRecLongClick(int position) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setSelected(false);
        }
        players.get(position).setSelected(true);
        recAdapter.notifyDataSetChanged();
        return true;
    }
}

