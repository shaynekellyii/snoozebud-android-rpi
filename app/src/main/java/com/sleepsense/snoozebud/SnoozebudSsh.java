package com.sleepsense.snoozebud;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

import java.io.InputStream;

/**
 * Created by shayne on 2017-12-03.
 */

public class SnoozebudSsh {

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
}
