package com.example.beats;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class BeatBoxActivity extends SingleFragmentActivity {

    @Override
    public Fragment createFragment() {
        return BeatBoxFragment.getInstance();
    }


}