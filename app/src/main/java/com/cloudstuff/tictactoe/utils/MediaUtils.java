package com.cloudstuff.tictactoe.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.cloudstuff.tictactoe.R;
import com.cloudstuff.tictactoe.TicTacToe;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MediaUtils {

    private PreferenceUtils preferenceUtils;
    private Context context;
    private MediaPlayer gameMusicMediaPlayer;
    private MediaPlayer buttonClickMediaPlayer;
    private AudioAttributes audioAttribute;

    @Inject
    public MediaUtils(Context context) {
        this.context = context;
        preferenceUtils = TicTacToe.getInstance().getAppComponent().providePreferenceUtils();

        audioAttribute = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        initButtonSound();
        initMusic();
    }

    //Initialize Button Sound
    private void initButtonSound() {
        if (buttonClickMediaPlayer == null) {
            buttonClickMediaPlayer = MediaPlayer.create(context, R.raw.sound);
            buttonClickMediaPlayer.setVolume(100.0f, 100.0f);
            buttonClickMediaPlayer.setAudioAttributes(audioAttribute);
        }
    }

    //Initialize Music
    private void initMusic() {
        if (gameMusicMediaPlayer == null) {
            gameMusicMediaPlayer = MediaPlayer.create(context, R.raw.music);
            gameMusicMediaPlayer.setVolume(100.0f, 100.0f);
            gameMusicMediaPlayer.setLooping(true);
            buttonClickMediaPlayer.setAudioAttributes(audioAttribute);
        }
    }

    //Play Sound
    public void playButtonSound() {
        if (buttonClickMediaPlayer != null && preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AUDIO_ON)) {
            buttonClickMediaPlayer.start();
        }
    }

    //Play Music
    public void playMusic() {
        if (gameMusicMediaPlayer != null && preferenceUtils.getBoolean(Constants.PreferenceConstant.IS_AUDIO_ON)) {
            gameMusicMediaPlayer.start();
        }
    }

    //Stop Sound
    public void stopButtonSound() {
        if (buttonClickMediaPlayer != null) {
            buttonClickMediaPlayer.stop();
        }
    }

    //Pause Music
    public void pauseMusic() {
        if (gameMusicMediaPlayer != null && gameMusicMediaPlayer.isPlaying()) {
            gameMusicMediaPlayer.pause();
        }
    }
}
