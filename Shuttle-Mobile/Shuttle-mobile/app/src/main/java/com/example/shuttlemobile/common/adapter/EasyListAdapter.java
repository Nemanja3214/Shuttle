package com.example.shuttlemobile.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.shuttlemobile.R;

import java.util.List;

/**
 * Basic implementation of <code>BaseAdapter</code>.
 * @param <T> The type of object used in the underlying list.
 */
public abstract class EasyListAdapter<T> extends BaseAdapter {
    /**
     * @return The sequence of objects used by this list.
     * <br/>
     * Note that this method is called in <code>getCount, getItem and getView</code>
     * so it may not perform well depending on the implementation of this method.
     */
    public abstract List<T> getList();

    /**
     * @return The layout inflater of the container for the list using this adapter.
     */
    public abstract LayoutInflater getLayoutInflater();

    /**
     * Modify view objects based on the retrieved type.
     * <br/>
     * Usage: <code>view.findViewById(...).setX(obj.getY());</code>
     * @param view The view from which you find other views.
     * @param obj The object used for displaying data.
     */
    public abstract void applyToView(View view, T obj);

    /**
     *
     * @return ID of the layout for each item in the list.
     */
    public abstract int getListItemLayoutId();

    @Override
    public int getCount() {
        return getList().size();
    }

    @Override
    public Object getItem(int i) {
        return getList().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;

        if (view == null) {
            v = getLayoutInflater().inflate(getListItemLayoutId(), null);
        } else {
            v = view;
        }

        T obj = (T)getItem(i);
        applyToView(v, obj);

        return v;
    }
}
