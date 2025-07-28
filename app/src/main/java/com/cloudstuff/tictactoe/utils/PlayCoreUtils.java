package com.cloudstuff.tictactoe.utils;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class PlayCoreUtils {

    public static final int RC_FORCE_UPDATE = 987;

    private Context context;

    private ReviewInfo reviewInfo = null;
    private ReviewManager reviewManager = null;

    private AppUpdateManager appUpdateManager = null;

    @Inject
    public PlayCoreUtils(Context context) {
        this.context = context;

        appUpdateManager = AppUpdateManagerFactory.create(context);
        reviewManager = ReviewManagerFactory.create(context);
    }

    //region #InApp Review

    /**
     * Initialize In-App Review
     */
    public void initializeInAppReview() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();

        ((Task<?>) request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Timber.e("InAppReview: Task Successful");
                // We can get the ReviewInfo object
                reviewInfo = (ReviewInfo) task.getResult();
            } else {
                // There was some problem, continue regardless of the result.
                reviewInfo = null;
                Timber.e("InAppReview: Task Fail");
            }
        });
    }

    /**
     * Call In-App Review
     */
    public void callInAppReview(Activity activity) {
        if (reviewInfo != null) {
            Task<Void> flow = reviewManager.launchReviewFlow(activity, reviewInfo);

            flow.addOnSuccessListener(task1 -> {
                Timber.e("InAppReview: Success");
            });

            flow.addOnFailureListener(task1 -> {
                Timber.e("InAppReview: Failure");
            });

            flow.addOnCompleteListener(task1 -> {
                // The flow has finished. The API does not indicate whether the user
                // reviewed or not, or even whether the review dialog was shown. Thus, no
                // matter the result, we continue our app flow.
                Timber.e("InAppReview: Complete");

                //Re-Initialize Flow - as we can't detect user has reviewd or not
                initializeInAppReview();
            });
        }
    }
    //endregion

    /**
     * Check and request IMMEDIATE Update
     */
    public void checkInAppUpdate(Activity activity) {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        //Check update available or not
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if ((appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    || appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS)
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                //Request Update
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            activity,
                            RC_FORCE_UPDATE);
                } catch (IntentSender.SendIntentException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    e.printStackTrace();
                }
            }
        });
    }
}
