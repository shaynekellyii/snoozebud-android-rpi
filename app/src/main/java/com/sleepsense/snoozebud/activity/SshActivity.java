package com.sleepsense.snoozebud.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudSsh;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Connects to Raspberry Pi and starts an SSH session.
 * Raspberry Pi should be connected to the phone by USB tethering
 * while this is running.
 */

public class SshActivity extends AppCompatActivity {

    private static final String SSH_CHANNEL_TYPE = "exec";
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

        new AsyncTask<Integer, Void, String>() {
            @Override
            protected String doInBackground(Integer... params) {
                try {
                    String response = sshIntoPi();
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

        // TODO: Send Firebase ID to SnoozeBud
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
    }


    private String sshIntoPi() throws Exception {
        try {
            JSch sshChannel = new JSch();

            // TODO: Add values to Keys class, set static IP on SnoozeBud
            Session session = sshChannel.getSession("pi", "142.58.167.2", 22);
            session.setPassword("snoozebud");
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Log.d("SSH", "Session is connected");

            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'wpa_passphrase " + ssid + " " + wifiPassword +
                            " >> /etc/wpa_supplicant/wpa_supplicant.conf'");
            SnoozebudSsh.executeSshCommand(session, "sudo shutdown -r now");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }
}
