package com.greata.greatasmartcam;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private static boolean deviceState;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public void showPlay2(View v) {
        if (NetWorkUtils.isNetworkConnected(this)) {
            Intent mIntent = new Intent(this, PlayerActivity.class);
            mIntent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS, false);
            //mIntent.setData(Uri.parse("http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8"));
            mIntent.setData(Uri.parse(Environment.getExternalStorageDirectory() + "/DCIM/Camera/VID.mp4"));
            mIntent.putExtra("title", getIntent().getStringExtra("name"));
            mIntent.setAction(PlayerActivity.ACTION_VIEW);
            startActivity(mIntent);
        } else {
            Toast.makeText(this, R.string.no_network, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        deviceState = getIntent().getBooleanExtra("state", false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("錄影記錄 - " + getIntent().getStringExtra("name"));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Toast.makeText(this, R.string.no_google, Toast.LENGTH_SHORT).show();
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putBoolean("state", RecordActivity.deviceState);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_record, container, false);
            LinearLayout content = rootView.findViewById(R.id.record_content);
            TextView stateTextView = rootView.findViewById(R.id.section_label);
            Button loginButton = rootView.findViewById(R.id.login_btn);
            ImageView imgCloud = rootView.findViewById(R.id.imageCloud);
            ListView recList = rootView.findViewById(R.id.rec_list);
            TextView storageText = rootView.findViewById(R.id.storage_text);
            List<Integer> recData = new ArrayList<Integer>();
            recData.add(0);
            recData.add(1);
            recData.add(2);
            recData.add(3);
            recList.setAdapter(new MyRecAdapter(getContext(), recData));
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 0) {
                loginButton.setVisibility(View.INVISIBLE);
                imgCloud.setVisibility(View.INVISIBLE);
                if (getArguments().getBoolean("state")) {
                    content.setVisibility(View.VISIBLE);
                    stateTextView.setVisibility(View.INVISIBLE);
                    storageText.setText("攝影機總存儲空間:7.47GB 已使用存儲空間:5.32GB");
                } else {
                    content.setVisibility(View.INVISIBLE);
                    stateTextView.setVisibility(View.VISIBLE);
                    stateTextView.setText(R.string.no_connection);
                }
            }

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {
                content.setVisibility(View.INVISIBLE);
                imgCloud.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                stateTextView.setText(R.string.greata_cloud);
                stateTextView.setVisibility(View.VISIBLE);
            }


            // new Date()为获取当前系统时间

            return rootView;
        }

        static class ViewHolder {
            TextView dateTextView;
            ImageView recImage, recIcon;
            TextView recTextView;

            void hideItem() {

                recImage.setVisibility(View.GONE);
                recIcon.setVisibility(View.GONE);
                recTextView.setVisibility(View.GONE);
                dateTextView.setVisibility(View.VISIBLE);


            }

            void showItem() {

                recImage.setVisibility(View.VISIBLE);
                recIcon.setVisibility(View.VISIBLE);
                recTextView.setVisibility(View.VISIBLE);
                dateTextView.setVisibility(View.GONE);


            }
        }

        class MyRecAdapter extends BaseAdapter {

            SimpleDateFormat format = new SimpleDateFormat("HH");
            Calendar c = Calendar.getInstance();
            private Context context;
            private List<Integer> dataList;
            private boolean isLoading;
            private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");//设置日期格式


            public MyRecAdapter(Context context, List dataList) {
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
            public View getView(int position, View convertView, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                int cint = getResources().getColor(android.R.color.black);
                ViewHolder holder = null;
                if (convertView == null) {
                    //View view = inflater.inflate(R.layout.item_home, null);
                    convertView = inflater.inflate(R.layout.item_rec, null);
                    holder = new ViewHolder();
                    //final TextView idTextView = (TextView) view.findViewById(R.id.id_num);
                    holder.dateTextView = convertView.findViewById(R.id.date_textview);
                    holder.recImage = convertView.findViewById(R.id.rec_img);
                    holder.recTextView = convertView.findViewById(R.id.rec_name);
                    holder.recIcon = convertView.findViewById(R.id.rec_icon);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (dataList != null && holder != null) {
                    switch (position) {
                        case 0:
                            holder.hideItem();
                            holder.dateTextView.setText("—" + df.format(new Date()) + "—");
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                            c.add(Calendar.HOUR, -1);
                            String start = format.format(c.getTime()) + ":00";
                            holder.showItem();
                            holder.recImage.setBackgroundColor(cint);
                            holder.recImage.setImageResource(R.drawable.vid_pic);
                            holder.recTextView.setText(start);
                            break;
                        default:
                            break;
                    }
                }

                return convertView;
            }

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "攝影機存儲";
                case 1:
                    return "雲端存儲";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
