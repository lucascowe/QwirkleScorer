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

        TableRow playersTR = findViewById(R.id.headersTableRow);
        TableRow scoresTR = findViewById(R.id.scoresTableRow);

        for (int jj = 1; jj < MainActivity.players.size(); jj++) {
            TextView playerTV = (TextView) playersTR.getChildAt(jj);
            TextView scoreTV = (TextView) scoresTR.getChildAt(jj);
            playerTV.setText(MainActivity.players.get(jj-1).getName());
            scoreTV.setText(Integer.toString(MainActivity.players.get(jj-1).getTotalScore()));
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
