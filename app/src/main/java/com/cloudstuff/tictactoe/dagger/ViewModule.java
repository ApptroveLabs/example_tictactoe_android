package com.cloudstuff.tictactoe.dagger;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @ViewScope
    @Provides
    InputMethodManager provideInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @ViewScope
    @Provides
    Handler provideSafeHandler() {
        return new Handler(Looper.getMainLooper());
    }
}
