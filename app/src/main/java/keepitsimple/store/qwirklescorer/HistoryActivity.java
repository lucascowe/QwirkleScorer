package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    HistoryRecAdapter historyRecAdapter;
    ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Intent intent = getIntent();
        players = (ArrayList<Player>) intent.getSerializableExtra("Players");
    }

    public void initRecycler() {
        // link Adapter to list
        historyRecAdapter = new HistoryRecAdapter(players, (HistoryRecAdapter.RecListener) this);

        // Set up Recycler manager to link to adapter
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(historyRecAdapter);
    }
}
