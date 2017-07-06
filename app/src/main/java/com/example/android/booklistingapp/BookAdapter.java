package com.example.android.booklistingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by miche on 6/26/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    private Context mContext;

    public BookAdapter(Context context, ArrayList<Book> bookList) {
        super(context, 0, bookList);
        mContext = context;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Book book = getItem(position);
        ViewHolder holder = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder();

            holder.bookTitle = (TextView) convertView.findViewById(R.id.book_title);

            holder.bookAuthor = (TextView) convertView.findViewById(R.id.author);

            holder.bookPublisher = (TextView) convertView.findViewById(R.id.publisher);

            holder.bookImage = (ImageView) convertView.findViewById(R.id.book_image);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.bookTitle.setText(book.getTitle());
        holder.bookAuthor.setText(book.getAuthor());
        holder.bookPublisher.setText(book.getPublisher());
        Picasso.with(getContext()).load(book.getImage()).into(holder.bookImage);

        return convertView;
    }

    static class ViewHolder {

        TextView bookTitle;
        TextView bookAuthor;
        TextView bookPublisher;
        ImageView bookImage;
    }
}