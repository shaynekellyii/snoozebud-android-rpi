package com.sleepsense.snoozebud.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudSsh;

import org.json.JSONObject;

/**
 * Connects to Raspberry Pi and starts an SSH session.
 * Raspberry Pi should be connected to the phone by USB tethering
 * while this is running.
 */

public class SshActivity extends AppCompatActivity {
    private static final String TAG = "SSH_ACTIVITY";
    private static final int ASYNC_START = 1;

    private String ssid;
    private String wifiPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ssh);

        Bundle extras = getIntent().getExtras();
        ssid = extras.getString("SSID");
        wifiPassword = extras.getString("PASSWORD");

        // TODO: Save wifi params into SharedPreferences?

        final TextView firebaseIdTv = (TextView)findViewById(R.id.firebase_id_tv);
        firebaseIdTv.setText(FirebaseInstanceId.getInstance().getToken());
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FCM_ID", FirebaseInstanceId.getInstance().getToken());
        editor.apply();

        firebaseIdTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager =
                        (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData =
                        ClipData.newPlainText("SnoozeBud", firebaseIdTv.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(
                        getApplicationContext(),
                        "Firebase ID copied to clipboard",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });

        Button wifiButton = (Button)findViewById(R.id.wifi_set_bt);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            String response = setWifiConfig();
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
                    }
                }.execute(ASYNC_START);
            }
        });

        Button configButton = (Button)findViewById(R.id.config_setup_bt);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            String response = setMonitoringConfig();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    protected void onPostExecute(String result) {
                        Toast.makeText(
                                getApplicationContext(),
                                "The config details have been added to the SnoozeBud",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }.execute(ASYNC_START);
            }
        });
    }


    private String setWifiConfig() throws Exception {
        try {
            Session session = SnoozebudSsh.setupSession();

            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'wpa_passphrase " + ssid + " " + wifiPassword +
                            " >> /etc/wpa_supplicant/wpa_supplicant.conf'");
            SnoozebudSsh.executeSshCommand(session, "sudo shutdown -r now");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }

    private String setMonitoringConfig() throws Exception {
        try {
            Session session = SnoozebudSsh.setupSession();

            JSONObject configJson = new JSONObject();
            configJson.put("sensitivity", 10);
            configJson.put("firebase_id", FirebaseInstanceId.getInstance().getToken());
            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'echo {\\\"sensitivity\\\": 10,  > /home/pi/snoozebud-rpi/config.json'");
            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'echo \\\"firebase_id\\\": \\\"" + FirebaseInstanceId.getInstance().getToken() +
                            "\\\"} >> /home/pi/snoozebud-rpi/config.json'");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }
}
