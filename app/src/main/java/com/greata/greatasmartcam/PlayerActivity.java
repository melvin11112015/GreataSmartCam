package com.greata.greatasmartcam;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.drm.FrameworkMediaDrm;
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback;
import com.google.android.exoplayer2.drm.UnsupportedDrmException;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.mediacodec.MediaCodecRenderer.DecoderInitializationException;
import com.google.android.exoplayer2.mediacodec.MediaCodecUtil.DecoderQueryException;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.DebugTextViewHelper;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * An activity that plays media using {@link SimpleExoPlayer}.
 */

public class PlayerActivity extends Activity implements OnClickListener, EventListener,

        PlaybackControlView.VisibilityListener {

    public static final String DRM_SCHEME_UUID_EXTRA = "drm_scheme_uuid";

    public static final String DRM_LICENSE_URL = "drm_license_url";

    public static final String DRM_KEY_REQUEST_PROPERTIES = "drm_key_request_properties";

    public static final String PREFER_EXTENSION_DECODERS = "prefer_extension_decoders";


    public static final String ACTION_VIEW = "com.google.android.exoplayer.demo.action.VIEW";

    public static final String EXTENSION_EXTRA = "extension";


    public static final String ACTION_VIEW_LIST =

            "com.google.android.exoplayer.demo.action.VIEW_LIST";

    public static final String URI_LIST_EXTRA = "uri_list";

    public static final String EXTENSION_LIST_EXTRA = "extension_list";

    public static final String AD_TAG_URI_EXTRA = "ad_tag_uri";
    public static final int L_SIZE = 22;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {

        DEFAULT_COOKIE_MANAGER = new CookieManager();

        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);

    }


    private ProgressBar mProgressBar;

    private Handler mainHandler;

    private EventLogger eventLogger;

    private SimpleExoPlayerView simpleExoPlayerView;

    private LinearLayout debugRootView;

    private TextView debugTextView;

    private TextView playerTitle, recFrame;

    private Button retryButton;

    private DataSource.Factory mediaDataSourceFactory;

    private SimpleExoPlayer player;

    private DefaultTrackSelector trackSelector;

    private TrackSelectionHelper trackSelectionHelper;

    private DebugTextViewHelper debugViewHelper;

    private boolean inErrorState;

    private TrackGroupArray lastSeenTrackGroupArray;

    private TextClock mTextClock;

    private ImageButton screenShotBtn;


    private boolean shouldAutoPlay;

    private int resumeWindow;

    private long resumePosition;


    // Fields used only for ad playback. The ads loader is loaded via reflection.


    private Object imaAdsLoader; // com.google.android.exoplayer2.ext.ima.ImaAdsLoader

    private Uri loadedAdTagUri;

    private ViewGroup adOverlayViewGroup;

    private ToggleButton lockScreenButton, recVideoButton;

    private FrameLayout.LayoutParams pParams, lParams;

    private String htmlStr = "<font color='#000000'>y</font>yyy-<font color='#000000'>MM-dd</font> HH:mm:ss";
    private String htmlStr2 = "<font color='#000000'>y</font>yyy-<font color='#000000'>MM-dd</font> HH:";
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    // Activity lifecycle
    private NetReceiver mNetReceiver;

    private static boolean isBehindLiveWindow(ExoPlaybackException e) {

        if (e.type != ExoPlaybackException.TYPE_SOURCE) {

            return false;

        }

        Throwable cause = e.getSourceException();

        while (cause != null) {

            if (cause instanceof BehindLiveWindowException) {

                return true;

            }

            cause = cause.getCause();

        }

        return false;

    }

    public void saveBitmap() {
        Log.e("save", "保存图片");
        File f = new File(this.getExternalFilesDir(null), Long.toString(System.currentTimeMillis()) + ".png");
        if (f.exists()) {
            f.delete();
        }
        try {

            //Bitmap bm = simpleExoPlayerView.getBitmap();
            FileOutputStream out = new FileOutputStream(f);
            //bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i("save", "已经保存");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isLandscape() {
        /*   * 通过API动态改变当前屏幕的显示方向   */

        // 取得当前屏幕方向
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        return width > height;
    }

    @Override

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        shouldAutoPlay = false;

        clearResumePosition();

        mediaDataSourceFactory = buildDataSourceFactory(true);

        mainHandler = new Handler();

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {

            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);

        }


        setContentView(R.layout.player_activity);

        lockScreenButton = findViewById(R.id.lockscreen_btn);
        lockScreenButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
            }
        });

        playerTitle = findViewById(R.id.player_title);
        recFrame = findViewById(R.id.rec_frame);
        recVideoButton = findViewById(R.id.rec_video_btn);
        recVideoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    recFrame.setVisibility(View.VISIBLE);
                } else {
                    recFrame.setVisibility(View.INVISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //execute the task
                            showToast("錄影存儲到" + Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + "2017" + (new Date()).getTime() + ".mp4");
                        }
                    }, 1000);

                }
            }
        });

        View rootView = findViewById(R.id.root);

        rootView.setOnClickListener(this);

        debugRootView = findViewById(R.id.controls_root);

        debugTextView = findViewById(R.id.debug_text_view);

        retryButton = findViewById(R.id.retry_button);

        retryButton.setOnClickListener(this);

        mTextClock = findViewById(R.id.textClock);
        mTextClock.setTypeface(Typeface.createFromAsset(getAssets(), "video.TTF"));


        mTextClock.setFormat24Hour(Html.fromHtml(htmlStr2) + sdf.format(new Date()));
        mTextClock.setAlpha(0.5f);
        mTextClock.setVisibility(View.INVISIBLE);

        pParams = (FrameLayout.LayoutParams) mTextClock.getLayoutParams();
        lParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lParams.setMargins(0, 32, 113, 0);
        mTextClock.setTextSize(14);
        if (isLandscape()) {
            mTextClock.setLayoutParams(lParams);
            mTextClock.setTextSize(L_SIZE);
        }

        playerTitle.setText(getIntent().getCharSequenceExtra("title"));

        ImageButton backBtn = findViewById(R.id.back_play);
        backBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerActivity.this.finish();
            }
        });

        screenShotBtn = findViewById(R.id.screenshot_btn);
        screenShotBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("截圖保存到 " + Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + "2017" + (new Date()).getTime() + ".jpg");
            }
        });

        ToggleButton soundBtn = findViewById(R.id.sound_btn);
        soundBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    player.setVolume(1.0f);
                } else {
                    player.setVolume(0);
                }
            }
        });

        simpleExoPlayerView = findViewById(R.id.player_view);
        simpleExoPlayerView.hideController();

        mProgressBar = findViewById(R.id.progressBar_play);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTextClock.setFreezesText(true);
        simpleExoPlayerView.setControllerVisibilityListener(this);
        simpleExoPlayerView.requestFocus();

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetReceiver = new NetReceiver();

        if (NetWorkUtils.isWifiConnected(this)) {
            shouldAutoPlay = true;
            mTextClock.setFormat24Hour(Html.fromHtml(htmlStr));
        } else {
            showNormalDialog();
        }


        registerReceiver(mNetReceiver, filter);

    }

    private void showNormalDialog() {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(PlayerActivity.this);
        normalDialog.setCancelable(false);
        normalDialog.setMessage(R.string.network_tips);
        normalDialog.setPositiveButton("繼續", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shouldAutoPlay = true;
                mTextClock.setFormat24Hour(Html.fromHtml(htmlStr));
            }
        });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PlayerActivity.this.finish();
            }
        });
        // 显示
        normalDialog.show();
    }

    @Override

    public void onNewIntent(Intent intent) {
        if (intent.getAction().equals("realNetworkChanged")) {
            if (intent.getBooleanExtra("realNetwork", true)) {
                Log.d("network", "onNewIntent:true ");
                player.setRepeatMode(Player.REPEAT_MODE_ONE);
                player.setPlayWhenReady(shouldAutoPlay);
            } else {
                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                player.setPlayWhenReady(false);
            }
            return;
        }
        releasePlayer();

        clearResumePosition();

        setIntent(intent);


    }

    @Override

    public void onStart() {

        super.onStart();

        if (Util.SDK_INT > 23) {

            initializePlayer();

        }

    }

    @Override

    public void onResume() {

        super.onResume();

        if ((Util.SDK_INT <= 23 || player == null)) {

            initializePlayer();

        }

    }

    @Override

    public void onPause() {

        super.onPause();

        if (Util.SDK_INT <= 23) {

            releasePlayer();

        }

    }

    @Override

    public void onStop() {

        super.onStop();

        if (Util.SDK_INT > 23) {

            releasePlayer();

        }

    }

    @Override

    public void onDestroy() {

        super.onDestroy();

        releaseAdsLoader();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        unregisterReceiver(mNetReceiver);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mTextClock.setLayoutParams(lParams);
            mTextClock.setTextSize(L_SIZE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            mTextClock.setLayoutParams(pParams);
            mTextClock.setTextSize(14);
        }
        Log.d("Test", "rotate");
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,

                                           @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            initializePlayer();

        } else {

            showToast(R.string.storage_permission_denied);

            finish();

        }

    }


    // Activity input

    @Override

    public boolean dispatchKeyEvent(KeyEvent event) {

        // If the event was not handled then see if the player view can handle it.

        return super.dispatchKeyEvent(event) || simpleExoPlayerView.dispatchKeyEvent(event);

    }


    // OnClickListener methods

    @Override

    public void onClick(View view) {

        if (view == retryButton) {

            initializePlayer();

        } else if (view.getParent() == debugRootView) {

            MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

            if (mappedTrackInfo != null) {

                trackSelectionHelper.showSelectionDialog(this, ((Button) view).getText(),

                        trackSelector.getCurrentMappedTrackInfo(), (int) view.getTag());

            }

        }

    }


    // PlaybackControlView.VisibilityListener implementation

    @Override

    public void onVisibilityChange(int visibility) {

        debugRootView.setVisibility(visibility);

    }


    // Internal methods

    private void initializePlayer() {

        Intent intent = getIntent();

        boolean needNewPlayer = player == null;

        if (needNewPlayer) {

            TrackSelection.Factory adaptiveTrackSelectionFactory =

                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(adaptiveTrackSelectionFactory);

            trackSelectionHelper = new TrackSelectionHelper(trackSelector, adaptiveTrackSelectionFactory);

            lastSeenTrackGroupArray = null;

            eventLogger = new EventLogger(trackSelector);


            UUID drmSchemeUuid = intent.hasExtra(DRM_SCHEME_UUID_EXTRA)

                    ? UUID.fromString(intent.getStringExtra(DRM_SCHEME_UUID_EXTRA)) : null;

            DrmSessionManager<FrameworkMediaCrypto> drmSessionManager = null;

            if (drmSchemeUuid != null) {

                String drmLicenseUrl = intent.getStringExtra(DRM_LICENSE_URL);

                String[] keyRequestPropertiesArray = intent.getStringArrayExtra(DRM_KEY_REQUEST_PROPERTIES);

                int errorStringId = R.string.error_drm_unknown;

                if (Util.SDK_INT < 18) {

                    errorStringId = R.string.error_drm_not_supported;

                } else {

                    try {

                        drmSessionManager = buildDrmSessionManagerV18(drmSchemeUuid, drmLicenseUrl,

                                keyRequestPropertiesArray);

                    } catch (UnsupportedDrmException e) {

                        errorStringId = e.reason == UnsupportedDrmException.REASON_UNSUPPORTED_SCHEME

                                ? R.string.error_drm_unsupported_scheme : R.string.error_drm_unknown;

                    }

                }

                if (drmSessionManager == null) {

                    showToast(errorStringId);

                    return;

                }

            }


            boolean preferExtensionDecoders = intent.getBooleanExtra(PREFER_EXTENSION_DECODERS, false);

            @DefaultRenderersFactory.ExtensionRendererMode int extensionRendererMode =

                    ((DemoApplication) getApplication()).useExtensionRenderers()

                            ? (preferExtensionDecoders ? DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER

                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON)

                            : DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF;

            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this,

                    drmSessionManager, extensionRendererMode);


            player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector);

            player.addListener(this);

            player.addListener(eventLogger);

            player.addMetadataOutput(eventLogger);

            player.setAudioDebugListener(eventLogger);

            player.setVideoDebugListener(eventLogger);


            simpleExoPlayerView.setPlayer(player);

            player.setPlayWhenReady(shouldAutoPlay);

            debugViewHelper = new DebugTextViewHelper(player, debugTextView);

            debugViewHelper.start();

        }

        String action = intent.getAction();

        Uri[] uris;

        String[] extensions;

        if (ACTION_VIEW.equals(action)) {

            uris = new Uri[]{intent.getData()};

            extensions = new String[]{intent.getStringExtra(EXTENSION_EXTRA)};

        } else if (ACTION_VIEW_LIST.equals(action)) {

            String[] uriStrings = intent.getStringArrayExtra(URI_LIST_EXTRA);

            uris = new Uri[uriStrings.length];

            for (int i = 0; i < uriStrings.length; i++) {

                uris[i] = Uri.parse(uriStrings[i]);

            }

            extensions = intent.getStringArrayExtra(EXTENSION_LIST_EXTRA);

            if (extensions == null) {

                extensions = new String[uriStrings.length];

            }

        } else {

            showToast(getString(R.string.unexpected_intent_action, action));

            return;

        }

        if (Util.maybeRequestReadExternalStoragePermission(this, uris)) {

            // The player will be reinitialized if the permission is granted.

            return;

        }

        MediaSource[] mediaSources = new MediaSource[uris.length];

        for (int i = 0; i < uris.length; i++) {

            mediaSources[i] = buildMediaSource(uris[i], extensions[i]);

        }

        MediaSource mediaSource = mediaSources.length == 1 ? mediaSources[0]

                : new ConcatenatingMediaSource(mediaSources);

        String adTagUriString = intent.getStringExtra(AD_TAG_URI_EXTRA);

        if (adTagUriString != null) {

            Uri adTagUri = Uri.parse(adTagUriString);

            if (!adTagUri.equals(loadedAdTagUri)) {

                releaseAdsLoader();

                loadedAdTagUri = adTagUri;

            }

            try {

                mediaSource = createAdsMediaSource(mediaSource, Uri.parse(adTagUriString));

            } catch (Exception e) {

                showToast(R.string.ima_not_loaded);

            }

        } else {

            releaseAdsLoader();

        }

        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {

            player.seekTo(resumeWindow, resumePosition);

        }
        player.setRepeatMode(Player.REPEAT_MODE_ALL);

        player.prepare(mediaSource, !haveResumePosition, false);

        inErrorState = false;


        updateButtonVisibilities();

    }

    private MediaSource buildMediaSource(Uri uri, String overrideExtension) {

        int type = TextUtils.isEmpty(overrideExtension) ? Util.inferContentType(uri)

                : Util.inferContentType("." + overrideExtension);

        switch (type) {

            case C.TYPE_SS:

                return new SsMediaSource(uri, buildDataSourceFactory(false),

                        new DefaultSsChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);

            case C.TYPE_DASH:

                return new DashMediaSource(uri, buildDataSourceFactory(false),

                        new DefaultDashChunkSource.Factory(mediaDataSourceFactory), mainHandler, eventLogger);

            case C.TYPE_HLS:

                return new HlsMediaSource(uri, mediaDataSourceFactory, mainHandler, eventLogger);

            case C.TYPE_OTHER:

                return new ExtractorMediaSource(uri, mediaDataSourceFactory, new DefaultExtractorsFactory(),

                        mainHandler, eventLogger);

            default: {

                throw new IllegalStateException("Unsupported type: " + type);

            }

        }

    }

    private DrmSessionManager<FrameworkMediaCrypto> buildDrmSessionManagerV18(UUID uuid,

                                                                              String licenseUrl, String[] keyRequestPropertiesArray) throws UnsupportedDrmException {

        HttpMediaDrmCallback drmCallback = new HttpMediaDrmCallback(licenseUrl,

                buildHttpDataSourceFactory(false));

        if (keyRequestPropertiesArray != null) {

            for (int i = 0; i < keyRequestPropertiesArray.length - 1; i += 2) {

                drmCallback.setKeyRequestProperty(keyRequestPropertiesArray[i],

                        keyRequestPropertiesArray[i + 1]);

            }

        }

        return new DefaultDrmSessionManager<>(uuid, FrameworkMediaDrm.newInstance(uuid), drmCallback,

                null, mainHandler, eventLogger);

    }

    private void releasePlayer() {

        if (player != null) {

            debugViewHelper.stop();

            debugViewHelper = null;

            shouldAutoPlay = player.getPlayWhenReady();

            updateResumePosition();

            player.release();

            player = null;

            trackSelector = null;

            trackSelectionHelper = null;

            eventLogger = null;

        }

    }

    private void updateResumePosition() {

        resumeWindow = player.getCurrentWindowIndex();

        resumePosition = Math.max(0, player.getContentPosition());

    }

    private void clearResumePosition() {

        resumeWindow = C.INDEX_UNSET;

        resumePosition = C.TIME_UNSET;

    }

    /**
     * Returns a new DataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          <p>
     *                          DataSource factory.
     * @return A new DataSource factory.
     */

    private DataSource.Factory buildDataSourceFactory(boolean useBandwidthMeter) {

        return ((DemoApplication) getApplication())

                .buildDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);

    }

    /**
     * Returns a new HttpDataSource factory.
     *
     * @param useBandwidthMeter Whether to set {@link #BANDWIDTH_METER} as a listener to the new
     *                          <p>
     *                          DataSource factory.
     * @return A new HttpDataSource factory.
     */

    private HttpDataSource.Factory buildHttpDataSourceFactory(boolean useBandwidthMeter) {

        return ((DemoApplication) getApplication())

                .buildHttpDataSourceFactory(useBandwidthMeter ? BANDWIDTH_METER : null);

    }

    /**
     * Returns an ads media source, reusing the ads loader if one exists.
     *
     * @throws Exception Thrown if it was not possible to create an ads media source, for example, due
     *                   <p>
     *                   to a missing dependency.
     */

    private MediaSource createAdsMediaSource(MediaSource mediaSource, Uri adTagUri) throws Exception {

        // Load the extension source using reflection so the demo app doesn't have to depend on it.

        // The ads loader is reused for multiple playbacks, so that ad playback can resume.

        Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");

        if (imaAdsLoader == null) {

            imaAdsLoader = loaderClass.getConstructor(Context.class, Uri.class)

                    .newInstance(this, adTagUri);

            adOverlayViewGroup = new FrameLayout(this);

            // The demo app has a non-null overlay frame layout.

            simpleExoPlayerView.getOverlayFrameLayout().addView(adOverlayViewGroup);

        }

        Class<?> sourceClass =

                Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsMediaSource");

        Constructor<?> constructor = sourceClass.getConstructor(MediaSource.class,

                DataSource.Factory.class, loaderClass, ViewGroup.class);

        return (MediaSource) constructor.newInstance(mediaSource, mediaDataSourceFactory, imaAdsLoader,

                adOverlayViewGroup);

    }

    private void releaseAdsLoader() {

        if (imaAdsLoader != null) {

            try {

                Class<?> loaderClass = Class.forName("com.google.android.exoplayer2.ext.ima.ImaAdsLoader");

                Method releaseMethod = loaderClass.getMethod("release");

                releaseMethod.invoke(imaAdsLoader);

            } catch (Exception e) {

                // Should never happen.

                throw new IllegalStateException(e);

            }

            imaAdsLoader = null;

            loadedAdTagUri = null;

            simpleExoPlayerView.getOverlayFrameLayout().removeAllViews();

        }

    }


    // Player.EventListener implementation

    @Override

    public void onLoadingChanged(boolean isLoading) {

        // Do nothing.
        if (isLoading) {


        } else {


        }

    }

    @Override

    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch (playbackState) {
            case Player.STATE_BUFFERING:
                mProgressBar.setVisibility(View.VISIBLE);
                mTextClock.setVisibility(View.INVISIBLE);
                break;
            case Player.STATE_ENDED:
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case Player.STATE_IDLE:
                break;
            case Player.STATE_READY:
                mProgressBar.setVisibility(View.INVISIBLE);
                mTextClock.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }


        //updateButtonVisibilities();

    }

    @Override

    public void onRepeatModeChanged(int repeatMode) {

        // Do nothing.

    }

    @Override

    public void onPositionDiscontinuity() {

        if (inErrorState) {

            // This will only occur if the user has performed a seek whilst in the error state. Update the

            // resume position so that if the user then retries, playback will resume from the position to

            // which they seeked.

            updateResumePosition();

        }

    }

    @Override

    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

        // Do nothing.

    }

    @Override

    public void onTimelineChanged(Timeline timeline, Object manifest) {

        // Do nothing.

    }

    @Override

    public void onPlayerError(ExoPlaybackException e) {

        String errorString = null;

        if (e.type == ExoPlaybackException.TYPE_RENDERER) {

            Exception cause = e.getRendererException();

            if (cause instanceof DecoderInitializationException) {

                // Special case for decoder initialization failures.

                DecoderInitializationException decoderInitializationException =

                        (DecoderInitializationException) cause;

                if (decoderInitializationException.decoderName == null) {

                    if (decoderInitializationException.getCause() instanceof DecoderQueryException) {

                        errorString = getString(R.string.error_querying_decoders);

                    } else if (decoderInitializationException.secureDecoderRequired) {

                        errorString = getString(R.string.error_no_secure_decoder,

                                decoderInitializationException.mimeType);

                    } else {

                        errorString = getString(R.string.error_no_decoder,

                                decoderInitializationException.mimeType);

                    }

                } else {

                    errorString = getString(R.string.error_instantiating_decoder,

                            decoderInitializationException.decoderName);

                }

            }

        }

        if (errorString != null) {

            showToast(errorString);

        }

        inErrorState = true;

        if (isBehindLiveWindow(e)) {

            clearResumePosition();

            initializePlayer();

        } else {

            updateResumePosition();

            updateButtonVisibilities();

            showControls();

        }

    }

    @Override

    @SuppressWarnings("ReferenceEquality")

    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

        updateButtonVisibilities();

        if (trackGroups != lastSeenTrackGroupArray) {

            MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

            if (mappedTrackInfo != null) {

                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_VIDEO)

                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {

                    showToast(R.string.error_unsupported_video);

                }

                if (mappedTrackInfo.getTrackTypeRendererSupport(C.TRACK_TYPE_AUDIO)

                        == MappedTrackInfo.RENDERER_SUPPORT_UNSUPPORTED_TRACKS) {

                    showToast(R.string.error_unsupported_audio);

                }

            }

            lastSeenTrackGroupArray = trackGroups;

        }

    }


    // User controls

    private void updateButtonVisibilities() {

        debugRootView.removeAllViews();


        retryButton.setVisibility(inErrorState ? View.VISIBLE : View.GONE);

        debugRootView.addView(retryButton);


        if (player == null) {

            return;

        }


        MappedTrackInfo mappedTrackInfo = trackSelector.getCurrentMappedTrackInfo();

        if (mappedTrackInfo == null) {

            return;

        }


        for (int i = 0; i < mappedTrackInfo.length; i++) {

            TrackGroupArray trackGroups = mappedTrackInfo.getTrackGroups(i);

            if (trackGroups.length != 0) {

                Button button = new Button(this);

                int label;

                switch (player.getRendererType(i)) {

                    case C.TRACK_TYPE_AUDIO:

                        label = R.string.audio;

                        break;

                    case C.TRACK_TYPE_VIDEO:

                        label = R.string.video;

                        break;

                    case C.TRACK_TYPE_TEXT:

                        label = R.string.text;

                        break;

                    default:

                        continue;

                }

                button.setText(label);

                button.setTag(i);

                button.setOnClickListener(this);

                debugRootView.addView(button, debugRootView.getChildCount() - 1);

            }

        }

    }

    private void showControls() {

        debugRootView.setVisibility(View.VISIBLE);

    }

    private void showToast(int messageId) {

        showToast(getString(messageId));

    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

    }

    private class NetReceiver extends BroadcastReceiver {
        private Date stopDate;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (shouldAutoPlay) {
                if (!NetWorkUtils.isNetworkConnected(context)) {
                    if (stopDate == null) {
                        stopDate = new Date();
                    }

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //execute the task
                            if (!NetWorkUtils.isNetworkConnected(getApplicationContext()))
                                showToast("無法連結網路");
                        }
                    }, 1000);
                    mTextClock.setFormat24Hour(Html.fromHtml(htmlStr2) + sdf.format(stopDate));
                    mProgressBar.setVisibility(View.VISIBLE);
                    recVideoButton.setEnabled(false);
                    screenShotBtn.setEnabled(false);
                } else {
                    mTextClock.setFormat24Hour(Html.fromHtml(htmlStr));
                    mProgressBar.setVisibility(View.INVISIBLE);
                    recVideoButton.setEnabled(true);
                    screenShotBtn.setEnabled(true);
                    stopDate = null;
                }
            }
        }
    }


}
