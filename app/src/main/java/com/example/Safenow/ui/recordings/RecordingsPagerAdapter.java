package com.example.Safenow.ui.recordings;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RecordingsPagerAdapter extends FragmentStateAdapter {

    public RecordingsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new VideoRecordingsFragment();
            default:
                return new AudioRecordingsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
