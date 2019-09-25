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
    private ArrayList<Player> recyclerList;
    private RecListener recListener;

    public HistoryRecAdapter(ArrayList<Player> list, RecListener listener) {
        this.recyclerList = list;
        this.recListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView turn, player1, player2, player3, player4;
        RecListener mRecListener;
        LinearLayout linearLayout;

        public ViewHolder(@NonNull View itemView, RecListener recListener) {
            super(itemView);

            turn = itemView.findViewById(R.id.turnTextView);
            player1 = itemView.findViewById(R.id.player1TextView);
            player2 = itemView.findViewById(R.id.player2TextView);
            player3 = itemView.findViewById(R.id.player3TextView);
            player4 = itemView.findViewById(R.id.player4TextView);
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
        holder.turn.setText(Integer.toString(position));
        holder.player1.setText(String.valueOf(recyclerList.get(0).getTurn(position)));
        holder.player2.setText(String.valueOf(recyclerList.get(1).getTurn(position)));
        holder.player3.setText(String.valueOf(recyclerList.get(2).getTurn(position)));
        holder.player4.setText(String.valueOf(recyclerList.get(3).getTurn(position)));
        if (recyclerList.get(position).isSelected()) {
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

