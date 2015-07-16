package io.intrepid.nostalgia.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import io.intrepid.nostalgia.R;
import io.intrepid.nostalgia.models.facebook.Comment;
import io.intrepid.nostalgia.models.facebook.ViewHolder;


public class CustomListAdapter extends BaseAdapter {

    Context context;

    protected List<Comment> commentList;
    LayoutInflater inflater;

    public CustomListAdapter(Context context, List<Comment> commentList) {
        this.commentList = commentList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Comment getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {

            holder = new ViewHolder();
            convertView = this.inflater.inflate(R.layout.list_item_comment,
                    parent, false);

            holder.commentName = (TextView) convertView
                    .findViewById(R.id.comment_name);
            holder.commentData = (TextView) convertView
                    .findViewById(R.id.comment_data);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Comment comment = commentList.get(position);
        holder.commentName.setText(comment.getName());
        holder.commentData.setText(comment.getComment());

        return convertView;
    }
}



