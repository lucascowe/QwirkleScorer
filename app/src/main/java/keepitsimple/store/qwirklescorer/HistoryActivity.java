package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import static keepitsimple.store.qwirklescorer.MainActivity.recAdapter;

public class HistoryActivity extends AppCompatActivity implements HistoryRecAdapter.RecListener {
    RecyclerView recyclerView;
    static HistoryRecAdapter historyRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        TableRow playersTR = findViewById(R.id.headersTableRow);
        TableRow scoresTR = findViewById(R.id.scoresTableRow);

        for (int jj = 1; jj < scoresTR.getChildCount(); jj++) {
            TextView playerTV = (TextView) playersTR.getChildAt(jj);
            TextView scoreTV = (TextView) scoresTR.getChildAt(jj);
            if (jj <= recAdapter.getItemCount()) {
                playerTV.setText(recAdapter.getPlayerName(jj));
                scoreTV.setText(String.valueOf(recAdapter.getPlayerScore(jj)));
            } else {
                playerTV.setVisibility(View.INVISIBLE);
                scoreTV.setVisibility(View.INVISIBLE);
            }
        }

        initRecycler();

    }

    public void initRecycler() {
        // link Adapter to
        recyclerView = findViewById(R.id.historyRecyclerView);
        historyRecAdapter = new HistoryRecAdapter(MainActivity.mCursorScores, this);

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
