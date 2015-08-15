package com.codeu.teamjacob.groups.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GroupsSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static GroupsSyncAdapter sGroupsSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sGroupsSyncAdapter == null) {
                sGroupsSyncAdapter = new GroupsSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sGroupsSyncAdapter.getSyncAdapterBinder();
    }
}
