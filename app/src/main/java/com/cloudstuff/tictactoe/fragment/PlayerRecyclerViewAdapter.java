package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.TicTacToe;
import com.cloudstuff.tictactoe.listener.OnInvitationSentListener;
import com.cloudstuff.tictactoe.model.FCMRequest;
import com.cloudstuff.tictactoe.model.GamePlayer;
import com.cloudstuff.tictactoe.utils.Constants;
import com.cloudstuff.tictactoe.utils.FCMUtils;
import com.cloudstuff.tictactoe.utils.PreferenceUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder> {

    private PreferenceUtils preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();

    private final List<GamePlayer> players;
    private OnInvitationSentListener listener;
    private Context context;

    PlayerRecyclerViewAdapter(OnInvitationSentListener listener, List<GamePlayer> items) {
        players = items;
        this.listener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.player = players.get(position);
        if (holder.player.isOnline()) {
            holder.mIvOnlineOffline.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_online));
        } else {
            holder.mIvOnlineOffline.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_offline));
        }
        holder.mIdView.setText(players.get(position).getDisplayName());
        holder.mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FCMRequest fcmRequest = new FCMRequest();
                FCMRequest.Data data = new FCMRequest.Data();
                data.setSenderId(preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_ID));
                data.setSenderName(preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_NAME));
                data.setReceiverId(holder.player.getPlayerId());
                data.setReceiverName(holder.player.getDisplayName());
                data.setNotificationType(100); // 100 For Game Play RequestR Notification
                fcmRequest.setData(data);
                fcmRequest.setRegistrationIds(Collections.singletonList(holder.player.getFcmToken()));
                FCMUtils.sendFCMMessage(holder.player, fcmRequest, listener);
            }
        });
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final AppCompatTextView mIdView;
        final AppCompatButton mBtnInvite;
        final AppCompatImageView mIvOnlineOffline;

        public GamePlayer player;

        ViewHolder(View view) {
            super(view);
            mIdView = view.findViewById(R.id.item_number);
            mBtnInvite = view.findViewById(R.id.btn_invite);
            mIvOnlineOffline = view.findViewById(R.id.iv_online_offline);
        }

    }
}
