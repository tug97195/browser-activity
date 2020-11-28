package edu.temple.webbrowserapp;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PageListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<PageViewerFragment> pages;

    // Takes list of pages and uses titles for Views
    public PageListAdapter (Context context, ArrayList<PageViewerFragment> pages) {
        this.context = context;
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public Object getItem(int position) {
        return pages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView;

        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        } else {
            textView = new TextView(context);
            textView.setPadding(5,8,8,5);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(14);
        }

        textView.setText(pages.get(position).getTitle());
        return textView;
    }
}