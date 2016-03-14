package com.robot.tuling.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.robot.tuling.R;
import com.robot.tuling.constant.TulingParameters;
import com.robot.tuling.ui.adapter.base.BaseListAdapter;
import com.robot.tuling.ui.adapter.base.ViewHolder;
import com.robot.tuling.ui.control.NavigateManager;
import com.robot.tuling.ui.entity.MessageEntity;
import com.robot.tuling.ui.entity.NewsEntity;
import com.robot.tuling.util.TimeUtil;

import java.util.List;

/**
 * @Description: 对话适配器
 * @author: sunfusheng
 * @date: 2015-2-4 上午
 */
public class ChatMessageAdapter extends BaseListAdapter<MessageEntity> {

    private Context mContext;
    private boolean showTime;

    //文字类、链接类
    private final int TYPE_RECEIVE_TXT = 0;
    private final int TYPE_SEND_TXT = 1;
    //新闻
    private final int TYP_RECEIVE_NEWS = 2;

    public ChatMessageAdapter(Context context, List<MessageEntity> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        MessageEntity entity = getItem(position);
        switch (entity.getCode()) {
            case TulingParameters.TulingCode.TEXT:
            case TulingParameters.TulingCode.URL:
                return entity.getType() == TulingParameters.TYPE_RECEIVE? TYPE_RECEIVE_TXT : TYPE_SEND_TXT;
            case TulingParameters.TulingCode.NEWS:
                return TYP_RECEIVE_NEWS;
            default:
                return entity.getType() == TulingParameters.TYPE_RECEIVE? TYPE_RECEIVE_TXT : TYPE_SEND_TXT;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private View createViewByCode(int position) {
        MessageEntity entity = getItem(position);
        switch (entity.getCode()) {
            case TulingParameters.TulingCode.TEXT:
            case TulingParameters.TulingCode.URL:
                return getItemViewType(position) == TYPE_RECEIVE_TXT ?
                        mInflater.inflate(R.layout.item_chat_received_message, null) : mInflater.inflate(R.layout.item_chat_sent_message, null);
            case TulingParameters.TulingCode.NEWS:
                return mInflater.inflate(R.layout.item_chat_received_message, null);
            default:
                return getItemViewType(position) == TYPE_RECEIVE_TXT ?
                        mInflater.inflate(R.layout.item_chat_received_message, null) : mInflater.inflate(R.layout.item_chat_sent_message, null);
        }
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createViewByCode(position);
        }

        final MessageEntity entity = getItem(position);
        showTime = getShowTime(position);

        TextView mTvTime = ViewHolder.get(convertView, R.id.tv_time);
        TextView mTvMessage = ViewHolder.get(convertView, R.id.tv_message);

        if (showTime) {
            mTvTime.setVisibility(View.VISIBLE);
            mTvTime.setText(TimeUtil.friendlyTime(mContext, entity.getTime()));
        } else {
            mTvTime.setVisibility(View.GONE);
        }

        switch (entity.getCode()) {
            case TulingParameters.TulingCode.TEXT:
                mTvMessage.setText(entity.getText());
                break;
            case TulingParameters.TulingCode.URL:
                mTvMessage.setText("嗨，已帮您找到链接，点击打开。\n网址：" + entity.getUrl());
                break;
            case TulingParameters.TulingCode.NEWS:
                List<NewsEntity> newsList = NewsEntity.listAll(NewsEntity.class);
                mTvMessage.setText("亲，帮您搜索到" + newsList.size() + "条最新新闻，戳这里查看。");
                break;
            default:
                mTvMessage.setText(entity.getText());
                break;
        }

        mTvMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (entity.getCode()) {
                    case TulingParameters.TulingCode.NEWS:
                        NavigateManager.gotoNewsActivity(mContext);
                        break;
                    default:
                        break;
                }
            }
        });

        mTvMessage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                copyDeleteDialog(mContext, entity);
                return false;
            }
        });
        return convertView;
    }

    /*
     * 十分钟内的请求与回复不显示时间
     */
    public boolean getShowTime(int position) {
        if (position > 0) {
            if ((getItem(position).getTime() - getItem(position - 1).getTime() > 60000)) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    @SuppressWarnings("deprecation")
    private void copyDeleteDialog(final Context context, final MessageEntity entity) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.show();

        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_copy_delete);
        TextView copyView = (TextView) window.findViewById(R.id.copy);
        TextView deleteView = (TextView) window.findViewById(R.id.delete);
        TextView cancelView = (TextView) window.findViewById(R.id.cancel);

        copyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                copy.setText(entity.getText());
                Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
            }
        });
        deleteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                entity.delete();
                getList().remove(entity);
                notifyDataSetChanged();
            }
        });
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

}
