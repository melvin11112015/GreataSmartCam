package com.greata.greatasmartcam;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.AlertDialog;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String DEVICE_TAG = "device_tag";
    private static final String TAG = "ASYNC_TASK";
    private static int checkedPos;
    SimpleAdapter mAdapter;
    ListView playList;
    LinearLayout myToolB;
    SwipeRefreshLayout mRefreshLayout;
    Button noItemText;
    ImageView playImg;
    ListDataSave mListDataSave;
    MyTask mTask;
    MenuItem mProgressMenu;
    private List<Map<String, Object>> mDatas;
    private Map<String, Object> mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BroadcastUtils.sendFinishActivityBroadcast(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noItemText = (Button) findViewById(R.id.noItemText);
        noItemText.setVisibility(View.INVISIBLE);
        playImg = (ImageView) findViewById(R.id.play_img);
        mListDataSave = new ListDataSave(this, "devices");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        playList = (ListView) findViewById(R.id.play_list);

        mDatas = new ArrayList<Map<String, Object>>();
        mAdapter = new SimpleAdapter(this, mDatas, R.layout.item_home, new String[]{"screenshot", "name"}, new int[]{R.id.video_img, R.id.id_num});

        playList.setAdapter(mAdapter);
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                //判断是否为我们要处理的对象
                if (view instanceof ImageView) {
                    Log.d(TAG, "setViewValue: data" + Double.valueOf(textRepresentation).intValue());

                    ImageView iv = (ImageView) view;
                    Drawable mDrawable = getApplicationContext().getResources().getDrawable(Double.valueOf(textRepresentation).intValue());
                    iv.setImageDrawable(mDrawable);

                    return true;
                } else
                    return false;
            }
        });
        myToolB = (LinearLayout) findViewById(R.id.home_toolbar);
        myToolB.setVisibility(View.INVISIBLE);
        checkedPos = playList.getCheckedItemPosition();
        playList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                                if (i == checkedPos) {
                                                    hideToolbar(i);
                                                } else {
                                                    if (checkedPos < 0) {
                                                        TranslateAnimation anim = new TranslateAnimation(0, 0, myToolB.getHeight(), 0f);
                                                        anim.setDuration(500);
                                                        myToolB.startAnimation(anim);
                                                    }
                                                    myToolB.setVisibility(View.VISIBLE);
                                                    checkedPos = i;
                                                    playList.setSelector(R.drawable.selector2);

                                                }

                                            }
                                        }

        )
        ;

        newMTask();

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                newMTask();
            }
        });


    }

    private void hideToolbar(int i) {
        HomeActivity.this.playList.setItemChecked(i, false);
        checkedPos = -1;
        HomeActivity.this.playList.setSelector(R.drawable.selector);
        TranslateAnimation anim = new TranslateAnimation(0.0f, 0.0f, 0f, myToolB.getHeight());
        anim.setDuration(500);
        HomeActivity.this.myToolB.startAnimation(anim);
        HomeActivity.this.myToolB.setVisibility(View.INVISIBLE);
    }

    public void delBtnOnClick(View v) {
        if (checkedPos < 0) return;
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder delDialog =
                new AlertDialog.Builder(HomeActivity.this);
        delDialog.setMessage("請確認移除" + mDatas.get(checkedPos).get("name") + "?");
        delDialog.setPositiveButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatas.remove(checkedPos);
                mListDataSave.setDataList(DEVICE_TAG, mDatas);
                mAdapter.notifyDataSetChanged();
                hideToolbar(checkedPos);

            }
        });
        delDialog.setNegativeButton("取消", null);
        // 显示
        delDialog.show();

    }

    public void googleReq(View v) {
        Toast.makeText(this, "錯誤:沒有檢測到 Google Play 服務 ", Toast.LENGTH_SHORT).show();
    }

    private void newMTask() {
        mTask = new MyTask();
        mTask.execute();
    }

    private void itemsCheck() {
        mAdapter.notifyDataSetChanged();
        if (mDatas.isEmpty()) {
            noItemText.setVisibility(View.VISIBLE);
        } else {
            noItemText.setVisibility(View.GONE);
        }
        Log.d(TAG, "itemsCheck: " + mDatas.toString());
    }

    private void showPlay() {
        if (NetWorkUtils.isNetworkConnected(this)) {
            Intent mIntent = new Intent(HomeActivity.this, PlayerActivity.class);
            mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
            mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
            mIntent.putExtra("title", "this is title");
            mIntent.setAction(PlayerActivity.ACTION_VIEW);
            startActivity(mIntent);
        } else {
            Toast.makeText(this, "無法連結網路，請檢查網路設定", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mProgressMenu = menu.findItem(R.id.refresh);
        return true;
    }

    public void setLoadingState(boolean refreshing) {
        if (mProgressMenu != null) {
            if (refreshing) {
                mProgressMenu
                        .setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                mProgressMenu.setActionView(null);
                mProgressMenu.setIcon(android.R.drawable.ic_popup_sync);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                newMTask();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
        return false;//返回false不调用系统菜单键事件
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (playList.getCheckedItemPosition() >= 0) {
            hideToolbar(playList.getCheckedItemPosition());
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent intent;
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showPlay();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            addDevice();
        } else if (id == R.id.nav_slideshow) {
            addItem(android.R.color.black, "mathi", true);
            itemsCheck();
        } else if (id == R.id.nav_manage) {
            intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            intent = new Intent(HomeActivity.this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            final AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
            alertDialog.show();
            Window window = alertDialog.getWindow();
            window.setContentView(R.layout.info_dialog);
            Button dialogButton = window.findViewById(R.id.mydialog_ok);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.cancel();
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noItemText:
                addDevice();
                break;
        }
    }

    private void addDevice() {
        Intent intent;
        intent = new Intent(HomeActivity.this, AddDeviceActivity.class);
        startActivity(intent);
    }

    private void addItem(int imgID, String name, boolean state) {
        mMap = new HashMap<String, Object>();
        mMap.put("screenshot", imgID);
        Log.d(TAG, "addItem: imgid" + imgID);
        mMap.put("name", name);
        mMap.put("state", state);
        mDatas.add(mMap);
        mListDataSave.setDataList(DEVICE_TAG, mDatas);
    }

    public void videoimgOnClick(View view) {
        Toast.makeText(HomeActivity.this, "toast video", Toast.LENGTH_SHORT).show();
        // TODO: 2017/10/13 use listener and hide buttons bar
    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute() called");
            setLoadingState(true);
            // TODO: 2017/10/19 change playImg while refreshing
            //playImg.setImageDrawable();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground(Params... params) called");
            try {
                Thread.sleep(1000);
                List<Map<String, Object>> tempList = mListDataSave.getDataList(DEVICE_TAG);
                mDatas.clear();
                mDatas.addAll(tempList);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
            return null;
        }

        //onProgressUpdate方法用于更新进度信息
        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.i(TAG, "onProgressUpdate(Progress... progresses) called");
        }

        //onPostExecute方法用于在执行完后台任务后更新UI,显示结果
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "onPostExecute(Result result) called");
            setLoadingState(false);
            //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
            itemsCheck();
            mRefreshLayout.setRefreshing(false);
        }

        //onCancelled方法用于在取消执行中的任务时更改UI
        @Override
        protected void onCancelled() {
            Log.i(TAG, "onCancelled() called");
            setLoadingState(false);
            //数据重新加载完成后，提示数据发生改变，并且设置现在不在刷新
            itemsCheck();
            mRefreshLayout.setRefreshing(false);
        }
    }

}
