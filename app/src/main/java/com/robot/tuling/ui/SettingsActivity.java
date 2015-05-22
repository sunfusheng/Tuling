package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.robot.tuling.R;
import com.robot.tuling.sharedpreferences.SettingsSharedPreferences;
import com.robot.tuling.ui.base.BaseActivity;
import com.zcw.togglebutton.ToggleButton;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SettingsActivity extends BaseActivity {

    @InjectView(R.id.tb_msg_reception)
    ToggleButton mTbMsgReception;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.inject(this);

        initData();
        initListener();
    }

    private void initData() {
        initActionBar();
        if (getSharePrefence(SettingsSharedPreferences.class).isReceivePush()) {
            mTbMsgReception.setToggleOn();
        } else {
            mTbMsgReception.setToggleOff();
        }
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.action_settings));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initListener() {
        mTbMsgReception.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                getSharePrefence(SettingsSharedPreferences.class).isReceivePush(on);
            }
        });
    }

}
