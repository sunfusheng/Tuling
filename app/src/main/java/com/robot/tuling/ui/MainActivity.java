package com.robot.tuling.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
            MessageEntity entity = new MessageEntity(ChatMessageAdapter.TYPE_LEFT, TimeUtil.getCurrentTimeMillis());
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
        ivSendMsg.setOnClickListener(v -> sendMessage());
//        ivSendMsg.setOnClickListener(v -> funcDemo());

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

    // 给Turing发送问题
    public void sendMessage() {
        String msg = etMsg.getText().toString().trim();

        if (!IsNullOrEmpty.isEmpty(msg)) {
            MessageEntity entity = new MessageEntity(ChatMessageAdapter.TYPE_RIGHT, TimeUtil.getCurrentTimeMillis(), msg);
            msgList.add(entity);
            msgAdapter.notifyDataSetChanged();
            etMsg.setText("");

            // 仅使用 Retrofit 请求接口
//            requestApiByRetrofit(msg);

            // 使用 Retrofit 和 RxJava 请求接口
            requestApiByRetrofit_RxJava(msg);
        }
    }

    // 请求图灵API接口，获得问答信息
    private void requestApiByRetrofit(String info) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TulingParams.TULING_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi api = retrofit.create(RetrofitApi.class);

        Call<MessageEntity> call = api.getTuringInfo(TulingParams.TULING_KEY, info);
        call.enqueue(new Callback<MessageEntity>() {
            @Override
            public void onResponse(Call<MessageEntity> call, Response<MessageEntity> response) {
                handleResponseMessage(response.body());
            }

            @Override
            public void onFailure(Call<MessageEntity> call, Throwable t) {

            }
        });
    }

    // 请求图灵API接口，获得问答信息
    private void requestApiByRetrofit_RxJava(String info) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TulingParams.TULING_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        RetrofitApi api = retrofit.create(RetrofitApi.class);

        api.getTuringInfoByRxJava(TulingParams.TULING_KEY, info)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<MessageEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(MessageEntity entity) {
                        handleResponseMessage(entity);
                    }
                });
    }

    // 通过RxJava将Integer类型转成String类型
    private void funcDemo() {
        Observable.OnSubscribe<Integer> onSubscribe1 = new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(100);
            }
        };

        Func1<Integer, String> func1 = new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return String.valueOf(integer);
            }
        };

        Subscriber<String> subscriber1 = new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.d("onNext: ", s);
            }
        };

        Observable.create(onSubscribe1)
                .map(func1)
                .subscribe(subscriber1);

        // 将上面分解成三步执行
        // Observable<Integer> observable1 = Observable.create(onSubscribe1);
        // Observable<String> observable2 = observable1.map(func1);
        // observable2.subscribe(subscriber1);
    }

    // 处理获得到的问答信息
    private void handleResponseMessage(MessageEntity entity) {
        if (entity == null) return;

        entity.setTime(TimeUtil.getCurrentTimeMillis());
        entity.setType(ChatMessageAdapter.TYPE_LEFT);

        switch (entity.getCode()) {
            case TulingParams.TulingCode.URL:
                entity.setText(entity.getText() + "，点击网址查看：" + entity.getUrl());
                break;
            case TulingParams.TulingCode.NEWS:
                entity.setText(entity.getText() + "，点击查看");
                break;
        }

        msgList.add(entity);
        msgAdapter.notifyDataSetChanged();
    }

}
