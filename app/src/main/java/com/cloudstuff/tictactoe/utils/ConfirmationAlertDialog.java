package com.cloudstuff.tictactoe.utils;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textview.MaterialTextView;
import com.cloudstuff.tictactoe.R;

public class ConfirmationAlertDialog {

    private static AlertDialog confirmationDialog;

    public static void showConfirmationDialog(Activity activity, boolean isCancelable, String title, String message,
                                              int positiveButtonVisibility, String positiveButtonText,
                                              int negativeButtonVisibility, String negativeButtonText,
                                              ConfirmationAlertDialogClickListener confirmationAlertDialogClickListener) {

        //validation if dialog is null or already open
        if (confirmationDialog != null && confirmationDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(isCancelable);

        ViewGroup viewGroup = activity.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_confirmation, viewGroup, false);

        MaterialTextView tvTitle = dialogView.findViewById(R.id.tv_title);
        MaterialTextView tvMessage = dialogView.findViewById(R.id.tv_message);
        LinearLayout llPositiveButton = dialogView.findViewById(R.id.ll_positive_button);
        AppCompatButton btnPositive = dialogView.findViewById(R.id.btn_positive);
        LinearLayout llNegativeButton = dialogView.findViewById(R.id.ll_negative_button);
        AppCompatButton btnNegative = dialogView.findViewById(R.id.btn_negative);

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        }

        tvTitle.setSelected(true);
        btnPositive.setSelected(true);
        btnNegative.setSelected(true);

        tvTitle.setText(title);
        tvMessage.setText(message);

        llPositiveButton.setVisibility(positiveButtonVisibility);
        btnPositive.setText(positiveButtonText);

        llNegativeButton.setVisibility(negativeButtonVisibility);
        btnNegative.setText(negativeButtonText);

        builder.setView(dialogView);

        confirmationDialog = builder.create();
        Window window = confirmationDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        btnPositive.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }

            confirmationDialog.dismiss();
            confirmationAlertDialogClickListener.onPositiveButtonClick();
        });

        btnNegative.setOnClickListener(view -> {
            if (CommonUtils.isClickDisabled()) {
                return;
            }

            confirmationDialog.dismiss();
            confirmationAlertDialogClickListener.onNegativeButtonClick();
        });

        confirmationDialog.show();
    }

    /**
     * Dismiss Confirmation Dialog
     */
    public static void dismissConfirmationDialog() {
        if (confirmationDialog != null && confirmationDialog.isShowing()) {
            confirmationDialog.dismiss();
        }
    }

    public abstract static class ConfirmationAlertDialogClickListener {

        /**
         * Positive Button Click
         */
        public abstract void onPositiveButtonClick();

        /**
         * Negative Button Click
         */
        public void onNegativeButtonClick() {
            //override only if necessary
        }
    }
}