package com.sleepsense.snoozebud.activity;

import android.app.ProgressDialog;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudNotification;
import com.sleepsense.snoozebud.SnoozebudPrefs;
import com.sleepsense.snoozebud.SnoozebudSsh;
import com.sleepsense.snoozebud.service.SnoozebudService;

import org.json.JSONObject;

/**
 * Essentially the main menu of the app (for now).
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MAIN_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.sensitivity_tb);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SnoozeBud");
        }

        SnoozebudNotification.setupNotifications(this);
        SnoozebudPrefs.setupPrefs(this);

        // Keep app awake in the foreground for monitoring
        final Intent serviceIntent = new Intent(this, SnoozebudService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

        Button wifiButton = (Button) findViewById(R.id.wifi_set_bt);
        wifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNetworkActivity.class));
            }
        });

        Button configButton = (Button) findViewById(R.id.config_setup_bt);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SetSensitivityActivity.class));
            }
        });

        Button pairButton = (Button) findViewById(R.id.pair_bt);
        pairButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new AsyncTask<Integer, Void, String>() {
                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            TextView progressTv = (TextView)findViewById(R.id.main_progress_tv);
                            progressTv.setText(
                                    "Pairing phone to the SnoozeBud...",
                                    TextView.BufferType.NORMAL);
                            findViewById(R.id.main_progress_ll).setVisibility(View.VISIBLE);
                        }

                        @Override
                        protected String doInBackground(Integer... params) {
                            try {
                                SnoozebudSsh.setPiConfig(MainActivity.this);
                            } catch (Exception e) {
                                return e.getMessage();
                            }
                            return "done";
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);
                            findViewById(R.id.main_progress_ll).setVisibility(View.GONE);
                            if (s.equals("done")) {
                                Toast.makeText(
                                        MainActivity.this,
                                        "The device has been paired. The SnoozeBud is restarting.",
                                        Toast.LENGTH_LONG)
                                        .show();
                            } else {
                                Toast.makeText(
                                        MainActivity.this,
                                        "Error: " + s,
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    }.execute(1);
            }
        });

        Button testAlarmButton = (Button) findViewById(R.id.test_alarm_bt);
        testAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AlarmActivity.class));
            }
        });

        Button restartButton = (Button) findViewById(R.id.restart_bt);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Integer, Void, String>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        TextView progressTv = (TextView)findViewById(R.id.main_progress_tv);
                        progressTv.setText(
                                "Restarting SnoozeBud...",
                                TextView.BufferType.NORMAL);
                        findViewById(R.id.main_progress_ll).setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected String doInBackground(Integer... params) {
                        try {
                            SnoozebudSsh.restartSystem(SnoozebudSsh.setupSession(MainActivity.this));
                        } catch (Exception e) {
                            return e.getMessage();
                        }
                        return "done";
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        findViewById(R.id.main_progress_ll).setVisibility(View.GONE);
                        if (s.equals("done")) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "SnoozeBud is now restarting",
                                    Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Error: " + s,
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }.execute(1);
            }
        });

        Button quitButton = (Button)findViewById(R.id.quit_bt);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this, SnoozebudService.class));
                MainActivity.this.finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.advanced_options:
                startActivity(new Intent(MainActivity.this, OptionsActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
