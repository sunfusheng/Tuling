package com.robot.tuling.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.robot.tuling.constant.TulingParameters;
import com.robot.tuling.ui.MainActivity;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {

	private static final String TAG = "JPush";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processPushMessage(context, intent);
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接收到推送下来的通知标题: " + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.d(TAG, "接收到推送下来的通知内容: " + bundle.getString(JPushInterface.EXTRA_ALERT));
            processPushMessage(context, intent);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //点击打开通知
//        	Intent i = new Intent(context, MainActivity.class);
//        	i.putExtras(bundle);
//        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//        	context.startActivity(i);
        }
	}

    /*
     * 发送推送和通知到MainActivity
     */
	private void processPushMessage(Context context, Intent intent) {
		if (MainActivity.isForeground) {
            String message = "";
            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                message = "消息：" + intent.getExtras().getString(JPushInterface.EXTRA_MESSAGE);
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                message = "通知：" + intent.getExtras().getString(JPushInterface.EXTRA_ALERT);
            }
			Intent msgIntent = new Intent(TulingParameters.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(TulingParameters.KEY_MESSAGE, message);
			context.sendBroadcast(msgIntent);
		} else {

        }
	}
}
