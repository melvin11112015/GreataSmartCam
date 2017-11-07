package com.greata.greatasmartcam;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AddDeviceActivity extends AppCompatActivity {

    public static final String WIFI = "wifi";
    List<String> list;
    ArrayAdapter<String> adapter;
    private AddFragment f0, f2, f3;
    private FragmentManager fManager;
    private ImageView lightView;
    private ActionBar actionBar;
    private EditText editSSID, editPwd;
    private List<String> ssidList;
    private Spinner sp;
    private ListPopupWindow listPopupWindow;
    private CheckBox pwdCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("根據提示新增攝像機 ");
        f0 = AddFragment.newInstance(R.layout.fragment_a);
        f2 = AddFragment.newInstance(R.layout.fragment_two);
        f3 = AddFragment.newInstance(R.layout.fragment_three);
        fManager = getFragmentManager();
        fManager.beginTransaction().add(R.id.frameFragment, f0).add(R.id.frameFragment, f2).add(R.id.frameFragment, f3).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lightView == null) {
            lightView = f0.getView().findViewById(R.id.light_view);
            AnimationDrawable animationDrawable = (AnimationDrawable) lightView.getDrawable();
            animationDrawable.start();
        }
        if (sp == null || editSSID == null) {
            sp = f2.getView().findViewById(R.id.spinner);
            editSSID = f2.getView().findViewById(R.id.edit_ssid);
            (new WifiTask()).execute();
        }

        if (editPwd == null) {
            editPwd = f2.getView().findViewById(R.id.edit_pwd);
        }
        if (pwdCheckBox == null) {
            pwdCheckBox = f2.getView().findViewById(R.id.show_pwd_checkbox);
            pwdCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //如果选中，显示密码
                        editPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    } else {
                        //否则隐藏密码
                        editPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }

                }
            });
        }
        fManager.beginTransaction().hide(f2).hide(f3).commit();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_next_0:
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).hide(f0).show(f2).addToBackStack(null).commit();
                break;
            case R.id.button_next_2:
                if (editSSID.getText().toString().equals("")) {
                    Toast toast = Toast.makeText(AddDeviceActivity.this, "請輸入" + getResources().getString(R.string.ssid_connect_text), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                fManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).hide(f2).show(f3).addToBackStack(null).commit();
                break;
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

    private class WifiTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i(WIFI, "onPreExecute() called");

        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            Log.i(WIFI, "doInBackground(Params... params) called");
            try {
                list = new ArrayList<String>();

                list.add("利优视Wifi");
                list.add("利优视Wifi HD版");
                list.add("利优云监控");
                list.add("利优云监控 S");
                list.add("利优云监控 2S");
                adapter = new ArrayAdapter<String>(AddDeviceActivity.this, android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
                ssidList = new ArrayList<String>();
                List<WifiConfiguration> wifiConfigurationList = wifiManager.getConfiguredNetworks();
                if (wifiConfigurationList != null) {


                for (int i = 0; i < wifiConfigurationList.size(); i++) {
                    Log.d(WIFI, "" + wifiConfigurationList.get(i).SSID);
                    ssidList.add(wifiConfigurationList.get(i).SSID.replaceAll("\"", ""));
                }
                    Log.d(WIFI, "doInBackground: EDITSSID=NULL?");
                    Log.d(WIFI, "doInBackground: EDITSSID!=NULL");
                }
            } catch (Exception e) {
                Log.e(WIFI, e.getMessage());
            }
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i(WIFI, "onProgressUpdate(Progress... progresses) called");
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i(WIFI, "onPostExecute(Result result) called");

            sp.setAdapter(adapter);

            editSSID.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int DRAWABLE_RIGHT = 2;
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (event.getX() >= (editSSID.getWidth() - editSSID
                                .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            listPopupWindow = new ListPopupWindow(AddDeviceActivity.this);
                            listPopupWindow.setAdapter(new ArrayAdapter<String>(AddDeviceActivity.this, android.R.layout.simple_list_item_1, ssidList));
                            listPopupWindow.setAnchorView(editSSID);
                            listPopupWindow.setHeight(500);
                            listPopupWindow.setModal(true);
                            listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    editSSID.setText(ssidList.get(i));
                                    listPopupWindow.dismiss();
                                }
                            });
                            listPopupWindow.show();
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i(WIFI, "onCancelled() called");

        }
    }
}
