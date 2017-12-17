package com.sleepsense.snoozebud;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by shayne on 2017-12-03.
 */

public class SnoozebudSsh {
    private static final String SSH_CHANNEL_TYPE = "exec";

    public static String executeSshCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel(SSH_CHANNEL_TYPE);
        channel.setCommand(command);
        channel.connect();

        InputStream input = channel.getInputStream();
        int data = input.read();

        StringBuilder outputBuffer = new StringBuilder();
        while (data != -1) {
            outputBuffer.append((char) data);
            data = input.read();
        }

        channel.disconnect();
        Log.d("SSH", outputBuffer.toString());
        return outputBuffer.toString();
    }

    public static Session setupSession(Context context) throws Exception {
        JSch sshChannel = new JSch();

        // TODO: Add values to Keys class
        Session session = sshChannel.getSession("pi", SnoozebudPrefs.getPref("IP_ADDRESS", context), 22);
        session.setPassword("snoozebud");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        Log.d("SSH", "Session is connected");

        return session;
    }

    public static String restartSystem(Session session) throws Exception {
        return executeSshCommand(session, "sudo shutdown -r now");
    }

    public static String setPiConfig(Context context) throws Exception {
        Session session = SnoozebudSsh.setupSession(context);

        SnoozebudSsh.executeSshCommand(session,
                "sudo sh -c 'echo {\\\"sensitivity\\\": "
                        + SnoozebudPrefs.getPref("SENSITIVITY", context)
                        + ",  > /home/pi/snoozebud-rpi/config.json'");
        SnoozebudSsh.executeSshCommand(session,
                "sudo sh -c 'echo \\\"firebase_id\\\": \\\""
                        + FirebaseInstanceId.getInstance().getToken()
                        + "\\\"} >> /home/pi/snoozebud-rpi/config.json'");

        return "done";
    }
}
