package com.it_score.admin.uscore001.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.it_score.admin.uscore001.R;
import com.it_score.admin.uscore001.models.Comment;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends BaseAdapter {

    private static final String TAG = "CommentListAdapter";

    ArrayList<Comment> comments = new ArrayList<>();
    Context context;

    public CommentListAdapter(ArrayList<Comment> comments, Context context) {
        this.comments = comments;
        this.context = context;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Comment getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return comments.indexOf(position);
    }

    public class ViewHolder {
        CircleImageView circleImageView;
        TextView senderUsername;
        TextView date;
        TextView body;
        TextView likes;
        ImageView likesImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView == null) {  // item has not been loaded yet
            convertView = LayoutInflater.from(context).inflate(R.layout.comment_item, null);
            viewHolder = new ViewHolder();
            viewHolder.circleImageView = convertView.findViewById(R.id.circleImageView);
            viewHolder.senderUsername = convertView.findViewById(R.id.senderUsername);
            viewHolder.date = convertView.findViewById(R.id.date);
            viewHolder.body = convertView.findViewById(R.id.body);
            viewHolder.likes = convertView.findViewById(R.id.likes);
            viewHolder.likesImage = convertView.findViewById(R.id.likeImageView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Comment comment = comments.get(position);
        String senderImage = comment.getSenderImage();
        String senderUsernameValue = comment.getSenderUsername();
        String dateValue = comment.getCurrentDate();
        String bodyValue = comment.getBody();
        int likesValue = comment.getLikes();

        GlideApp
                .with(convertView.getContext())
                .load(senderImage)
                .fitCenter()
                .into(viewHolder.circleImageView);
        viewHolder.senderUsername.setText(senderUsernameValue);
        viewHolder.date.setText(dateValue);
        viewHolder.body.setText(bodyValue);
        viewHolder.likes.setText(Integer.toString(likesValue));

        return convertView;
    }
}
