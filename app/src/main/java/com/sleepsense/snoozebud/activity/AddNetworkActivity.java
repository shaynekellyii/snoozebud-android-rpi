package com.sleepsense.snoozebud.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudNotification;
import com.sleepsense.snoozebud.SnoozebudSsh;
import com.sleepsense.snoozebud.service.SnoozebudService;

/**
 * Created by shayne on 2017-11-24.
 * For user to enter the details of the WiFi network to connect to.
 */

public class AddNetworkActivity extends AppCompatActivity {

    Button submitButton, testAlarmButton;
    TextView ssidTextView;
    TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_network);

        submitButton = (Button)findViewById(R.id.bt_submit);
        testAlarmButton = (Button)findViewById(R.id.test_alarm_bt);
        ssidTextView = (TextView)findViewById(R.id.tv_ssid);
        passwordTextView = (TextView)findViewById(R.id.tv_password);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ssid = ssidTextView.getText().toString();
                final String password = passwordTextView.getText().toString().length() < 8 ?
                        "FAKE_PASSWORD" : passwordTextView.getText().toString();

                // TODO: Add validation
                // TODO: Add support for unsecured network

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(view.getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("SSID", ssid);
                editor.putString("PASSWORD", password);
                editor.apply();

                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            String response = setWifiConfig(ssid, password);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(String result) {
                        Toast.makeText(
                                getApplicationContext(),
                                "The WiFi details have been added to the SnoozeBud",
                                Toast.LENGTH_LONG)
                                .show();
                        AddNetworkActivity.this.finish();
                    }
                }.execute(1);
            }
        });

        testAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), AlarmActivity.class));
            }
        });
    }

    private String setWifiConfig(String ssid, String password) throws Exception {
        try {
            Session session = SnoozebudSsh.setupSession();

            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'wpa_passphrase " + ssid + " " + password +
                            " >> /etc/wpa_supplicant/wpa_supplicant.conf'");
            SnoozebudSsh.executeSshCommand(session, "sudo shutdown -r now");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }
}
