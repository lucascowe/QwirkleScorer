package keepitsimple.store.qwirklescorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryRecAdapter.RecListener {
    RecyclerView recyclerView;
    HistoryRecAdapter historyRecAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
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
