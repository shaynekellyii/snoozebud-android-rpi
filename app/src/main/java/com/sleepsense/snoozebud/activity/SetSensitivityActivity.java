package com.sleepsense.snoozebud.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudSsh;

import static java.lang.Integer.parseInt;

/**
 * Created by shayne on 2017-12-06.
 */

public class SetSensitivityActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sensitivity);

        Button setButton = (Button) findViewById(R.id.set_sensitivity_bt);
        final TextView sensitivityTv = (TextView) findViewById(R.id.sensitivity_et);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String sensitivity;

                try {
                    Integer.parseInt(sensitivityTv.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(
                            SetSensitivityActivity.this,
                            "Sensitivity must be a number above 0.",
                            Toast.LENGTH_LONG)
                            .show();
                    SetSensitivityActivity.this.finish();
                }

                sensitivity = sensitivityTv.getText().toString();
                if (parseInt(sensitivity) <= 0) {
                    Toast.makeText(
                            SetSensitivityActivity.this,
                            "Sensitivity must be a number above 0",
                            Toast.LENGTH_LONG)
                            .show();
                    SetSensitivityActivity.this.finish();
                }

                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            String response = setMonitoringConfig(sensitivity);
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
                        SetSensitivityActivity.this.finish();
                    }
                }.execute(1);
            }
        });
    }

    private String setMonitoringConfig(String sensitivity) throws Exception {
        try {
            Session session = SnoozebudSsh.setupSession();

            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'echo {\\\"sensitivity\\\": "
                            + sensitivity
                            + ",  > /home/pi/snoozebud-rpi/config.json'");
            SnoozebudSsh.executeSshCommand(session,
                    "sudo sh -c 'echo \\\"firebase_id\\\": \\\""
                            + FirebaseInstanceId.getInstance().getToken()
                            + "\\\"} >> /home/pi/snoozebud-rpi/config.json'");
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return "done";
    }
}
