package com.greata.greatasmartcam;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;

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
import android.widget.AdapterView;
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


//git test

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String DEVICE_TAG = "device_tag";
    private List<Map<String, Object>> mDatas;
    private Map<String, Object> mMap;
    SimpleAdapter mAdapter;
    ListView playList;
    LinearLayout myToolB;
    SwipeRefreshLayout mRefreshLayout;

    TextView noItemText;
    ImageView playImg;
    ListDataSave mListDataSave;

    private static final String TAG = "ASYNC_TASK";

    MyTask mTask;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noItemText = (TextView) findViewById(R.id.noItemText);
        noItemText.setVisibility(View.INVISIBLE);
        playImg = (ImageView) findViewById(R.id.play_img);
        mListDataSave = new ListDataSave(this,"devices");

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
        mAdapter.setViewBinder(new SimpleAdapter.ViewBinder(){
            public boolean setViewValue(View view, Object data,    
                                String textRepresentation) {    
        //判断是否为我们要处理的对象    
        if(view instanceof ImageView ){
            Log.d(TAG, "setViewValue: data"+Double.valueOf(textRepresentation).intValue());

            ImageView iv = (ImageView) view;
            Drawable mDrawable = getApplicationContext().getResources().getDrawable(Double.valueOf(textRepresentation).intValue());
            iv.setImageDrawable(mDrawable);

            return true;    
        }else    
        return false;    
    }    
        });
        myToolB = (LinearLayout) findViewById(R.id.home_toolbar);
        myToolB.setVisibility(View.INVISIBLE);

        playList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(HomeActivity.this,"click "+i,Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeActivity.this,"selected?"+playList.getSelectedItemPosition(),Toast.LENGTH_SHORT).show();
                Toast.makeText(HomeActivity.this,"checked?"+playList.getCheckedItemPosition(),Toast.LENGTH_SHORT).show();

            }
        });

        newMTask();

        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.layout_swipe_refresh);
        mRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                newMTask();
            }
        });


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
        Log.d(TAG, "itemsCheck: "+mDatas.toString());
    }

    private void showPlay() {
            Intent mIntent = new Intent(HomeActivity.this, PlayerActivity.class);
            mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
            mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
            mIntent.setAction(PlayerActivity.ACTION_VIEW);
            startActivity(mIntent);
    }



    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(this, "network:" + NetWorkUtils.isNetworkConnected(this) + NetWorkUtils.getConnectedType(this), Toast.LENGTH_SHORT).show();
    }

    MenuItem mProgressMenu;

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
            intent = new Intent(HomeActivity.this, AddDeviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            addItem(android.R.color.black,"mathi");
            itemsCheck();
        } else if (id == R.id.nav_manage) {
            mDatas.clear();
            itemsCheck();
        } else if (id == R.id.nav_share) {
            intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            intent = new Intent(HomeActivity.this, MoveInspectionService.class);
            startService(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addItem(int imgID,String name) {
        mMap = new HashMap<String, Object>();
        mMap.put("screenshot", imgID);
        Log.d(TAG, "addItem: imgid"+imgID);
        mMap.put("name", name);
        mDatas.add(mMap);
        mListDataSave.setDataList(DEVICE_TAG,mDatas);
    }

    public void videoimgOnClick(View view) {
        Toast.makeText(HomeActivity.this, "toast video", Toast.LENGTH_SHORT).show();
        // TODO: 2017/10/13 use listener and hide buttons bar
    }

}
