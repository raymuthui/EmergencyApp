package com.example.InstaSOS.ui.recordings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.InstaSOS.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioRecordingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_audio_recordings, container, false);
        ListView listView = root.findViewById(R.id.listView);

        List<String> recordings = getRecordings();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, recordings);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = recordings.get(position);
            playAudio(fileName);
        });

        return root;
    }

    private List<String> getRecordings() {
        List<String> recordings = new ArrayList<>();
        File recordingsDir = new File(requireContext().getFilesDir(), "InstaSOS/Recordings/Audios");
        if (recordingsDir.exists() && recordingsDir.isDirectory()) {
            File[] files = recordingsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    recordings.add(file.getName());
                }
            }
        }
        return recordings;
    }

    private void playAudio(String fileName) {
        File recordingsDir = new File(requireContext().getFilesDir(), "InstaSOS/Recordings/Audios");
        File audioFile = new File(recordingsDir, fileName);
        Uri audioUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", audioFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(audioUri, "audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }
}
