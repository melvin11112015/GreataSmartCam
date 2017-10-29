package com.greata.greatasmartcam;

/**
 * Created by Administrator on 2017/10/29.
 */

import android.content.Context;
import android.content.Intent;

/**
 * Created by Administrator on 2017/7/28.
 */

public class BroadcastUtils {
    /**
     * 发送finish页面的广播
     * <p>
     * action可以自己根据需要添加
     *
     * @param context
     */
    public static final String RECEIVER_ACTION_FINISH = "receiver_action_finish_a";

    public static void sendFinishActivityBroadcast(Context context) {


        Intent intent = new Intent(RECEIVER_ACTION_FINISH);

        context.sendBroadcast(intent);


    }

}
