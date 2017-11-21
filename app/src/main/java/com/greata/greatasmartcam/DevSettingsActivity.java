package com.greata.greatasmartcam;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
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
        devName = (EditText) findViewById(R.id.dev_name);
        if (position != -1) {
            myData = mDatas.get(position);
            devName.setText((String) myData.get("name"));
        }
        actionBar.setTitle("調整 - " + myData.get("model"));

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
            mDatas.set(position, myData);
            mListDataSave.setDataList(ListDataSave.DEVICE_TAG, mDatas);
        }
    }
}

