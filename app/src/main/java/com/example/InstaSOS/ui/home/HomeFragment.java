package com.example.InstaSOS.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.InstaSOS.R;
import com.example.InstaSOS.databinding.FragmentHomeBinding;
import com.example.InstaSOS.VoiceActivationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {

    private static final int REQUEST_AUDIO_PERMISSION = 1;
    private static final int REQUEST_VIDEO_PERMISSION = 2;

    private FragmentHomeBinding binding;
    private VoiceActivationService voiceActivationService;
    private boolean isServiceBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VoiceActivationService.LocalBinder binder = (VoiceActivationService.LocalBinder) service;
            voiceActivationService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            voiceActivationService = null;
            isServiceBound = false;
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Intent intent = new Intent(context, VoiceActivationService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Add SOS button click listener
        Button sosButton = root.findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v -> showOptionsDialog());
        return root;
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Option");
        builder.setItems(new CharSequence[]{"Record Audio", "Record Video"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    launchAudioRecorder();
                    break;
                case 1:
                    startVideoRecording();
                    break;
            }
        });
        builder.create().show();
    }

    private void launchAudioRecorder() {
        if (isServiceBound && voiceActivationService != null) {
            voiceActivationService.stopListening();
        }

        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_AUDIO_PERMISSION);
        } else {
            Toast.makeText(requireContext(), "No audio recording app found.", Toast.LENGTH_SHORT).show();
        }
    }
    private void startVideoRecording() {
        if (isServiceBound && voiceActivationService != null) {
            voiceActivationService.stopListening();
        }

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_PERMISSION);
        } else {
            Toast.makeText(requireContext(), "No video recording app found.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (isServiceBound && voiceActivationService != null) {
            voiceActivationService.startListening();
        }

        if (requestCode == REQUEST_AUDIO_PERMISSION && resultCode == Activity.RESULT_OK) {
            Uri audioUri = data.getData();
            // Save the audio file to internal storage
            saveAudioToInternalStorage(audioUri);
            Toast.makeText(requireContext(), "Audio recorded successfully.", Toast.LENGTH_SHORT).show();
        } else if (requestCode == REQUEST_VIDEO_PERMISSION && resultCode == Activity.RESULT_OK) {
            Uri videoUri = data.getData();
            // Do something with the recorded video URI, such as save it or upload it
            saveVideoToInternalStorage(videoUri);
            Toast.makeText(requireContext(), "Video recorded successfully.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(requireContext(), "Recording cancelled.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAudioToInternalStorage(Uri audioUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(audioUri);

            // Create the destination directory if it doesn't exist
            File recordingsDir = new File(requireContext().getFilesDir(), "InstaSOS/Recordings/Audios");
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Generate a unique filename for the saved audio file
            String audioFileName = "audio_record_" + System.currentTimeMillis() + ".m4a";
            File destAudioFile = new File(recordingsDir, audioFileName);

            FileOutputStream outputStream = new FileOutputStream(destAudioFile);
            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            Toast.makeText(requireContext(), "Audio saved: " + destAudioFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error saving audio to internal storage.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVideoToInternalStorage(Uri videoUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(videoUri);

            // Create the destination directory if it doesn't exist
            File recordingsDir = new File(requireContext().getFilesDir(), "InstaSOS/Recordings/Videos");
            if (!recordingsDir.exists()) {
                recordingsDir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Generate a unique filename for the saved video file
            String videoFileName = "video_record_" + System.currentTimeMillis() + ".mp4";
            File destVideoFile = new File(recordingsDir, videoFileName);

            FileOutputStream outputStream = new FileOutputStream(destVideoFile);
            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            Toast.makeText(requireContext(), "Video saved: " + destVideoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error saving video to internal storage.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchAudioRecorder();
            } else {
                Toast.makeText(requireContext(), "Audio recording permission denied.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_VIDEO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                startVideoRecording();
            } else {
                Toast.makeText(requireContext(), "Video recording permissions denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
