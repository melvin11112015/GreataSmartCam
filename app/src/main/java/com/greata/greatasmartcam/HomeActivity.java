package com.greata.greatasmartcam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


//git test

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<String> mDatas;
    HomeAdapter mAdapter;

    SwipeRefreshLayout mRefreshLayout;
    SharedPreferences sPre;

    TextView noItemText;
    ImageView playImg;

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
                sPre = getSharedPreferences("devices", Context.MODE_PRIVATE);
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
        playImg = (ImageView) findViewById(R.id.play_img);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDatas = new ArrayList<String>();
        newMTask();
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // 设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 设置adapter

        mRecyclerView.setAdapter(mAdapter = new HomeAdapter());
        // 设置Item添加和移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置Item之间间隔样式
        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
    }

    private void showPlay() {

        if (!NetWorkUtils.isWifiConnected(this)) {
            showNormalDialog();
        } else {
            Intent mIntent = new Intent(HomeActivity.this, PlayerActivity.class);
            mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
            mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
            mIntent.setAction(PlayerActivity.ACTION_VIEW);
            startActivity(mIntent);
        }
    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(HomeActivity.this);
        normalDialog.setMessage("你的网络不是wifi，是否继续");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        Intent mIntent = new Intent(HomeActivity.this, PlayerActivity.class);
                        mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
                        mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
                        mIntent.setAction(PlayerActivity.ACTION_VIEW);
                        startActivity(mIntent);
                    }
                });
        normalDialog.setNegativeButton("关闭", null);
        // 显示
        normalDialog.show();
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

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(
                    HomeActivity.this).inflate(R.layout.item_home, parent,
                    false);
            MyViewHolder holder = new MyViewHolder(view);

            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.tv.setText(mDatas.get(position));
        }

        @Override
        public int getItemCount() {
            return mDatas.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView tv;
            ImageView iv, playiv;

            public MyViewHolder(View view) {
                super(view);
                tv = view.findViewById(R.id.id_num);
                iv = view.findViewById(R.id.video_img);
                playiv = view.findViewById(R.id.play_img);
            }
        }

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            showPlay();
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(HomeActivity.this, AddDeviceActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            mDatas.add("itemx");
            itemsCheck();
        } else if (id == R.id.nav_manage) {
            mDatas.clear();
            itemsCheck();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void videoimgOnClick(View view) {
        Toast.makeText(HomeActivity.this, "toast video", Toast.LENGTH_SHORT).show();
        // TODO: 2017/10/13 use listener and hide buttons bar
    }

    class MyDividerItemDecoration extends RecyclerView.ItemDecoration {

        private final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };
        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
        /**
         * 用于绘制间隔样式
         */
        private Drawable mDivider;
        /**
         * 列表的方向，水平/竖直
         */
        private int mOrientation;


        public MyDividerItemDecoration(Context context, int orientation) {
            // 获取默认主题的属性
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            // 绘制间隔
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }

        private void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        /**
         * 绘制间隔
         */
        private void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin +
                        Math.round(ViewCompat.getTranslationY(child));
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        /**
         * 绘制间隔
         */
        private void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();
            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin +
                        Math.round(ViewCompat.getTranslationX(child));
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

}
