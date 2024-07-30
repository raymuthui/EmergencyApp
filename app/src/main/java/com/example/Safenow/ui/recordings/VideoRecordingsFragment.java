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

public class VideoRecordingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_video_recordings, container, false);
        ListView listView = root.findViewById(R.id.listView);

        List<File> recordings = getRecordings();
        ArrayAdapter<File> adapter = new ArrayAdapter<File>(requireContext(), R.layout.list_item_video, R.id.videoTitle, recordings) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_video, parent, false);
                }

                File file = getItem(position);
                TextView title = convertView.findViewById(R.id.videoTitle);
                ImageButton shareButton = convertView.findViewById(R.id.shareButton);
                ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);

                title.setText(file != null ? file.getName() : "Unknown");

                shareButton.setOnClickListener(v -> shareVideo(file));
                deleteButton.setOnClickListener(v -> deleteVideo(file));

                convertView.setOnClickListener(v -> playVideo(file));

                return convertView;
            }
        };

        listView.setAdapter(adapter);

        return root;
    }

    private List<File> getRecordings() {
        List<File> recordings = new ArrayList<>();
        File recordingsDir = new File(requireContext().getFilesDir(), "Safenow/Recordings/Videos");
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

    private void playVideo(File videoFile) {
        Uri videoUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", videoFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(videoUri, "video/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void shareVideo(File videoFile) {
        Uri videoUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", videoFile);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/*");
        intent.putExtra(Intent.EXTRA_STREAM, videoUri);
        startActivity(Intent.createChooser(intent, "Share Video"));
    }

    private void deleteVideo(File videoFile) {
        if (videoFile.delete()) {
            Toast.makeText(requireContext(), "Video deleted.", Toast.LENGTH_SHORT).show();
            // Refresh the list
            ListView listView = getView().findViewById(R.id.listView);
            ((ArrayAdapter<File>) listView.getAdapter()).remove(videoFile);
        } else {
            Toast.makeText(requireContext(), "Failed to delete video.", Toast.LENGTH_SHORT).show();
        }
    }
}
