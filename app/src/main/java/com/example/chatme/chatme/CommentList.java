package com.example.chatme.chatme;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
public class CommentList extends ArrayAdapter<Messages>{
    private Activity context;
    List<Messages> comments;

    public CommentList(Activity context, List<Messages> comments) {
        super(context, R.layout.commentlist, comments);
        this.context = context;
        this.comments = comments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.commentlist, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.txtName);
        TextView textViewComment = (TextView) listViewItem.findViewById(R.id.txtComment);

        Messages comment = comments.get(position);
        textViewName.setText(comment.getName());
        textViewComment.setText(comment.getComment());
        return listViewItem;
    }
}
