package com.cloudstuff.tictactoe.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.db.tables.GameDetails;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    //region #Variables
    private Context context;
    private List<GameDetails> historyList;
    //endregion

    //region #Constructors
    public HistoryAdapter(Context context, List<GameDetails> historyList) {
        this.context = context;
        this.historyList = historyList;
    }
    //endregion

    //region #InBuilt Methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        GameDetails history = historyList.get(holder.getAdapterPosition());

        int playerOneScore = history.getPlayerOneScore();
        int playerTwoScore = history.getPlayerTwoScore();

        holder.tvPlayerOneName.setText(history.getPlayerOneName());
        holder.tvPlayerOneScore.setText(String.valueOf(playerOneScore));

        holder.tvPlayerTwoName.setText(history.getPlayerTwoName());
        holder.tvPlayerTwoScore.setText(String.valueOf(playerTwoScore));

        holder.tvDraw.setText(context.getString(R.string.draw_count, history.getDrawScore()));
        holder.tvTime.setText(DateUtils.getRelativeTimeSpanString(history.getTimestamp()));

        if (playerOneScore > playerTwoScore) {
            holder.tvPlayerOneScore.setTextColor(ContextCompat.getColor(context, R.color.game_win_text_color));
            holder.tvPlayerTwoScore.setTextColor(ContextCompat.getColor(context, R.color.game_lose_text_color));
        } else if (playerTwoScore > playerOneScore) {
            holder.tvPlayerOneScore.setTextColor(ContextCompat.getColor(context, R.color.game_lose_text_color));
            holder.tvPlayerTwoScore.setTextColor(ContextCompat.getColor(context, R.color.game_win_text_color));
        } else {
            holder.tvPlayerOneScore.setTextColor(ContextCompat.getColor(context, R.color.game_draw_text_color));
            holder.tvPlayerTwoScore.setTextColor(ContextCompat.getColor(context, R.color.game_draw_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    //endregion

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_player_one_name)
        MaterialTextView tvPlayerOneName;
        @BindView(R.id.tv_player_one_score)
        MaterialTextView tvPlayerOneScore;
        @BindView(R.id.tv_player_two_name)
        MaterialTextView tvPlayerTwoName;
        @BindView(R.id.tv_player_two_score)
        MaterialTextView tvPlayerTwoScore;
        @BindView(R.id.tv_draw)
        MaterialTextView tvDraw;
        @BindView(R.id.tv_time)
        MaterialTextView tvTime;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            tvPlayerOneName.setSelected(true);
            tvPlayerTwoName.setSelected(true);
        }
    }
}