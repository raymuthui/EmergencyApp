package com.example.Safenow.ui.recordings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.Safenow.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RecordingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recordings, container, false);

        TabLayout tabLayout = root.findViewById(R.id.tabLayout);
        ViewPager2 viewPager = root.findViewById(R.id.viewPager);

        RecordingsPagerAdapter adapter = new RecordingsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Audio Recordings");
                    break;
                case 1:
                    tab.setText("Video Recordings");
                    break;
            }
        }).attach();

        return root;
    }
}
