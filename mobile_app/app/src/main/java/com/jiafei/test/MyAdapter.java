package com.jiafei.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class MyAdapter extends PagerAdapter {

    private List<View> listView;

    private List<String> listTabTitle;

    private Context context;

    public MyAdapter(List<View> listView, List<String> listTabTitle, Context context) {
        this.listView = listView;
        this.listTabTitle = listTabTitle;
        this.context = context;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return listTabTitle.get(position);
    }

    @Override
    public int getCount() {
        return listView.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(listView.get(position),0);
        return listView.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(listView.get(position));
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
