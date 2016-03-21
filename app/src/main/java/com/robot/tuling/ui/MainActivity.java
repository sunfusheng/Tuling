package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.robot.tuling.R;
import com.robot.tuling.adapter.ChatMessageAdapter;
import com.robot.tuling.constant.TulingParams;
import com.robot.tuling.control.NavigateManager;
import com.robot.tuling.control.RetrofitApi;
import com.robot.tuling.entity.MessageEntity;
import com.robot.tuling.util.IsNullOrEmpty;
import com.robot.tuling.util.KeyBoardUtil;
import com.robot.tuling.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.lv_message)
    ListView lvMessage;
    @Bind(R.id.iv_send_msg)
    ImageView ivSendMsg;
    @Bind(R.id.et_msg)
    EditText etMsg;
    @Bind(R.id.rl_msg)
    RelativeLayout rlMsg;

    private List<MessageEntity> msgList = new ArrayList<>();
    private ChatMessageAdapter msgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initData();
        initView();
        initListener();
    }

    private void initData() {
        if (msgList.size() == 0) {
            MessageEntity entity = new MessageEntity();
            entity.setType(ChatMessageAdapter.TYPE_LEFT);
            entity.setTime(TimeUtil.getCurrentTimeMillis());
            entity.setText("你好！俺是图灵机器人！\n咱俩聊点什么呢？\n你有什么要问的么？");
            msgList.add(entity);
        }
        msgAdapter = new ChatMessageAdapter(this, msgList);
        lvMessage.setAdapter(msgAdapter);
        lvMessage.setSelection(msgAdapter.getCount());
    }

    private void initView() {
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
    }

    private void initListener() {
        ivSendMsg.setOnClickListener((v) -> sendMessage());
        lvMessage.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                KeyBoardUtil.hideKeyboard(mActivity);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_about:
                NavigateManager.gotoAboutActivity(mContext);
                return true;
            default:
                return false;
        }
    }

    public void sendMessage() {
        String msg = etMsg.getText().toString().trim();

        if (!IsNullOrEmpty.isEmpty(msg)) {
            MessageEntity entity = new MessageEntity();
            entity.setText(msg);
            entity.setType(ChatMessageAdapter.TYPE_RIGHT);
            entity.setTime(TimeUtil.getCurrentTimeMillis());

            msgList.add(entity);
            msgAdapter.notifyDataSetChanged();
            etMsg.setText("");
            askTulingInfo(msg);
        }
    }

    private void askTulingInfo(String info) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TulingParams.TULING_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();

        RetrofitApi api = retrofit.create(RetrofitApi.class);
        Call<MessageEntity> tulingInfo = api.getTulingInfo(TulingParams.TULING_KEY, info);
        tulingInfo.enqueue(new Callback<MessageEntity>() {
            @Override
            public void onResponse(Call<MessageEntity> call, Response<MessageEntity> response) {
                if (response == null) return ;
                if (response.body() != null) {
                    msgList.add(response.body());
                    msgAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<MessageEntity> call, Throwable t) {
            }
        });

    }

}
