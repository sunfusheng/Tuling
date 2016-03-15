package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.robot.tuling.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/1/13.
 */
public class AboutActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.gv_about)
    GifView gvAbout;
    @Bind(R.id.lr_title)
    TextView lrTitle;
    @Bind(R.id.tv_version_right)
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
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initGifView() {
        gvAbout.setGifImage(R.drawable.gif_robot_walk);
        gvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowGifView) {
                    gvAbout.showCover();
                } else {
                    gvAbout.showAnimation();
                }
                isShowGifView = !isShowGifView;
            }
        });
    }

}
