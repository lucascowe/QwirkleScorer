package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryRecAdapter.RecListener {
    RecyclerView recyclerView;
    HistoryRecAdapter historyRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TableRow playersTR = (TableRow) findViewById(R.id.headersTableRow);
        TableRow scoresTR = (TableRow) findViewById(R.id.scoresTableRow);

        for (int j=1; j<scoresTR.getChildCount();j++) {
            TextView playerTV = (TextView) playersTR.getChildAt(j);
            playerTV.setText(MainActivity.players.get(j-1).getName());
        }
        for (int j=1; j<scoresTR.getChildCount();j++) {
            TextView scoreTV = (TextView) scoresTR.getChildAt(j);
            scoreTV.setText(Integer.toString(MainActivity.players.get(j-1).getTotalScore()));
        }

//        player0TV.setText(MainActivity.players.get(0).getName());
//        score0TV.setText(Integer.toString(MainActivity.players.get(0).getTotalScore()));
        initRecycler();

    }

    public void initRecycler() {
        // link Adapter to
        recyclerView = findViewById(R.id.historyRecyclerView);
        historyRecAdapter = new HistoryRecAdapter(MainActivity.scoreHistory, this);

        // Set up Recycler manager to link to adapter
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(historyRecAdapter);
    }

    @Override
    public void onRecClick(int position) {

    }

    @Override
    public boolean onRecLongClick(int position) {
        return false;
    }
}
