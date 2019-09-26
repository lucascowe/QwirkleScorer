package keepitsimple.store.qwirklescorer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {
    private ArrayList<Player> recyclerList;
    private RecListener recListener;

    RecAdapter(ArrayList<Player> list, RecListener listener) {
        this.recyclerList = list;
        this.recListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView header, comment, data3, data4;
        RecListener mRecListener;
        LinearLayout linearLayout;

        ViewHolder(@NonNull View itemView, RecListener recListener) {
            super(itemView);

            header = itemView.findViewById(R.id.tvHeader);
            comment = itemView.findViewById(R.id.tvComment);
            data3 = itemView.findViewById(R.id.tvData3);
            data4 = itemView.findViewById(R.id.tvData4);
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
    public RecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ViewHolder(view,recListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecAdapter.ViewHolder holder, int position) {
        holder.header.setText(recyclerList.get(position).getName());
        holder.comment.setText(recyclerList.get(position).getTurn());
        holder.data3.setText(String.valueOf(recyclerList.get(position).getTurns()));
        holder.data4.setText(String.valueOf(recyclerList.get(position).getTotalScore()));
        if (recyclerList.get(position).isSelected()) {
            holder.linearLayout.setBackgroundColor(0xAA2196F3);
        } else {
            holder.linearLayout.setBackgroundColor(0x66111111);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
