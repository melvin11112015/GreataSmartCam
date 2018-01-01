package com.greata.greatasmartcam;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class DevSettingsActivity extends AppCompatActivity {
    EditText devName;
    private int position;
    private ListDataSave mListDataSave;
    private List<Map<String, Object>> mDatas;
    private Map<String, Object> myData;
    private ActionBar actionBar;
    private RadioButton radioButtonSD, radioButtonHD;
    private Switch autoDeleteSwitch, mSwitch1, mSwitch2, mSwitch3;
    private EditText deleteTime;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.dev_layout);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        position = getIntent().getIntExtra("pos", -1);
        mListDataSave = new ListDataSave(this, "devices");
        mDatas = mListDataSave.getDataList(ListDataSave.DEVICE_TAG);
        TextView nameTv = (TextView) findViewById(R.id.dev_name_tv);
        Drawable dwLeft = getResources().getDrawable(R.drawable.web_cam2_128px);
        dwLeft.setBounds(0, 0, 40, 40);
        Drawable dwLeftC = DrawableCompat.wrap(dwLeft);
        DrawableCompat.setTint(dwLeftC, getResources().getColor(R.color.colorPrimary));
        nameTv.setCompoundDrawables(dwLeftC, null, null, null);
        radioButtonHD = (RadioButton) findViewById(R.id.radioButtonHD);
        radioButtonSD = (RadioButton) findViewById(R.id.radioButtonSD);
        autoDeleteSwitch = (Switch) findViewById(R.id.switch22);
        mSwitch1 = (Switch) findViewById(R.id.switch1);
        mSwitch2 = (Switch) findViewById(R.id.switch2);
        mSwitch3 = (Switch) findViewById(R.id.switch3);
        devName = (EditText) findViewById(R.id.dev_name);
        deleteTime = (EditText) findViewById(R.id.dev_delete_time);
        if (position != -1) {
            myData = mDatas.get(position);
            devName.setText((String) myData.get("name"));
            actionBar.setTitle("調整 - " + myData.get("model"));
            if (myData.get("isHd") == null) {
                myData.put("isHd", false);
            }
            if (myData.get("autoDelete") == null) {
                myData.put("autoDelete", false);
            }
            if (myData.get("autoDelete_time") == null) {
                myData.put("autoDelete_time", "24");
            }
            if (myData.get("switch1") == null) {
                myData.put("switch1", false);
            }
            mSwitch1.setChecked((boolean) myData.get("switch1"));
            if (myData.get("switch2") == null) {
                myData.put("switch2", false);
            }
            mSwitch2.setChecked((boolean) myData.get("switch2"));
            if (myData.get("switch3") == null) {
                myData.put("switch3", false);
            }
            mSwitch3.setChecked((boolean) myData.get("switch3"));
            if ((boolean) myData.get("isHd")) {
                radioButtonSD.setChecked(false);
                radioButtonHD.setChecked(true);
            } else {
                radioButtonSD.setChecked(true);
                radioButtonHD.setChecked(false);
            }
            deleteTime.setText((String) myData.get("autoDelete_time"));
            autoDeleteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        deleteTime.setTextColor(DevSettingsActivity.this.getResources().getColor(R.color.colorAccent));
                        deleteTime.setEnabled(true);
                    } else {
                        deleteTime.setTextColor(DevSettingsActivity.this.getResources().getColor(android.R.color.darker_gray));
                        deleteTime.setEnabled(false);
                    }
                }
            });
            autoDeleteSwitch.setChecked((boolean) myData.get("autoDelete"));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (position != -1) {
            myData.put("name", devName.getText().toString());
            myData.put("isHd", radioButtonHD.isChecked());
            myData.put("autoDelete", autoDeleteSwitch.isChecked());
            myData.put("switch1", mSwitch1.isChecked());
            myData.put("switch2", mSwitch2.isChecked());
            myData.put("switch3", mSwitch3.isChecked());
            myData.put("autoDelete_time", deleteTime.getText().toString());
            mDatas.set(position, myData);
            mListDataSave.setDataList(ListDataSave.DEVICE_TAG, mDatas);
        }
    }
}

