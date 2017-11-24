package com.sleepsense.snoozebud;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * Connects to Raspberry Pi and starts an SSH session.
 * Raspberry Pi should be connected to the phone by USB tethering
 * while this is running.
 */

public class SetupActivity extends AppCompatActivity {

    private static final String SSH_CHANNEL_TYPE = "exec";
    private static final int ASYNC_START = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

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
                super.onPostExecute(result);
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            }
        }.execute(ASYNC_START);
    }


    private String sshIntoPi() throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(Keys.PI_USERNAME, Keys.PI_IP, Keys.PI_PORT);
        session.setPassword(Keys.PI_PASSWORD);

        // Avoid asking for key confirmation
        Properties properties = new Properties();
        properties.put("StrictHostKeyChecking", "no");
        session.setConfig(properties);

        session.connect();

        // Open SSH channel
        ChannelExec channel = new ChannelExec();
        session.openChannel(SSH_CHANNEL_TYPE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        channel.setOutputStream(outputStream);

        // Execute SSH commands
        channel.setCommand("pwd"); // TODO: Get the actual command string to use
        channel.connect();
        channel.disconnect();

        return outputStream.toString();
    }
}
