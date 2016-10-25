package com.chaibytes.newyorktimessearch.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaibytes.newyorktimessearch.R;
import com.chaibytes.newyorktimessearch.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pdebadarshini on 10/22/16.
 */

public class ArticleArrayAdapter extends ArrayAdapter<Article> {
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the data item for current position
        Article article = this.getItem(position);

        // check to see if existing view is being reused
        // not using recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
        }

        // find the image view
        ImageView imageView  = (ImageView) convertView.findViewById(R.id.ivImage);

        // clear out the recycled image from convertView from last time
        imageView.setImageResource(0);

        TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
        tvTitle.setText(article.getHeadline());

        // populate the thumbnail image
        // remote download the image in the background

        String thumbnail = article.getThumbNail();
        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(imageView);
        } else {
            imageView.setImageResource(R.mipmap.ic_no_image);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        return convertView;
    }
}
