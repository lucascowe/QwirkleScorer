import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {
    private ArrayList<GeneralRecyclerList> recyclerList;
    private RecListener recListener;
    private boolean selectorMode = false;

    public boolean isSelectorMode() {
        return selectorMode;
    }

    public void setSelectorMode(boolean selectorMode) {
        this.selectorMode = selectorMode;
        if (false == selectorMode){
            for (int i = 0; i < recyclerList.size(); i++) {
                recyclerList.get(i).setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public RecAdapter(ArrayList<GeneralRecyclerList> list, RecListener listener) {
        this.recyclerList = list;
        this.recListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView header, comment, data3, data4;
        RecListener mRecListener;
        RelativeLayout relativeLayout;

        public ViewHolder(@NonNull View itemView, RecListener recListener) {
            super(itemView);

            header = itemView.findViewById(R.id.tvHeader);
            comment = itemView.findViewById(R.id.tvComment);
            data3 = itemView.findViewById(R.id.tvData3);
            data4 = itemView.findViewById(R.id.tvData4);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

            this.mRecListener = recListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (selectorMode) {
                recyclerList.get(position).setSelected(!recyclerList.get(position).getSelected());
            } else {
                Log.i("position from click", String.valueOf(position));
            }
            recListener.onRecClick(position);
        }

        @Override
        public boolean onLongClick(View view) {
            int position = getAdapterPosition();
            Log.i("long","detecetd " + position);
            selectorMode = !selectorMode;
            if (selectorMode) {
                recListener.onRecLongClick(position);
                Log.i("long", "selection mode turned on");
                selectorMode = true;
                try {
                    recyclerList.get(position).setSelected(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("selected", "Couldn't set selected");
                }
                Log.i("Long Click on", String.valueOf(position));
            } else {
                setSelectorMode(false);
            }
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
        holder.header.setText(recyclerList.get(position).getData1());
        holder.comment.setText(recyclerList.get(position).getData2());
        holder.data3.setText(recyclerList.get(position).getData3());
        holder.data4.setText(recyclerList.get(position).getData4());
        if (recyclerList.get(position).getSelected()) {
            holder.relativeLayout.setBackgroundColor(0xFF2196F3);
        } else {
            holder.relativeLayout.setBackgroundColor(0);
        }
    }

    @Override
    public int getItemCount() {
        return recyclerList.size();
    }
}
