package com.sleepsense.snoozebud.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudPrefs;
import com.sleepsense.snoozebud.SnoozebudSsh;

import static java.lang.Integer.parseInt;

/**
 * Created by shayne on 2017-12-06.
 */

public class SetSensitivityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sensitivity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sensitivity_tb);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Set sensitivity");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Button setButton = (Button) findViewById(R.id.set_sensitivity_bt);
        final TextView sensitivityTv = (TextView) findViewById(R.id.sensitivity_et);
        sensitivityTv.setText(
                SnoozebudPrefs.getPref("SENSITIVITY", this),
                TextView.BufferType.EDITABLE);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

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

                SnoozebudPrefs.setPref("SENSITIVITY", sensitivity, SetSensitivityActivity.this);

                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        TextView progressTv = (TextView) findViewById(R.id.sens_progress_tv);
                        progressTv.setText(
                                "Setting SnoozeBud sensitivity...",
                                TextView.BufferType.NORMAL);
                        findViewById(R.id.sens_progress_ll).setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            SnoozebudSsh.setPiConfig(SetSensitivityActivity.this);
                            SnoozebudSsh.restartSystem(
                                    SnoozebudSsh.setupSession(SetSensitivityActivity.this));
                        } catch (Exception e) {
                            return e.getMessage();
                        }
                        return "done";
                    }

                    protected void onPostExecute(String result) {
                        findViewById(R.id.sens_progress_ll).setVisibility(View.INVISIBLE);
                        if (result.equals("done")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "The SnoozeBud sensitivity has been set. Restarting the SnoozeBud...",
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Error: " + result,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                        SetSensitivityActivity.this.finish();
                    }
                }.execute(1);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
