package com.greata.greatasmartcam;

import android.app.Application;


import android.app.Application;

import com.google.android.exoplayer2.upstream.DataSource;

import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import com.google.android.exoplayer2.upstream.HttpDataSource;

import com.google.android.exoplayer2.util.Util;

/**
 * Created by xy on 2017/10/17.
 */

public class DemoApplication extends Application {


    protected String userAgent;


    @Override

    public void onCreate() {

        super.onCreate();

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");

    }


    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {

        return new DefaultDataSourceFactory(this, bandwidthMeter,

                buildHttpDataSourceFactory(bandwidthMeter));

    }


    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {

        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);

    }


    public boolean useExtensionRenderers() {

        return BuildConfig.FLAVOR.equals("withExtensions");

    }


}