package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ant.liao.GifView;
import com.robot.tuling.R;
import com.robot.tuling.ui.base.BaseActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sunfusheng on 2015/1/13.
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.gv_about)
    GifView mGvAbout;

    private boolean isShowGifView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        initActionBar();
        initGifView();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.about));
        mToolbar.setSubtitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initGifView() {
        mGvAbout.setGifImage(R.drawable.gif_robot_walk);
        mGvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowGifView) {
                    mGvAbout.showCover();
                } else {
                    mGvAbout.showAnimation();
                }
                isShowGifView = !isShowGifView;
            }
        });
    }

}
