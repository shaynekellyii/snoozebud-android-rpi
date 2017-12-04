package com.sleepsense.snoozebud.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudNotification;
import com.sleepsense.snoozebud.service.SnoozebudService;

/**
 * Created by shayne on 2017-11-24.
 * For user to enter the details of the WiFi network to connect to.
 */

public class SetupActivity extends AppCompatActivity {

    Button submitButton;
    TextView ssidTextView;
    TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        submitButton = (Button)findViewById(R.id.bt_submit);
        ssidTextView = (TextView)findViewById(R.id.tv_ssid);
        passwordTextView = (TextView)findViewById(R.id.tv_password);

        SnoozebudNotification.setupNotifications(this);

        Intent serviceIntent = new Intent(this, SnoozebudService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), SshActivity.class);
                Bundle extras = new Bundle();
                extras.putString("SSID", ssidTextView.getText().toString());
                extras.putString("PASSWORD", passwordTextView.getText().toString());
                intent.putExtras(extras);

                startActivity(intent);
            }
        });
    }
}
