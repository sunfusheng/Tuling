package com.robot.tuling.sharedpreferences;

import de.devland.esperandro.SharedPreferenceActions;
import de.devland.esperandro.SharedPreferenceMode;
import de.devland.esperandro.annotations.SharedPreferences;

/**
 * Created by sunfusheng on 2014/12/31.
 */
@SharedPreferences(name = "settings", mode = SharedPreferenceMode.PRIVATE)
public interface SettingsSharedPreferences extends SharedPreferenceActions {

    boolean isReceivePush();
    void isReceivePush(boolean isReceivePush);
}
