package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.robot.tuling.R;
import com.robot.tuling.adapter.ChatMessageAdapter;
import com.robot.tuling.constant.TulingParameters;
import com.robot.tuling.control.NavigateManager;
import com.robot.tuling.entity.MessageEntity;
import com.robot.tuling.entity.NewsEntity;
import com.robot.tuling.util.IsNullOrEmpty;
import com.robot.tuling.util.KeyBoardUtil;
import com.robot.tuling.util.TimeUtil;
import com.robot.tuling.widget.CircleImageView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.lv_message)
    ListView lvMessage;
    @Bind(R.id.send_message_btn)
    ImageView sendMessageBtn;
    @Bind(R.id.message_content_edittext)
    EditText messageContentEdittext;
    @Bind(R.id.input_bottom)
    FrameLayout inputBottom;
    @Bind(R.id.bottom_bar_linearlayout)
    LinearLayout bottomBarLinearlayout;
    @Bind(R.id.input_relativelayout)
    RelativeLayout inputRelativelayout;
    @Bind(R.id.iv_user_avatar)
    CircleImageView ivUserAvatar;
    @Bind(R.id.ll_user_info)
    LinearLayout llUserInfo;
    @Bind(R.id.tv_version_right)
    LinearLayout tvVersionRight;
    @Bind(R.id.drawer_view)
    RelativeLayout drawerView;
    @Bind(R.id.drawer)
    DrawerLayout drawer;

    private ActionBarDrawerToggle mDrawerToggle;
    private List<MessageEntity> msgList = new ArrayList<>();
    private ChatMessageAdapter msgAdapter;

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        if (msgAdapter != null) {
            msgAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                NavigateManager.gotoAboutActivity(MainActivity.this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initData() {
        initActionBar();
        initAdapter();

    }

    private void tulingHttpEvent(String input) {
//        HttpUtils httpUtils = new HttpUtils();
//        final String url = HttpControl.getTulingUrl(input);
//        httpUtils.send(HttpRequest.HttpMethod.GET, url,
//                new RequestCallBack<String>() {
//                    @Override
//                    public void onStart() {
//                        Log.d(TulingParameters.TAG, url);
//                    }
//
//                    @Override
//                    public void onLoading(long total, long current, boolean isUploading) {
//                    }
//
//                    @Override
//                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        Log.d(TulingParameters.TAG, responseInfo.result);
//                        MessageEntity entity = getMessageEntity(responseInfo.result);
//                        handleNewMessageEntity(entity);
//                    }
//
//                    @Override
//                    public void onFailure(HttpException error, String msg) {
//                    }
//                });

    }

    private MessageEntity getMessageEntity(String result) {
        JSONObject jsonObj = null;
        try {
            jsonObj = new JSONObject(result);
            MessageEntity entity = new MessageEntity();

            entity.setType(TulingParameters.TYPE_RECEIVE);
            entity.setTime(TimeUtil.getCurrentTimeMillis());
            entity.setCode(jsonObj.optInt("code"));
            entity.setText(jsonObj.optString("text"));

            switch (jsonObj.optInt("code")) {
                case TulingParameters.TulingCode.URL:
                    entity.setUrl(jsonObj.optString("url"));
                    break;
                case TulingParameters.TulingCode.NEWS:
                    List<NewsEntity> newsList = JSON.parseArray(jsonObj.optJSONArray("list").toString(), NewsEntity.class);
                    if (newsList == null) {
                        break;
                    }
                    NewsEntity.deleteAll(NewsEntity.class);
                    NewsEntity.saveInTx(newsList);
                    break;
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleNewMessageEntity(MessageEntity entity) {
        if (entity == null) {
            return;
        }
        entity.save();
        msgList.add(entity);
        msgAdapter.notifyDataSetChanged();
    }

    private void initActionBar() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, 0, 0);
        mDrawerToggle.syncState();
        drawer.setDrawerListener(mDrawerToggle);
    }

    private void initAdapter() {
        msgList = MessageEntity.listAll(MessageEntity.class);
        if (msgList.size() == 0) {
            MessageEntity entity = new MessageEntity();
            entity.setType(TulingParameters.TYPE_RECEIVE);
            entity.setTime(TimeUtil.getCurrentTimeMillis());
            entity.setText("您好，我是图灵机器人！");
            entity.save();
            msgList.add(entity);
        }
        msgAdapter = new ChatMessageAdapter(this, msgList);
        lvMessage.setAdapter(msgAdapter);
        lvMessage.setSelection(msgAdapter.getCount());
    }

    private void initListener() {
        sendMessageBtn.setOnClickListener((v) -> sendMessage());

        lvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                KeyBoardUtil.hideKeyboard(MainActivity.this);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    public void sendMessage() {
        String msg = messageContentEdittext.getText().toString().trim();

        if (!IsNullOrEmpty.isEmpty(msg)) {
            MessageEntity entity = new MessageEntity();
            entity.setText(msg);
            entity.setType(TulingParameters.TYPE_SEND);
            entity.setTime(TimeUtil.getCurrentTimeMillis());
            handleNewMessageEntity(entity);
            messageContentEdittext.setText("");
            tulingHttpEvent(msg);
        }
    }

}
