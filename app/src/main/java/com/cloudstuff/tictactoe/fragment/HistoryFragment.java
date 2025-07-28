package com.cloudstuff.tictactoe.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.activity.MainActivity;
import com.cloudstuff.tictactoe.adapter.HistoryAdapter;
import com.cloudstuff.tictactoe.db.tables.GameDetails;
import com.cloudstuff.tictactoe.utils.CommonUtils;
import com.cloudstuff.tictactoe.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

public class HistoryFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    //region #Butterknife
    @BindView(R.id.tv_title)
    MaterialTextView tvTitle;
    @BindView(R.id.rv_history)
    RecyclerView rvHistory;
    @BindView(R.id.srl_history)
    SwipeRefreshLayout srlHistory;
    @BindView(R.id.ll_empty_view)
    LinearLayout llEmptyView;
    @BindView(R.id.tv_empty_view_title)
    MaterialTextView tvEmptyViewTitle;
    @BindView(R.id.tv_empty_view_message)
    MaterialTextView tvEmptyViewMessage;
    @BindView(R.id.tv_history_limit)
    MaterialTextView tvHistoryLimit;
    //endregion

    //region #Variables
    private HistoryAdapter historyAdapter;
    private List<GameDetails> historyList = new ArrayList<>();
    private Observer<List<GameDetails>> gameDetailsListObserver;

    private int historyRecordLimit = 10;
    private MainActivity mainActivity;
    //endregion

    //region #InBuilt Methods
    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onStop() {
        mainActivity.getDatabaseHelper().getAllGameDetails(historyRecordLimit).removeObserver(gameDetailsListObserver);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mainActivity.getDatabaseHelper().getAllGameDetails(historyRecordLimit).removeObserver(gameDetailsListObserver);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);

        //initialize views and variables
        initialization();

        return view;
    }
    //endregion

    //region #Click Listeners
    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        if (CommonUtils.isClickDisabled()) {
            return;
        }

        mediaUtils.playButtonSound();

        switch (view.getId()) {
            //Back
            case R.id.iv_back:
                mainActivity.onBackPressed();
                break;

            //Default
            default:
                if (Constants.IS_DEBUG_ENABLE) {
                    showAlert("HistoryFragment Default Click Called.");
                }
                break;
        }
    }
    //endregion

    //region #Custom Methods
    private void initialization() {
        //Set View Selected
        setViewSelected();

        historyRecordLimit = mainActivity.getCommonConfiguration().getHistoryRecordLimit();
        if (historyRecordLimit <= 0) {
            historyRecordLimit = 10;
        }

        //Pull to Refresh Listener initialization
        srlHistory.setOnRefreshListener(this);

        tvHistoryLimit.setText(getResources().getString(R.string.message_history_limit, historyRecordLimit));

        loadHistory();
    }

    /**
     * Set View Selected
     */
    private void setViewSelected() {
        tvTitle.setSelected(true);
        tvHistoryLimit.setSelected(true);
        tvEmptyViewTitle.setSelected(true);
    }

    /**
     * Load History
     */
    private void loadHistory() {
        //Start Tracing History List Performance Monitoring - Fetch, Update and Show List
        Trace historyTrace = FirebasePerformance.getInstance().newTrace(Constants.PerformanceMonitoring.HISTORY_TRACE);
        historyTrace.start();

        historyList.clear();

        //Get Categories
        gameDetailsListObserver = gameDetailsList -> {
            if (srlHistory.isRefreshing()) {
                srlHistory.setRefreshing(false);
            }

            if (gameDetailsList != null && !gameDetailsList.isEmpty()) {
                Timber.e("History Size: %d", gameDetailsList.size());
                historyList.addAll(gameDetailsList);

                //Display Image List
                rvHistory.setVisibility(View.VISIBLE);
                llEmptyView.setVisibility(View.GONE);

                rvHistory.setLayoutManager(new LinearLayoutManager(mainActivity));
                historyAdapter = new HistoryAdapter(mainActivity, historyList);
                rvHistory.setAdapter(historyAdapter);
            } else {
                //Show Empty View
                rvHistory.setVisibility(View.GONE);
                llEmptyView.setVisibility(View.VISIBLE);

                tvEmptyViewTitle.setText(getString(R.string.title_no_data));
                tvEmptyViewMessage.setText(getString(R.string.message_no_history));
            }
        };
        mainActivity.getDatabaseHelper().getAllGameDetails(historyRecordLimit).observe(mainActivity, gameDetailsListObserver);

        //Stop Theme Trace Performance Monitoring
        historyTrace.stop();
    }
    //endregion

    //region #Method from SwipeRefreshLayout.OnRefreshListener

    /**
     * Implemented from SwipeRefreshLayout.OnRefreshListener
     */
    @Override
    public void onRefresh() {
        loadHistory();
    }
    //endregion
}
