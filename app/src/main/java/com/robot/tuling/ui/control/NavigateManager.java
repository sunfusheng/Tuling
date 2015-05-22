package com.robot.tuling.ui.control;

import android.content.Context;
import android.content.Intent;

import com.robot.tuling.ui.AboutActivity;
import com.robot.tuling.ui.NotificationActivity;
import com.robot.tuling.ui.SettingsActivity;

public class NavigateManager {

    public static void gotoNotificationActivity(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }

    public static void gotoSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public static void gotoAboutActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

}
