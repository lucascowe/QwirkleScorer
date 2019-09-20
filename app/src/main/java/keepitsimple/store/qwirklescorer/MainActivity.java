package keepitsimple.store.qwirklescorer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
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
    ArrayList<Player> players;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addPlayer() {
        final EditText inputEditText = new EditText(this);
        inputEditText.setText("");

        new AlertDialog.Builder(this)
            .setTitle("Add Player")
            .setMessage("Enter Player Name")
            .setView(inputEditText)
            .setPositiveButton("Finished", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (inputEditText.getText().toString() != "") {
                        players.add(new Player(inputEditText.getText().toString()));
                        recAdapter.notifyDataSetChanged();
                    }
                }
            })
            .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (inputEditText.getText().toString() != "") {
                        players.add(new Player(inputEditText.getText().toString()));
                        recAdapter.notifyDataSetChanged();
                        addPlayer();
                    }
                }
            })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
            .show();
    }

    public void renamePlayer() {
        final EditText inputEditText = new EditText(this);
        int i;
        for (i = 0; i < players.size(); i++) {
            if (players.get(i).isSelected()) {
                break;
            }
        }
        final int pos = i;

        inputEditText.setText(players.get(pos).getName());

        new AlertDialog.Builder(this)
                .setTitle("Add Player")
                .setMessage("Enter Player Name")
                .setView(inputEditText)
                .setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (inputEditText.getText().toString() != "") {
                            players.get(pos).setName(inputEditText.getText().toString());
                            recAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.newPlayer:
                addPlayer();
            case R.id.rename:
                renamePlayer();
                break;
            case R.id.delete:
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).isSelected()) {
                        players.remove(i);
                        break;
                    }
                }
                recAdapter.notifyDataSetChanged();
            case R.id.deleteAll:
                players.clear();
                recAdapter.notifyDataSetChanged();
            case R.id.edit:
                // edit scores activity
            default:
                Log.e("Menu","Invalid menu option");
        }
        return true;
    }

    public void saveTurn() {
        players.get(playerTurn).addScore(moveString, turnScore);
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
                    moveString += " + " + String.valueOf(num);
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
        recyclerView = findViewById(R.id.recyclerView);

        players = new ArrayList<>();
        players.add(new Player("Player 1"));
        players.add(new Player("Player 2"));
        players.add(new Player("Player 3"));
        players.add(new Player("Player 4"));
        playerTurn = 0;
        players.get(playerTurn).setSelected(true);
        initRecycler();
        addPlayer();
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

