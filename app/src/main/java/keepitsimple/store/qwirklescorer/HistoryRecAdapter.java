package keepitsimple.store.qwirklescorer;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static keepitsimple.store.qwirklescorer.DatabaseNames.*;
import static keepitsimple.store.qwirklescorer.MainActivity.*;


public class HistoryRecAdapter extends RecyclerView.Adapter<HistoryRecAdapter.ViewHolder> {
    private Cursor sCursor;
    private RecListener recListener;

    HistoryRecAdapter(Cursor scoreCursor, RecListener listener) {
        this.sCursor = scoreCursor;
        this.recListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView turn, player1, player2, player3, player4;
        TextView players[] = {player1, player2, player3, player4};
        RecListener mRecListener;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView, RecListener recListener) {
            super(itemView);

            turn = itemView.findViewById(R.id.turnTextView);
            players[0] = itemView.findViewById(R.id.player0TextView);
            players[1] = itemView.findViewById(R.id.player1TextView);
            players[2] = itemView.findViewById(R.id.player2TextView);
            players[3] = itemView.findViewById(R.id.player3TextView);
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

    public interface RecListener {
        void onRecClick(int position);
        boolean onRecLongClick(int position);
    }

    @NonNull
    @Override
    public HistoryRecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.historyrow,parent,false);
        return new ViewHolder(view,recListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryRecAdapter.ViewHolder holder, int position) {
        if (!sCursor.moveToPosition(position)) {
            Log.i("history",position + " no row found");
            return;
        }
        String text;
        String msg;
        holder.turn.setText(Integer.toString(position + 1));
        for (int ii = 0; ii < 4; ii++) {
            if (ii < recAdapter.getItemCount()) {
                mCursorPlayers.moveToPosition(ii);
                int playerNum = mCursorPlayers.getInt(mCursorPlayers.getColumnIndex(PlayersTable.COLUMN_NUMBER));
                text = sCursor.getString(sCursor.getColumnIndex(ScoreHistory.COLUMN_TURN[playerNum]));
                msg = "matched player number " + playerNum;
            } else {
                msg = "no match found " + ii + " < " + recAdapter.getItemCount();
                text = "";
            }
            Log.i("history", "row: " + position + " loc: " + ii + " text: " + text + " msg: " + msg);
            holder.players[ii].setText(text);
        }
        if (position % 2 == 1) {
            holder.linearLayout.setBackgroundColor(0x992196F3);
        } else {
            holder.linearLayout.setBackgroundColor(0);
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

