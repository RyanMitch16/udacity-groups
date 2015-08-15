package com.codeu.teamjacob.groups.ui;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public abstract class EntryLoader<T> extends AsyncTaskLoader<T> {

    public EntryLoader(Context context){
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

}
