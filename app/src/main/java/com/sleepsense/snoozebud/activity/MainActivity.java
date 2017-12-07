package com.sleepsense.snoozebud.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudNotification;
import com.sleepsense.snoozebud.SnoozebudSsh;
import com.sleepsense.snoozebud.service.SnoozebudService;

import org.json.JSONObject;

/**
 * Connects to Raspberry Pi and starts an SSH session.
 * Raspberry Pi should be connected to the phone by USB tethering
 * while this is running.
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SSH_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SnoozebudNotification.setupNotifications(this);

        // Keep app awake in the foreground for monitoring
        Intent serviceIntent = new Intent(this, SnoozebudService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Button wifiButton = (Button)findViewById(R.id.wifi_set_bt);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNetworkActivity.class));
            }
        });

        Button configButton = (Button)findViewById(R.id.config_setup_bt);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SetSensitivityActivity.class));
            }
        });

        Button testAlarmButton = (Button)findViewById(R.id.test_alarm_bt);
        testAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlarmActivity.class));
            }
        });
    }
}
