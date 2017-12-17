package com.sleepsense.snoozebud.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sleepsense.snoozebud.R;
import com.sleepsense.snoozebud.SnoozebudPrefs;

/**
 * Created by shayne on 2017-12-16.
 */

public class OptionsActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        final EditText ipEt = (EditText)findViewById(R.id.ip_set_et);
        ipEt.setText(SnoozebudPrefs.getPref("IP_ADDRESS", this), TextView.BufferType.EDITABLE);

        Button confirmButton = (Button)findViewById(R.id.confirm_options_bt);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                String ip = ipEt.getText().toString();
                if (!ip.isEmpty()) {
                    SnoozebudPrefs.setPref("IP_ADDRESS", ip, OptionsActivity.this);
                    OptionsActivity.this.finish();
                } else {
                    Toast.makeText(
                            OptionsActivity.this,
                            "Please enter an IP address",
                            Toast.LENGTH_LONG)
                            .show();
                }
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
