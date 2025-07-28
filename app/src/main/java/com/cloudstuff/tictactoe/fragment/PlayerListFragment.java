package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.listener.OnInvitationSentListener;
import com.cloudstuff.tictactoe.model.GamePlayer;
import com.cloudstuff.tictactoe.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class PlayerListFragment extends BaseFragment implements OnInvitationSentListener {

    @BindView(R.id.iv_back)
    AppCompatImageView ivBack;
    @BindView(R.id.playerList)
    RecyclerView rvPlayer;

    private List<GamePlayer> players;
    private PlayerRecyclerViewAdapter playerAdapter;

    private FirebaseDatabase database;
    private DatabaseReference dbRef;
    private DatabaseReference playerTable;

    public PlayerListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        players = new ArrayList<>();
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference(Constants.DatabaseConstants.DB_VERSION);
        playerTable = dbRef.child(Constants.DatabaseConstants.TABLE_PLAYER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        playerTable.addChildEventListener(this);
        // Set the adapter
        rvPlayer.setLayoutManager(new LinearLayoutManager(view.getContext()));
        playerAdapter = new PlayerRecyclerViewAdapter(this, players);
        rvPlayer.setAdapter(playerAdapter);

        final String currentPlayerId = preferenceUtils.getString(Constants.PreferenceConstant.MY_PLAYER_ID);

        playerTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Timber.d("Firebase --- onDataChange");

                if (dataSnapshot.getChildrenCount() == 0) return;

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    GamePlayer gamePlayer = ds.getValue(GamePlayer.class);

                    if (players.isEmpty()) {
                        if (!gamePlayer.getPlayerId().equals(currentPlayerId)) {
                            players.add(gamePlayer);
                            continue;
                        }
                    }

                    boolean isNewPlayer = true;

                    for (int i = 0; i < players.size(); i++) {
                        GamePlayer player = players.get(i);

                        if (gamePlayer.getPlayerId().equals(currentPlayerId)) {
                            isNewPlayer = false;
                            continue;
                        }

                        if (player.getPlayerId().equals(gamePlayer.getPlayerId())) {
                            isNewPlayer = false;
                            player.setPhotoUrl(gamePlayer.getPhotoUrl());
                            player.setOnline(gamePlayer.isOnline());
                            player.setGivenName(gamePlayer.getGivenName());
                            player.setDisplayName(gamePlayer.getDisplayName());
                            player.setFcmToken(gamePlayer.getFcmToken());
                            player.setLastPlayedTimestamp(gamePlayer.getLastPlayedTimestamp());
                            break;
                        }
                    }

                    if (isNewPlayer) {
                        if (!gamePlayer.getPlayerId().equals(currentPlayerId)) {
                            players.add(gamePlayer);
                        }
                    }
                }
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Timber.d("Firebase --- onCancelled");
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onInvitationSent(GamePlayer gamePlayer) {
        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), "Invitation sent successfully.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*@Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Timber.d("Firebase --- onChildAdded");
    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Timber.d("Firebase --- onChildChanged");
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
        Timber.d("Firebase --- onChildRemoved");
    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        Timber.d("Firebase --- onChildMoved");
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Timber.d("Firebase --- onCancelled");
    }*/

}
