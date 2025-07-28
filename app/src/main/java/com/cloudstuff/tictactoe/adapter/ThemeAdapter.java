package com.cloudstuff.tictactoe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.annotation.ThemeUnlockType;
import com.cloudstuff.tictactoe.listener.ThemeClickListener;
import com.cloudstuff.tictactoe.model.Theme;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    //region #Variables
    private Context context;
    private List<Theme> themeList;
    private ThemeClickListener themeClickListener;
    //endregion

    //region #Constructors
    public ThemeAdapter(Context context, List<Theme> themeList, ThemeClickListener themeClickListener) {
        this.context = context;
        this.themeList = themeList;
        this.themeClickListener = themeClickListener;
    }
    //endregion

    //region #InBuilt Methods
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_theme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Theme theme = themeList.get(holder.getAdapterPosition());

        if (theme.isThemeUnlock()) {
            holder.ivThemeType.setVisibility(View.GONE);
            holder.tvUnlockType.setVisibility(View.GONE);
        } else {
            holder.ivThemeType.setVisibility(View.VISIBLE);
            holder.tvUnlockType.setVisibility(View.VISIBLE);

            if (theme.getThemeUnlockType().equals(ThemeUnlockType.IN_APP)) {
                holder.ivThemeType.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_dollar));
                holder.tvUnlockType.setText(context.getResources().getString(R.string.title_purchase));
            } else if (theme.getThemeUnlockType().equals(ThemeUnlockType.ADVERTISE)) {
                holder.ivThemeType.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.ic_video));
                holder.tvUnlockType.setText(context.getResources().getString(R.string.title_advertise));
            } else {
                holder.ivThemeType.setVisibility(View.GONE);
                holder.tvUnlockType.setVisibility(View.GONE);
            }
        }

        holder.ivThemeCross.setBackgroundDrawable(ContextCompat.getDrawable(context, theme.getCrossIcon()));

        holder.ivThemeCircle.setBackgroundDrawable(ContextCompat.getDrawable(context, theme.getCircleIcon()));

        holder.tvThemeName.setText(theme.getThemeName());
        if (theme.isSelected()) {
            holder.tvThemeName.setTextColor(ContextCompat.getColor(context, R.color.theme_selected_name_text_color));
        } else {
            holder.tvThemeName.setTextColor(ContextCompat.getColor(context, R.color.theme_name_text_color));
        }

        holder.llTheme.setOnClickListener(view ->
                themeClickListener.onThemeClick(theme, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }
    //endregion

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ll_theme)
        LinearLayout llTheme;
        @BindView(R.id.iv_theme_cross)
        AppCompatImageView ivThemeCross;
        @BindView(R.id.iv_theme_circle)
        AppCompatImageView ivThemeCircle;
        @BindView(R.id.tv_theme_name)
        MaterialTextView tvThemeName;
        @BindView(R.id.iv_theme_type)
        AppCompatImageView ivThemeType;
        @BindView(R.id.tv_unlock_type)
        MaterialTextView tvUnlockType;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            tvThemeName.setSelected(true);
        }
    }
}