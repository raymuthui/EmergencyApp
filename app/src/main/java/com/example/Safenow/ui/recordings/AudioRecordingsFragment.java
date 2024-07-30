package com.example.Safenow.ui.recordings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.Safenow.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AudioRecordingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_audio_recordings, container, false);
        ListView listView = root.findViewById(R.id.listView);

        List<File> recordings = getRecordings();
        ArrayAdapter<File> adapter = new ArrayAdapter<File>(requireContext(), R.layout.list_item_audio, R.id.audioTitle, recordings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_audio, parent, false);
                }

                File file = getItem(position);
                TextView title = convertView.findViewById(R.id.audioTitle);
                ImageButton shareButton = convertView.findViewById(R.id.shareButton);
                ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

                title.setText(file != null ? file.getName() : "Unknown");

                shareButton.setOnClickListener(v -> shareAudio(file));
                deleteButton.setOnClickListener(v -> deleteAudio(file));

                convertView.setOnClickListener(v -> playAudio(file));

                return convertView;
            }
        };

        listView.setAdapter(adapter);

        return root;
    }

    private List<File> getRecordings() {
        List<File> recordings = new ArrayList<>();
        File recordingsDir = new File(requireContext().getFilesDir(), "Safenow/Recordings/Audios");
        if (recordingsDir.exists() && recordingsDir.isDirectory()) {
            File[] files = recordingsDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    recordings.add(file);
                }
            }
        }
        return recordings;
    }

    private void playAudio(File audioFile) {
        Uri audioUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", audioFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(audioUri, "audio/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void shareAudio(File audioFile) {
        Uri audioUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", audioFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, audioUri);
        startActivity(Intent.createChooser(intent, "Share Audio"));
    }

    private void deleteAudio(File audioFile) {
        if (audioFile.delete()) {
            Toast.makeText(requireContext(), "Audio deleted.", Toast.LENGTH_SHORT).show();
            // Refresh the list
            ListView listView = getView().findViewById(R.id.listView);
            ((ArrayAdapter<File>) listView.getAdapter()).remove(audioFile);
        } else {
            Toast.makeText(requireContext(), "Failed to delete audio.", Toast.LENGTH_SHORT).show();
        }
    }
}
