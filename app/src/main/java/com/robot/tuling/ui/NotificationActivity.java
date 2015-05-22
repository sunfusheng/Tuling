package com.robot.tuling.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.robot.tuling.R;
import com.robot.tuling.ui.adapter.NotificationAdapter;
import com.robot.tuling.ui.base.BaseActivity;
import com.robot.tuling.util.KeyBoardUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

/**
 * Created by sunfusheng on 2015/1/13.
 */
public class NotificationActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener,
        AdapterView.OnItemClickListener, StickyListHeadersListView.OnHeaderClickListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.slhl_listview)
    StickyListHeadersListView mSlhlListview;
    @InjectView(R.id.srl_layout)
    SwipeRefreshLayout mSrlLayout;

    private NotificationAdapter mNotificationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.inject(this);

        initData();
    }

    private void initData() {
        initActionBar();
        initSwipeRefreshLayout();
        initStickyList();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.action_notification));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initSwipeRefreshLayout() {
        mSrlLayout.setOnRefreshListener(this);
        mSrlLayout.setProgressBackgroundColor(android.R.color.white);
        mSrlLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light);
    }

    private void initStickyList() {
        mNotificationAdapter = new NotificationAdapter(this);

        mSlhlListview.setOnItemClickListener(this);
        mSlhlListview.setOnHeaderClickListener(this);
//        mSlhlListview.setOnStickyHeaderChangedListener(this);
//        mSlhlListview.setOnStickyHeaderOffsetChangedListener(this);
//        mSlhlListview.addHeaderView(getLayoutInflater().inflate(R.layout.list_header, null));
//        mSlhlListview.addFooterView(getLayoutInflater().inflate(R.layout.list_footer, null));
//        mSlhlListview.setEmptyView(findViewById(R.id.empty));
//        mSlhlListview.setDrawingListUnderStickyHeader(false);
//        mSlhlListview.setAreHeadersSticky(true);
        mSlhlListview.setAdapter(mNotificationAdapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Item " + position + " clicked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId, boolean currentlySticky) {
        Toast.makeText(this, "HeaderItem: " + l.getAdapter().getItem(itemPosition), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        KeyBoardUtil.hideKeyboard(NotificationActivity.this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSrlLayout.setRefreshing(false);
                Toast.makeText(NotificationActivity.this, "下拉刷新成功", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

}
