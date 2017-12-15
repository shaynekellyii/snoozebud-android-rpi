package com.sleepsense.snoozebud.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudSsh;

/**
 * Created by shayne on 2017-11-24.
 * For user to enter the details of the WiFi network to connect to.
 */

public class AddNetworkActivity extends AppCompatActivity {

    Button submitButton;
    CheckBox securedCheckBox;
    TextView ssidTextView;
    TextView passwordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_network);

        submitButton = (Button)findViewById(R.id.bt_submit);
        ssidTextView = (TextView)findViewById(R.id.tv_ssid);
        passwordTextView = (TextView)findViewById(R.id.tv_password);
        securedCheckBox = (CheckBox)findViewById(R.id.secured_cb);

        securedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                passwordTextView.setEnabled(b);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String ssid = ssidTextView.getText().toString();
                final String password;

                if (securedCheckBox.isChecked()) {
                    password = passwordTextView.getText().toString();

                    if (password.length() < 8) {
                        Toast.makeText(
                                AddNetworkActivity.this,
                                "Password must be at least 8 characters.",
                                Toast.LENGTH_LONG)
                                .show();
                        return;
                    }
                } else {
                    password = "";
                }

//                SharedPreferences sharedPreferences =
//                        PreferenceManager.getDefaultSharedPreferences(view.getContext());
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("SSID", ssid);
//                if (securedCheckBox.isChecked()) {
//                    editor.putString("PASSWORD", password);
//                }
//                editor.apply();

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
    }

    private String setWifiConfig(String ssid, String password) throws Exception {
        try {
            Session session = SnoozebudSsh.setupSession();

            if (!password.equals("")) {
                SnoozebudSsh.executeSshCommand(session,
                        "sudo sh -c 'wpa_passphrase " + ssid + " " + password +
                                " >> /etc/wpa_supplicant/wpa_supplicant.conf'");
            } else {
                // TODO: Need to fix.
                SnoozebudSsh.executeSshCommand(session,
                        "sudo sh -c 'echo network={ >> /etc/wpa_supplicant/wpa_supplicant.conf'");
                SnoozebudSsh.executeSshCommand(session,
                        "sudo sh -c 'echo ssid=\\\"" + ssid + "\\\" >> /etc/wpa_supplicant/wpa_supplicant.conf'");
                SnoozebudSsh.executeSshCommand(session,
                        "sudo sh -c 'echo key_mgmt=NONE >> /etc/wpa_supplicant/wpa_supplicant.conf'");
                SnoozebudSsh.executeSshCommand(session,
                        "sudo sh -c 'echo } >> /etc/wpa_supplicant/wpa_supplicant.conf'");
            }

            SnoozebudSsh.executeSshCommand(session, "sudo shutdown -r now");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }
}
