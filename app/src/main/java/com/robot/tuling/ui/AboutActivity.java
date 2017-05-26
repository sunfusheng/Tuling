package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.robot.tuling.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/1/13.
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gv_about)
    GifView gvAbout;
    @BindView(R.id.lr_title)
    TextView lrTitle;
    @BindView(R.id.tv_version_right)
    LinearLayout tvVersionRight;

    private boolean isShowGifView = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        initActionBar();
        initGifView();
    }

    private void initActionBar() {
        toolbar.setTitle(getString(R.string.about));
        toolbar.setSubtitle(getString(R.string.app_name));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initGifView() {
        gvAbout.setGifImage(R.drawable.gif_robot_walk);
        gvAbout.setOnClickListener((e)->{
            if (isShowGifView) {
                gvAbout.showCover();
            } else {
                gvAbout.showAnimation();
            }
            isShowGifView = !isShowGifView;
        });
    }

}
