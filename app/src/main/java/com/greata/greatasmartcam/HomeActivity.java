package com.greata.greatasmartcam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "ASYNC_TASK";
    private static int checkedPos;
    //SimpleAdapter mAdapter;
    ListView playList;
    GridLayout myToolB;
    SwipeRefreshLayout mRefreshLayout;
    Button noItemText;
    ImageView playImg;
    ListDataSave mListDataSave;
    MyTask mTask;
    MenuItem mProgressMenu;
    private List<Map<String, Object>> mDatas;
    private Map<String, Object> mMap;

    private MyAdapter mAdapter;

    static Bitmap drawableToBitmap(Drawable drawable)  // drawable  转换成  bitmap
    {
        int width = drawable.getIntrinsicWidth();      //  取  drawable  的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;                  //  取  drawable  的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);          //  建立对应  bitmap
        Canvas canvas = new Canvas(bitmap);                  //  建立对应  bitmap  的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);            //  把  drawable  内容画到画布中
        return bitmap;
    }

    static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);  // drawable  转换成  bitmap
        Matrix matrix = new Matrix();      //  创建操作图片用的  Matrix  对象
        float scaleWidth = ((float) w / width);      //  计算缩放比例
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);                  //  设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);              //  建立新的  bitmap  ，其内容是对原  bitmap  的缩放后的图
        return new BitmapDrawable(newbmp);              //  把  bitmap  转换成  drawable  并返回
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BroadcastUtils.sendFinishActivityBroadcast(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable logoDraw = zoomDrawable(getResources().getDrawable(R.drawable.logo), 130, 130);
        if (Build.VERSION.SDK_INT >= 21) {
            logoDraw.setTint(Color.WHITE);
        }
        toolbar.setLogo(logoDraw);

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

        mAdapter = new MyAdapter(this, mDatas);
        playList.setAdapter(mAdapter);

        myToolB = (GridLayout) findViewById(R.id.home_toolbar);
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

        );



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

    public void playBtnOnClick(View v) {
        showPlay(checkedPos);
    }
    public void recBtnOnClick(View v) {
        if (NetWorkUtils.isNetworkConnected(this)) {
            if (checkedPos < 0) return;
            Intent intent = new Intent();
            intent.putExtra("state", (boolean) mDatas.get(checkedPos).get("state"));
            intent.putExtra("name", (String) mDatas.get(checkedPos).get("name"));
            intent.setClass(HomeActivity.this, RecordActivity.class);
            startActivity(intent);
        } else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    Toast.makeText(HomeActivity.this, R.string.no_network, Toast.LENGTH_SHORT).show();
                }
            }, 800);
        }
    }

    public void devBtnOnClick(View v) {
        if (checkedPos < 0) return;
        Intent devIntent = new Intent(HomeActivity.this, DevSettingsActivity.class);
        devIntent.putExtra("pos", checkedPos);
        startActivityForResult(devIntent, 999);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999) {
            newMTask();
        }
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
                mListDataSave.setDataList(ListDataSave.DEVICE_TAG, mDatas);
                mAdapter.notifyDataSetChanged();
                hideToolbar(checkedPos);

            }
        });
        delDialog.setNegativeButton("取消", null);
        // 显示
        delDialog.show();

    }

    public void googleReq(View v) {
        Toast.makeText(this, R.string.no_google, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
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

    private void showPlay(int pos) {
        if (NetWorkUtils.isNetworkConnected(this) && (boolean) mDatas.get(pos).get("state")) {
            Intent mIntent = new Intent(HomeActivity.this, PlayerActivity.class);
            mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
            //mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
            mIntent.setData(Uri.parse(Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID.mp4"));
            mIntent.putExtra("title", (String) mDatas.get(pos).get("name"));
            mIntent.setAction(PlayerActivity.ACTION_VIEW);
            startActivity(mIntent);
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        newMTask();
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
        mAdapter.setLoading(refreshing);
        mAdapter.notifyDataSetChanged();
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

        if (id == R.id.nav_gallery) {
            addDevice();
        } else if (id == R.id.nav_slideshow) {
            addItem(android.R.color.black, "我的攝影機2 ", true, "鴻優視 2S 720P", false);
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

    private void addItem(int imgID, String name, boolean state, String model, boolean hd) {
        mMap = new HashMap<String, Object>();
        mMap.put("screenshot", imgID);
        Log.d(TAG, "addItem: imgid" + imgID);
        mMap.put("name", name);
        mMap.put("state", state);
        mMap.put("model", model);
        mMap.put("isHd", hd);
        mDatas.add(mMap);
        mListDataSave.setDataList(ListDataSave.DEVICE_TAG, mDatas);
    }

    static class ViewHolder {
        TextView idTextView;
        ImageView videoImage;
        ImageView playImage;
        TextView stateTextView;
        TextView modelTextView;
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private List<Map<String, Object>> dataList;
        private boolean isLoading;

        public MyAdapter(Context context, List dataList) {
            super();
            this.context = context;
            this.dataList = dataList;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            ViewHolder holder = null;
            if (convertView == null) {
                //View view = inflater.inflate(R.layout.item_home, null);
                convertView = inflater.inflate(R.layout.item_home, null);
                holder = new ViewHolder();
                //final TextView idTextView = (TextView) view.findViewById(R.id.id_num);
                holder.idTextView = convertView.findViewById(R.id.id_num);
                holder.videoImage = convertView.findViewById(R.id.video_img);
                holder.playImage = convertView.findViewById(R.id.play_img);
                holder.stateTextView = convertView.findViewById(R.id.state_text);
                holder.modelTextView = convertView.findViewById(R.id.model_code);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Map<String, Object> data = dataList.get(position);
            if (data != null) {
                holder.videoImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPlay(position);
                    }
                });
                holder.idTextView.setText((String) data.get("name"));
                holder.modelTextView.setText((String) data.get("model"));
                if (NetWorkUtils.isNetworkConnected(HomeActivity.this) && (boolean) data.get("state")) {
                    holder.videoImage.setImageResource(R.drawable.vid_pic);
                    holder.playImage.setImageResource(android.R.drawable.ic_media_play);
                    holder.stateTextView.setText("線上");
                    holder.stateTextView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    holder.videoImage.setImageResource(android.R.color.black);
                    holder.playImage.setImageResource(R.drawable.power_shutdown);
                    holder.stateTextView.setText("離線");
                    holder.stateTextView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
                }
                if (this.isLoading) {
                    holder.playImage.setVisibility(View.INVISIBLE);
                    holder.stateTextView.setVisibility(View.INVISIBLE);
                } else {
                    holder.playImage.setVisibility(View.VISIBLE);
                    holder.stateTextView.setVisibility(View.VISIBLE);
                }
            }
            return convertView;
        }

    }

    private class MyTask extends AsyncTask<String, Integer, String> {
        //onPreExecute方法用于在执行后台任务前做一些UI操作
        @Override
        protected void onPreExecute() {
            Log.i(TAG, "onPreExecute() called");
            setLoadingState(true);
            //playImg.setImageDrawable();
        }

        //doInBackground方法内部执行后台任务,不可在此方法内修改UI
        @Override
        protected String doInBackground(String... params) {
            Log.i(TAG, "doInBackground(Params... params) called");
            try {
                Thread.sleep(1000);
                List<Map<String, Object>> tempList = mListDataSave.getDataList(ListDataSave.DEVICE_TAG);
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
