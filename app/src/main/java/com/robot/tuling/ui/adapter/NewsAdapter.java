package com.robot.tuling.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.robot.tuling.R;
import com.robot.tuling.ui.adapter.base.BaseListAdapter;
import com.robot.tuling.ui.entity.NewsEntity;
import com.robot.tuling.util.IsNullOrEmpty;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2015/2/5.
 */
public class NewsAdapter extends BaseListAdapter<NewsEntity> {

    private BitmapUtils bitmapUtils;

    public NewsAdapter(Context context, List<NewsEntity> list) {
        super(context, list);
        bitmapUtils = new BitmapUtils(context);
    }

    @Override
    public View bindView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_news_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final NewsEntity entity = getItem(position);
        if (!IsNullOrEmpty.isEmpty(entity.getIcon())) {
            bitmapUtils.display(holder.ivNewsIcon, entity.getIcon());
        }
        holder.tvNewsTitle.setText(entity.getArticle() + "");
        holder.tvNewsContent.setText(entity.getSource() + "");

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.iv_news_icon)
        ImageView ivNewsIcon;
        @Bind(R.id.tv_news_title)
        TextView tvNewsTitle;
        @Bind(R.id.tv_news_content)
        TextView tvNewsContent;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
