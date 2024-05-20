package com.example.InstaSOS;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class VoiceActivationService extends Service implements RecognitionListener {

    private SpeechRecognizer speechRecognizer;
    private SharedPreferences sharedPreferences;
    private Intent recognizerIntent;
    private AudioManager mAudioManager;

    private static final String TAG = "VoiceActivationService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);

        // Start continuous listening
        startListening();

    }
    private void startListening() {
//        try {
//            mAudioManager.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
//        } catch (SecurityException e) {
//            // Handle the security exception (e.g., show a message to the user)
//            Log.e(TAG, "SecurityException: Not allowed to change Do Not Disturb state");
//            // Optionally, revert to default behavior or inform the user
//        }

        // Continue with your other logic
        speechRecognizer.startListening(createRecognizerIntent());
    }


    private Intent createRecognizerIntent() {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en");
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        return recognizerIntent;
    }

    private void stopListening() {
        mAudioManager.setStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);

        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
            speechRecognizer.destroy();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopListening(); // Stop listening before destroying the service
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null; // Release the SpeechRecognizer object
        }
        Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        // Handle when the recognizer is ready for speech
    }

    @Override
    public void onBeginningOfSpeech() {
        // Handle the beginning of speech
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // Handle RMS changes if needed
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // Handle Buffer
    }

    @Override
    public void onEndOfSpeech() {
        // Handle the end of speech
    }

    @Override
    public void onError(int error) {
        Log.e(TAG, "Speech recognition error: " + error);
        // Handle recognition errors
        // Restart listening after an error occurs
        startListening();
    }

    @Override
    public void onResults(Bundle results) {
        // Handle speech recognition results
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        assert matches != null;
        process(matches);

        // Restart listening after processing results
        startListening();
    }

    @Override
    public void onPartialResults(Bundle partialResults) {

    }

    @Override
    public void onEvent(int eventType, Bundle params) {

    }

    // Other RecognitionListener methods...

    private void process(ArrayList<String> suggestedWords) {
        // Your existing processing logic here
        // ...

        // Example: check for the wake word "emergency"
        for (String word : suggestedWords) {
            if (word.contains("emergency")) {
                // Trigger your emergency functions
                //makeCall();
                //getCurrentLocation();
                sendTextWithLocation();
                //storeVoiceRecordings();
                // ... (other actions)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation(String phoneNumber) {
     fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

     locationCallback = new LocationCallback() {
         @Override
         public void onLocationResult(@NonNull LocationResult locationResult) {
             Location location = locationResult.getLastLocation();
             if (location != null) {
                 // Location retrieved, send text message with location
                 Log.i(TAG, "Location retrieved: " + location.getLatitude() + ", " + location.getLongitude());
                 sendSMS(phoneNumber, location);
                 // Stop location updates after receiving the location
                 stopLocationUpdates();
             } else {
                 Log.e(TAG, "Location is null");
             }
         }
     };

     // Request location updates
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10000); // Update interval in milliseconds
        Log.i(TAG, "Requesting location updates");
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void stopLocationUpdates() {
        if (fusedLocationProviderClient != null && locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    private void storeVoiceRecordings() {
    }

    private void sendTextWithLocation() {
        Log.i(TAG, "Send text method executing");
        // Retrieve phone numbers from Room database using ViewModel
        ContactsRepository contactsRepository = new ContactsRepository(getApplication());
        contactsRepository.getAllContacts().observe((LifecycleOwner) this, contactLists -> {
            for (ContactList contact : contactLists) {
                String phoneNumber = contact.getPhoneNumber();
                // Pass phone number to getCurrentLocation method
                getCurrentLocation(phoneNumber);
            }
        });
    }

    private void sendSMS(String phoneNumber, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        SmsManager smsManager = SmsManager.getDefault();
        String message = "Help! I am in danger.\n" +
                "My current location is: " + "https://maps.google.com/maps?q=" + latitude + "," + longitude +
                "\nThis is an automated message.";
        try {
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Log.i(TAG, "SMS sent to: " + phoneNumber);
        } catch (Exception e) {
            Log.e(TAG, "Failed to send SMS: " + e.getMessage());
        }
    }

    // Your existing methods...

    private void makeCall() {
        Log.i(TAG, "Make call");
        String phoneNumber = sharedPreferences.getString("localEmergency", "");
        Log.i(TAG, "Phone number is: " + phoneNumber);

        // Check if phone number is not empty and is valid
        if (!phoneNumber.isEmpty() && phoneNumber.matches("^\\+[0-9]+$")) {
            Intent phoneIntent = new Intent(Intent.ACTION_CALL);
            phoneIntent.setData(Uri.parse("tel:" + phoneNumber));
            phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(phoneIntent);
                Log.i(TAG, "Finished making a call.");
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(),
                        "Call failed, please try again later.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Invalid phone number. Please enter a valid phone number in the settings.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    // ... (other methods)

}
