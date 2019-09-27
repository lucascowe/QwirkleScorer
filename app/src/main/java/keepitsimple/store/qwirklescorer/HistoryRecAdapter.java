package keepitsimple.store.qwirklescorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class HistoryRecAdapter extends RecyclerView.Adapter<HistoryRecAdapter.ViewHolder> {
    private ArrayList<RoundScore> recyclerList;
    private RecListener recListener;

    HistoryRecAdapter(ArrayList<RoundScore> list, RecListener listener) {
        this.recyclerList = list;
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
        holder.turn.setText(Integer.toString(position+1));
        for (int i = 0; i < MainActivity.players.size(); i++) {
            holder.players[i].setText(
                    String.valueOf(recyclerList.get(position).getTurn(i)) == null ? "" :
                            String.valueOf(recyclerList.get(position).getTurn(i)));
        }
        if (position % 2 == 1) {
            holder.linearLayout.setBackgroundColor(0xFF2196F3);
        } else {
            holder.linearLayout.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerList.size();
    }


}

