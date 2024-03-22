package com.example.InstaSOS;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // Handle other accessibility events if needed
    }

    @Override
    public void onInterrupt() {
        // Handle interruption
    }

    @Override
    protected void onServiceConnected() {
        Log.d("AccessibilityService", "Service connected");

        // Request accessibility shortcut feature for hardware button
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON | AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        setServiceInfo(info);

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
