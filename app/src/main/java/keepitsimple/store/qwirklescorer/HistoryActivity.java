package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import static keepitsimple.store.qwirklescorer.MainActivity.recAdapter;

public class HistoryActivity extends AppCompatActivity implements HistoryRecAdapter.RecListener {
    private RecyclerView recyclerView;
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
                playerTV.setText(recAdapter.getPlayerName(jj-1));
                scoreTV.setText(String.valueOf(recAdapter.getPlayerScore(jj-1)));
            } else {
                playerTV.setVisibility(View.INVISIBLE);
                scoreTV.setVisibility(View.INVISIBLE);
            }
        }

        initRecycler();
        recAdapter.logPlayerTableContents();

    }

    private void initRecycler() {
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
