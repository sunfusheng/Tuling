package com.robot.tuling.adapter;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.library.bubbleview.BubbleTextVew;
import com.robot.tuling.R;
import com.robot.tuling.constant.TulingParams;
import com.robot.tuling.control.NavigateManager;
import com.robot.tuling.entity.MessageEntity;
import com.robot.tuling.util.SpecialViewUtil;
import com.robot.tuling.util.TimeUtil;

import java.util.List;

public class ChatMessageAdapter extends BaseListAdapter<MessageEntity> {

    private Context mContext;

    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 1;

    public ChatMessageAdapter(Context context, List<MessageEntity> list) {
        super(context, list);
        mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getType() == TYPE_LEFT) {
            return TYPE_LEFT;
        }
        return TYPE_RIGHT;
    }

    private View createViewByType(int position) {
        if (getItem(position).getType() == TYPE_LEFT) {
            return mInflater.inflate(R.layout.item_conversation_left, null);
        }
        return mInflater.inflate(R.layout.item_conversation_right, null);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createViewByType(position);
        }

        final MessageEntity entity = getItem(position);

        TextView tvTime = ViewHolder.get(convertView, R.id.tv_time);
        BubbleTextVew btvMessage = ViewHolder.get(convertView, R.id.btv_message);

        if (isDisplayTime(position)) {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(TimeUtil.friendlyTime(mContext, entity.getTime()));
        } else {
            tvTime.setVisibility(View.GONE);
        }

        switch (entity.getCode()) {
            case TulingParams.TulingCode.URL:
                btvMessage.setText(SpecialViewUtil.getSpannableString(entity.getText(), entity.getUrl()));
                break;
            case TulingParams.TulingCode.NEWS:
                btvMessage.setText(SpecialViewUtil.getSpannableString(entity.getText(), "点击查看"));
                break;
            default:
                btvMessage.setText(entity.getText());
                break;
        }

        btvMessage.setOnClickListener(v -> {
            switch (entity.getCode()) {
                case TulingParams.TulingCode.URL:
                    NavigateManager.gotoDetailActivity(mContext, entity.getUrl());
                    break;
                case TulingParams.TulingCode.NEWS:
                    NavigateManager.gotoNewsActivity(mContext, entity);
                    break;
            }
        });

        btvMessage.setOnLongClickListener(v -> {
            copyDeleteDialog(mContext, entity);
            return false;
        });

        return convertView;
    }

    //  一分钟内的请求与回复不显示时间
    public boolean isDisplayTime(int position) {
        if (position > 0) {
            if ((getItem(position).getTime() - getItem(position-1).getTime()) > 60 * 1000) {
                return true;
            } else {
                return false;
            }
        } else if (position == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void copyDeleteDialog(Context context, MessageEntity entity) {
        new MaterialDialog.Builder(context)
                .items("复制该文本", "删除这一条")
                .itemsCallback((dialog, view, which, text) -> {
                    switch (which) {
                        case 0:
                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            cm.setText(entity.getText());
                            Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            getData().remove(entity);
                            notifyDataSetChanged();
                            break;
                    }
                })
                .show();
    }

}
