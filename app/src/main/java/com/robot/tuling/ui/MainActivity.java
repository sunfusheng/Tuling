package com.robot.tuling.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.robot.tuling.R;
import com.robot.tuling.constant.TulingParameters;
import com.robot.tuling.ui.adapter.ChatMessageAdapter;
import com.robot.tuling.ui.control.HttpControl;
import com.robot.tuling.ui.control.NavigateManager;
import com.robot.tuling.ui.entity.MessageEntity;
import com.robot.tuling.util.IsNullOrEmpty;
import com.robot.tuling.util.KeyBoardUtil;
import com.robot.tuling.util.TimeUtil;
import com.robot.tuling.widget.ActionSheet;
import com.robot.tuling.widget.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class MainActivity extends ActionBarActivity implements View.OnTouchListener, View.OnClickListener {

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.lv_message)
    ListView mLvMessage;
    @InjectView(R.id.send_message_btn)
    ImageView mSendMessageBtn;
    @InjectView(R.id.message_content_edittext)
    EditText mMessageContentEdittext;
    @InjectView(R.id.input_bottom)
    FrameLayout mInputBottom;
    @InjectView(R.id.bottom_bar_linearlayout)
    LinearLayout mBottomBarLinearlayout;
    @InjectView(R.id.input_relativelayout)
    RelativeLayout mInputRelativelayout;
    @InjectView(R.id.iv_user_avatar)
    CircleImageView mIvUserAvatar;
    @InjectView(R.id.ll_user_info)
    LinearLayout mLlUserInfo;
    @InjectView(R.id.iv_settings_icon)
    ImageView mIvSettingsIcon;
    @InjectView(R.id.rl_settings)
    RelativeLayout mRlSettings;
    @InjectView(R.id.tv_version_right)
    LinearLayout mTvVersionRight;
    @InjectView(R.id.drawer_view)
    RelativeLayout mDrawerView;
    @InjectView(R.id.drawer)
    DrawerLayout mDrawer;
    @InjectView(R.id.iv_notification_icon)
    ImageView mIvNotificationIcon;
    @InjectView(R.id.rl_notification)
    RelativeLayout mRlNotification;

    private ActionBarDrawerToggle mDrawerToggle;
    private List<MessageEntity> msgList = new ArrayList<>();
    private ChatMessageAdapter msgAdapter;

    private MessageReceiver mMessageReceiver;
    public static boolean isForeground = false;
    private String JPUSH_ALIAS = "group";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        initData();
        initListener();
        registerMessageReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
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
                aboutSoftwareDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void aboutSoftwareDialog() {
        ActionSheet.createBuilder(this, getSupportFragmentManager())
                .setCancelButtonTitle(getString(R.string.cancel))
                .setOtherButtonTitles(getString(R.string.about), getString(R.string.exit))
                .setCancelableOnTouchOutside(true)
                .setListener(new ActionSheet.ActionSheetListener() {
                    @Override
                    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
                    }

                    @Override
                    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
                        switch (index) {
                            case 0:
                                NavigateManager.gotoAboutActivity(MainActivity.this);
                                break;
                            case 1:
                                finish();
                                break;
                        }
                    }
                }).show();
    }

    private void initData() {
        initActionBar();
        initAdapter();
        initJPushAlias(JPUSH_ALIAS);
    }

    private void tulingHttpEvent(String input) {
        HttpUtils httpUtils = new HttpUtils();
        final String url = HttpControl.getTulingUrl(input);
        httpUtils.send(HttpRequest.HttpMethod.GET, url,
                new RequestCallBack<String>(){
                    @Override
                    public void onStart() {
                        Log.d(TulingParameters.TAG, url);
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {}

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        Log.d(TulingParameters.TAG, responseInfo.result);
                        MessageEntity entity = getMessageEntity(responseInfo.result);
                        handleNewMessageEntity(entity);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                    }
                });
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
            }
            return entity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleNewMessageEntity(MessageEntity entity) {
        if (entity == null) {
            return ;
        }
        entity.save();
        msgList.add(entity);
        msgAdapter.notifyDataSetChanged();
    }

    private void initActionBar() {
        mToolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, 0, 0);
        mDrawerToggle.syncState();
        mDrawer.setDrawerListener(mDrawerToggle);
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
        mLvMessage.setAdapter(msgAdapter);
        mLvMessage.setSelection(msgAdapter.getCount());
    }

    private void initJPushAlias(String alias) {
        JPushInterface.setAlias(this, alias, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> strings) {

            }
        });
    }

    private void initListener() {
        mDrawerView.setOnTouchListener(this);
        mRlNotification.setOnClickListener(this);
        mRlSettings.setOnClickListener(this);
        mSendMessageBtn.setOnClickListener(this);
        mLvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                KeyBoardUtil.hideKeyboard(MainActivity.this);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_btn:
                sendMessage();
                break;
            case R.id.rl_notification:
                NavigateManager.gotoNotificationActivity(this);
                break;
            case R.id.rl_settings:
                NavigateManager.gotoSettingsActivity(this);
                break;
            default:
                break;
        }
    }

    public void sendMessage() {
        String msg = mMessageContentEdittext.getText().toString().trim();

        if (!IsNullOrEmpty.isEmpty(msg)) {
            MessageEntity entity = new MessageEntity();
            entity.setText(msg);
            entity.setType(TulingParameters.TYPE_SEND);
            entity.setTime(TimeUtil.getCurrentTimeMillis());
            handleNewMessageEntity(entity);
            mMessageContentEdittext.setText("");
            tulingHttpEvent(msg);
        }
    }

    @Override
    protected void onDestroy() {
        unRegisterMessageReceiver();
        super.onDestroy();
    }

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(TulingParameters.MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    public void unRegisterMessageReceiver() {
        if (mMessageReceiver != null) {
            try {
                unregisterReceiver(mMessageReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (TulingParameters.MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                MessageEntity entity = new MessageEntity();
                entity.setType(TulingParameters.TYPE_RECEIVE);
                entity.setTime(TimeUtil.getCurrentTimeMillis());
                entity.setText(intent.getStringExtra(TulingParameters.KEY_MESSAGE));
                handleNewMessageEntity(entity);
            }
        }
    }

}
