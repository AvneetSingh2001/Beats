package com.example.beats;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.loader.AssetsProvider;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {

    private static final String TAG = "BeatBox";
    private static final String SOUND_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;
    private float mPlaybackSpeedRate;
    private String mPlaybackSpeedLabel;

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .setMaxStreams(MAX_SOUNDS)
                    .build();
        } else {
            //noinspection deprecation
            mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        }
        setPlaybackSpeedRate(1.0f);
        setPlaybackSpeedLabel(context.getString(R.string.playback_label));
        loadSounds();
    }

    public void play(Sound sound) {
        play(sound, mPlaybackSpeedRate);
    }

    public void play(Sound sound, float rate) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 1.0f, 1, 0, rate);
    }

    public void release() {
        mSoundPool.release();
    }

    private void loadSounds() {
        String[] soundNames;
        try {
            soundNames = mAssets.list(SOUND_FOLDER);
            Log.i(TAG, "Found " + soundNames.length + " sounds");
        } catch (IOException ioe) {
            Log.e(TAG, "Could not list assets", ioe);
            return;
        }
        for (String filename : soundNames) {
            try {
                String assetPath = SOUND_FOLDER + "/" + filename;
                Sound sound = new Sound(assetPath);
                load(sound);
                mSounds.add(sound);
            } catch (IOException ioe) {
                Log.e(TAG, "Could not load sound " + filename, ioe);
            }
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor afd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(afd, 1);
        sound.setSoundId(soundId);
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public void setPlaybackSpeedRate(float rate) {
        mPlaybackSpeedRate = rate;
    }

    public float getPlaybackSpeedRate() {
        return mPlaybackSpeedRate;
    }

    public String getPlaybackSpeedLabel() {
        return mPlaybackSpeedLabel;
    }

    public void setPlaybackSpeedLabel(String playbackSpeedLabel) {
        mPlaybackSpeedLabel = playbackSpeedLabel;
    }

}
