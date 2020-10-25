package keepitsimple.store.qwirklescorer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;
import static keepitsimple.store.qwirklescorer.MainActivity.*;


public class GameSelectRecAdapter extends RecyclerView.Adapter<GameSelectRecAdapter.ViewHolder> {
    private Cursor sCursor;
    private RecListener recListener;

    public GameSelectRecAdapter(Cursor gameCursor, RecListener listener) {
        this.sCursor = gameCursor;
        this.recListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView game;

        RecListener mRecListener;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView, RecListener recListener) {
            super(itemView);

            game = itemView.findViewById(R.id.textViewRules);
            linearLayout = itemView.findViewById(R.id.linearLayout);

            this.mRecListener = recListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            recListener.onRecClick(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();

            recListener.onRecLongClick(position);
            return true;
        }
    }

    String getGameName(int p) {
        if (sCursor.moveToPosition(p)) {
            return sCursor.getString(sCursor.getColumnIndex(GameOptions.COLUMN_NAME));
        }
        Log.i("Error","Position " + p + " out of range of " + sCursor.getCount());
        return "Not Found";
    }

    public interface RecListener {
        void onRecClick(int position);
        boolean onRecLongClick(int position);
    }

    @NonNull
    @Override
    public GameSelectRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gameselectrow,parent,false);
        return new ViewHolder(view,recListener);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSelectRecAdapter.ViewHolder holder, int position) {
        if (!sCursor.moveToPosition(position)) {
            Log.i("GAME",position + " no row found");
            return;
        }
        String text;
        String msg;
        try {
            holder.game.setText(sCursor.getString(sCursor.getColumnIndex(GameOptions.COLUMN_NAME)));
            Log.i("Recycler View:", String.valueOf(holder.game.getText()));
            if (sCursor.getInt(sCursor.getColumnIndex(PlayersTable.COLUMN_SELECTED)) == 1) {
                holder.game.setBackgroundColor(0xAA2196F3);
            } else {
                holder.game.setBackgroundColor(0xDD555555);
            }

        } catch (Exception e) {
            Log.i("Binding Error", " Position " + 1);
        }


    }

    @Override
    public int getItemCount() {
        return sCursor.getCount();
    }

    void updateCursor(Cursor newCursor) {
        if (sCursor != null) {
            sCursor.close();
        }
        sCursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }


}