package com.codeu.teamjacob.groups.ui;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class EntryAdapter<T> extends ArrayAdapter<T> {

    //The context of the adapter
    public Activity context;

    public EntryAdapter(Activity context, @LayoutRes int resource){
        super(context, resource, new ArrayList<T>());
        this.context = context;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = newView(context, position, parent);}
        if (getItem(position) != null){
            convertView = bindView(context, position, convertView);}

        return convertView;

    }

    public abstract View bindView(Context context, int position, View view);

    public abstract View newView(Context context, int position, ViewGroup parent);

    /**
     * Sets the apater to the list of entries
     * @param entries   the entries to set as the adapter
     */
    public void set(T[] entries){
        //Clear all the entries from the list
        super.clear();
        if (entries != null) {
            super.addAll(entries);
        }
    }

    public void add(T entry){
        super.add(entry);
    }
}
