package com.example.InstaSOS.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.InstaSOS.LocationStorage;
import com.example.InstaSOS.R;
import com.example.InstaSOS.databinding.FragmentHomeBinding;
import com.example.InstaSOS.VoiceActivationService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
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

    private final ActivityResultLauncher<Intent> audioRecorderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri audioUri = result.getData().getData();
                    saveAudioToInternalStorage(audioUri);
                    Toast.makeText(requireContext(), "Audio recorded successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Recording cancelled.", Toast.LENGTH_SHORT).show();
                }
                resumeVoiceActivationService();
            });

    private final ActivityResultLauncher<Intent> videoRecorderLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri videoUri = result.getData().getData();
                    saveVideoToInternalStorage(videoUri);
                    Toast.makeText(requireContext(), "Video recorded successfully.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Recording cancelled.", Toast.LENGTH_SHORT).show();
                }
                resumeVoiceActivationService();
            });

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean audioGranted = result.getOrDefault(android.Manifest.permission.RECORD_AUDIO, false);
                Boolean videoGranted = result.getOrDefault(android.Manifest.permission.CAMERA, false);

                if (audioGranted != null && audioGranted) {
                    launchAudioRecorder();
                } else if (videoGranted != null && videoGranted) {
                    startVideoRecording();
                } else {
                    Toast.makeText(requireContext(), "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
            });

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
        new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Add SOS button click listener
        Button sosButton = root.findViewById(R.id.sosButton);
        sosButton.setOnClickListener(v -> showOptionsDialog());

        // Add Call Police click listener
        View callPoliceLayout = root.findViewById(R.id.constraintLayout);
        callPoliceLayout.setOnClickListener(v -> callPolice());

        // Add Call Cab click listener
        View callCabLayout = root.findViewById(R.id.constraintLayout2);
        callCabLayout.setOnClickListener(v -> callCab());

        return root;
    }

    private void showOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Option");
        builder.setItems(new CharSequence[]{"Record Audio", "Record Video"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    requestPermissionsLauncher.launch(new String[]{
                            android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    });
                    break;
                case 1:
                    requestPermissionsLauncher.launch(new String[]{
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    });
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
            audioRecorderLauncher.launch(intent);
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
            videoRecorderLauncher.launch(intent);
        } else {
            Toast.makeText(requireContext(), "No video recording app found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveAudioToInternalStorage(Uri audioUri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(audioUri);
             FileOutputStream outputStream = new FileOutputStream(getOutputFile("InstaSOS/Recordings/Audios", "audio_record_", ".m4a"))) {

            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }
            Toast.makeText(requireContext(), "Audio saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error saving audio to internal storage", e);
            Toast.makeText(requireContext(), "Error saving audio to internal storage.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveVideoToInternalStorage(Uri videoUri) {
        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(videoUri);
             FileOutputStream outputStream = new FileOutputStream(getOutputFile("InstaSOS/Recordings/Videos", "video_record_", ".mp4"))) {

            byte[] buffer = new byte[1024];
            int length;
            while (true) {
                assert inputStream != null;
                if (!((length = inputStream.read(buffer)) > 0)) break;
                outputStream.write(buffer, 0, length);
            }
            Toast.makeText(requireContext(), "Video saved successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error saving video to internal storage", e);
            Toast.makeText(requireContext(), "Error saving video to internal storage.", Toast.LENGTH_SHORT).show();
        }
    }

    private File getOutputFile(String directory, String prefix, String suffix) throws IOException {
        File recordingsDir = new File(requireContext().getFilesDir(), directory);
        if (!recordingsDir.exists() && !recordingsDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + recordingsDir.getAbsolutePath());
        }
        return new File(recordingsDir, prefix + System.currentTimeMillis() + suffix);
    }

    private void resumeVoiceActivationService() {
        if (isServiceBound && voiceActivationService != null) {
            voiceActivationService.startListening();
            Log.d(TAG, "Voice activation service resumed.");
        }
    }

    private void callPolice() {
        String phoneNumber = "tel:+254713208001";
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(phoneNumber));
        startActivity(callIntent);
    }

    private void callCab() {
        Double pickupLatitude = LocationStorage.getInstance().getLatitude(); // Example latitude
        Double pickupLongitude = LocationStorage.getInstance().getLongitude();  // Example longitude

        String uri = "uber://?action=setPickup&pickup[latitude]=" + pickupLatitude + "&pickup[longitude]=" + pickupLongitude;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        PackageManager packageManager = requireContext().getPackageManager();

        // Check if the Uber app is installed
        boolean isUberInstalled;
        try {
            packageManager.getPackageInfo("com.ubercab", PackageManager.GET_ACTIVITIES);
            isUberInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            isUberInstalled = false;
        }

        if (isUberInstalled) {
            // Open the Uber app if installed
            intent.setPackage("com.ubercab");
            startActivity(intent);
        } else {
            // Open the Uber website if the app is not installed
            uri = "https://m.uber.com/ul/?action=setPickup&pickup[latitude]=" + pickupLatitude + "&pickup[longitude]=" + pickupLongitude;
            intent.setData(Uri.parse(uri));
            Toast.makeText(requireContext(), "Uber app not found. Redirecting to the website.", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
