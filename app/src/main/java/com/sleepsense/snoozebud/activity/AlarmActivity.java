package com.sleepsense.snoozebud.activity;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sleepsense.snoozebud.R;

public class AlarmActivity extends AppCompatActivity {

    Ringtone ringtone;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(
                AudioManager.STREAM_RING,
                audioManager.getStreamMaxVolume(AudioManager.STREAM_RING),
                0);

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            if (alert == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
        }

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
        ringtone.play();

        long pattern[] = {0, 100, 200, 300, 400};
        vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(pattern, 0);

//        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
//        PowerManager.WakeLock wakeLock =
//                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmActivity");
//        wakeLock.acquire();

        Button stopButton = (Button)findViewById(R.id.stop_alarm_bt);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ringtone.stop();
                vibrator.cancel();
//                wakeLock.release();
                AlarmActivity.this.finish();
            }
        });
    }
}
