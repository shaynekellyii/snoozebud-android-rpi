package com.sleepsense.snoozebud;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by shayne on 2017-12-03.
 */

public class SnoozebudSsh {
    private static final String SSH_CHANNEL_TYPE = "exec";

    public static String executeSshCommand(Session session, String command) throws Exception {
        ChannelExec channel = (ChannelExec)session.openChannel("exec");
        channel.setCommand(command);
        channel.connect();

        InputStream input = channel.getInputStream();
        int data = input.read();

        StringBuilder outputBuffer = new StringBuilder();
        while (data != -1) {
            outputBuffer.append((char)data);
            data = input.read();
        }

        channel.disconnect();
        Log.d("SSH", outputBuffer.toString());
        return outputBuffer.toString();
    }

    public static Session setupSession() throws Exception {
        JSch sshChannel = new JSch();

        // TODO: Add values to Keys class, set static IP on SnoozeBud
        Session session = sshChannel.getSession("pi", "142.58.167.2", 22);
        session.setPassword("snoozebud");
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        Log.d("SSH", "Session is connected");

        return session;
    }
}
