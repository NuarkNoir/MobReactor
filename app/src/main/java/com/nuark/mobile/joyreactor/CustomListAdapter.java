package com.nuark.mobile.joyreactor;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.MessageFormat;
import java.util.ArrayList;

import static com.nuark.mobile.joyreactor.R.id.date_list;
import static com.nuark.mobile.joyreactor.R.id.image_author;
import static com.nuark.mobile.joyreactor.R.id.image_tags;

class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemnames, authors_list, tags_list, date_date_list, date_time_list;

    CustomListAdapter(Activity context, ArrayList<String> itemnames, ArrayList<String> authors_list, ArrayList<String> tags_list, ArrayList<String> date_date_list, ArrayList<String> date_time_list) {
        super(context, R.layout.mylist, itemnames);

        this.context = context;
        this.itemnames = itemnames;
        this.authors_list = authors_list;
        this.tags_list = tags_list;
        this.date_date_list = date_date_list;
        this.date_time_list = date_time_list;
    }

    public View getView(int position, View view, ViewGroup parent) {
        final int pos = position;
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);

        final String date = MessageFormat.format("Опубликовано: {0} {1}", date_date_list.get(position), date_time_list.get(position));

        TextView txtTags = (TextView) rowView.findViewById(image_tags);
        TextView txtAuthor = (TextView) rowView.findViewById(image_author);
        TextView txtDateDate = (TextView) rowView.findViewById(date_list);

        txtTags.setText(MessageFormat.format("Теги: {0}", tags_list.get(position)));
        txtAuthor.setText(MessageFormat.format("Автор: {0}", authors_list.get(position)));
        txtDateDate.setText(date);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Glide.with(context).load(itemnames.get(position)).asBitmap().override(400, 200).centerCrop().placeholder(R.drawable.ic_sync_black_48dp).into(imageView);

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.nuark.mobile.joyreactor.FullscreenPictureActivity.class);
                intent.putExtra("URL", itemnames.get(pos));
                context.startActivity(intent);
            }
        });

        return rowView;
    }
}