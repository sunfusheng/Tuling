package com.robot.tuling.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;

import com.robot.tuling.R;
import com.robot.tuling.adapter.NewsAdapter;
import com.robot.tuling.control.NavigateManager;
import com.robot.tuling.entity.NewsEntity;
import com.robot.tuling.util.DisplayUtil;
import com.robot.tuling.widget.refreshswipemenulistview.XListView;
import com.robot.tuling.widget.swipemenulistview.SwipeMenu;
import com.robot.tuling.widget.swipemenulistview.SwipeMenuCreator;
import com.robot.tuling.widget.swipemenulistview.SwipeMenuItem;
import com.robot.tuling.widget.swipemenulistview.SwipeMenuListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/2/5.
 */
public class NewsActivity extends BaseActivity implements XListView.IXListViewListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.xlv_listView)
    XListView xlvListView;
    private List<NewsEntity> newsList;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        initActionBar();
        initData();
        initView();
    }

    private void initActionBar() {
        toolbar.setTitle("新闻");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initData() {
//        newsList = NewsEntity.listAll(NewsEntity.class);
    }

    private void initView() {
        initXlistView();
        newsAdapter = new NewsAdapter(this, newsList);
        xlvListView.setAdapter(newsAdapter);
        setSwipeMenuCreator();
        initSwipeMenuItemClickListener();
    }

    private void initXlistView() {
        xlvListView.setXListViewListener(this);
        xlvListView.setPullRefreshEnable(true);
        xlvListView.setPullLoadEnable(true);
        xlvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NavigateManager.gotoDetailActivity(NewsActivity.this, newsList.get(position - 1).getDetailurl());
            }
        });
    }

    private void setSwipeMenuCreator() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                openItem.setWidth(DisplayUtil.dip2px(NewsActivity.this, 90));
                openItem.setTitle("删除");
                openItem.setTitleSize(16);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        xlvListView.setMenuCreator(creator);
    }

    private void initSwipeMenuItemClickListener() {
        xlvListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deleteNewsListItem(position);
                        break;
                }
                return true;
            }
        });
    }

    private void deleteNewsListItem(int position) {
        NewsEntity entity = newsList.get(position);
        newsList.remove(entity);
        newsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                xlvListView.stopRefresh();
                xlvListView.setRefreshTime("刚刚");
                newsAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                xlvListView.stopLoadMore();
                newsAdapter.notifyDataSetChanged();
            }
        }, 2000);
    }
}
