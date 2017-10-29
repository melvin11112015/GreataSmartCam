package com.greata.greatasmartcam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class SplashActivity extends AppCompatActivity {

    private FinishActivityReceiver mRecevier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        registerFinishReciver();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, 1500, pendingIntent);
    }

    private void registerFinishReciver() {
        mRecevier = new FinishActivityReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.RECEIVER_ACTION_FINISH);
        registerReceiver(mRecevier, intentFilter);
    }

    @Override
    protected void onDestroy() {
        if (mRecevier != null) {
            unregisterReceiver(mRecevier);
        }
        super.onDestroy();
    }

    private class FinishActivityReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //根据需求添加自己需要关闭页面的action
            if (BroadcastUtils.RECEIVER_ACTION_FINISH.equals(intent.getAction())) {
                SplashActivity.this.finish();
            }
        }
    }
}
