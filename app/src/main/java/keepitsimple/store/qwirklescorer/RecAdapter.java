package keepitsimple.store.qwirklescorer;

import android.content.ContentValues;
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
import static keepitsimple.store.qwirklescorer.MainActivity.mDatabase;


public class RecAdapter extends RecyclerView.Adapter<RecAdapter.ViewHolder> {
    private Cursor mCursor;
    private RecListener recListener;

    RecAdapter(Cursor list, RecListener listener) {
        this.mCursor = list;
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
            linearLayout = itemView.findViewById(R.id.rowLinearLayout);


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
        boolean onRecClick(int position);
        void onRecLongClick(int position);
    }

    @NonNull
    @Override
    public RecAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
        return new ViewHolder(view,recListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecAdapter.ViewHolder holder, int position) {
        // check table row exists
        if (!mCursor.moveToPosition(position)) {
            return;
        }
        String text = mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_NAME));
        holder.header.setText(text);
        Log.i("Recycler bind " + position," Name: " + text + " Loc: " +
                mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_LOCATION)) +
                " num: " + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_NUMBER)) +
                " sel: " + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_SELECTED)));
        text = mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_TURN));
        holder.comment.setText(text);
        text = "" + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_TURNS));
        holder.data3.setText(text);
        text = "" + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_SCORE));
        holder.data4.setText(text);
        if (1 == mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_SELECTED))) {
            holder.linearLayout.setBackgroundColor(0xAA2196F3);
        } else {
            holder.linearLayout.setBackgroundColor(0x66111111);
        }
//        ContentValues cv = new ContentValues();
//        cv.put(PlayersTable.COLUMN_LOCATION, position);
//        mDatabase.update(PlayersTable.TABLE_NAME, cv, PlayersTable.COLUMN_NUMBER +
//                "=" + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_NUMBER)),null);
//        Log.i("after re-assign","Name: " + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_NAME))+
//                " loc: " + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_LOCATION)) +
//                " num: " + mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_NUMBER)));
    }

    int logPlayerTableContents() {
        if (!mCursor.moveToFirst()) {
            return Returns.FAIL;
        }
        do {
            Log.i("Players Table","loc: " + mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_LOCATION)) +
                    " #:" + mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_NUMBER)) +
                    " name: " + mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_NAME)) + "last turn: " +
                    mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_TURN)) + " # turns:" +
                    mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_TURNS)) + " score: " +
                    mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_SCORE)) + " Sel: " +
                    mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_SELECTED)));
        } while (mCursor.moveToNext());
        return Returns.SUCCESS;
    }

    @Override
    public int getItemCount() {
        try {
            return mCursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    void updateCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        if (newCursor != null) {
             notifyDataSetChanged();
        }
    }

    int getPlayerNumber(int p) {
        if (mCursor.moveToPosition(p)) {
            return mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_NUMBER));
        }
        Log.i("Error","Position " + p + " out of range of " + mCursor.getCount());
        return 0;
    }

    int getPlayerTurnNumber(int p) {
        if (mCursor.moveToPosition(p)) {
            return mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_TURNS));
        }
        Log.i("Error","Position " + p + " out of range of " + mCursor.getCount());
        return 0;
    }

    int getPlayerScore(int p) {
        if (mCursor.moveToPosition(p)) {
            return mCursor.getInt(mCursor.getColumnIndex(PlayersTable.COLUMN_SCORE));
        }
        Log.i("Error","Position " + p + " out of range of " + mCursor.getCount());
        return 0;
    }

    String getPlayerName(int p) {
        if (mCursor.moveToPosition(p)) {
            return mCursor.getString(mCursor.getColumnIndex(PlayersTable.COLUMN_NAME));
        }
        Log.i("Error","Position " + p + " out of range of " + mCursor.getCount());
        return "";
    }
}
