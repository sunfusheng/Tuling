package com.robot.tuling.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.ClipboardManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.github.library.bubbleview.BubbleTextVew;
import com.robot.tuling.R;
import com.robot.tuling.entity.MessageEntity;
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

        if (isShowTime(position)) {
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(TimeUtil.friendlyTime(mContext, entity.getTime()));
        } else {
            tvTime.setVisibility(View.GONE);
        }

        btvMessage.setText(entity.getText());
        btvMessage.setOnLongClickListener(v -> {
            copyDeleteDialog(mContext, entity);
            return false;
        });

        return convertView;
    }

    // 十分钟内的请求与回复不显示时间
    public boolean isShowTime(int position) {
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

        copyView.setOnClickListener(v -> {
            alertDialog.dismiss();
            ClipboardManager copy = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            copy.setText(entity.getText());
            Toast.makeText(context, "已复制", Toast.LENGTH_SHORT).show();
        });
        deleteView.setOnClickListener(v -> {
            alertDialog.dismiss();
            getData().remove(entity);
            notifyDataSetChanged();
        });
        cancelView.setOnClickListener(v -> alertDialog.dismiss());
    }

}
