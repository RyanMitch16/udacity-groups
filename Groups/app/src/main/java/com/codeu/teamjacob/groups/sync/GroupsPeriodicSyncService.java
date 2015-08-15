package com.codeu.teamjacob.groups.sync;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class GroupsPeriodicSyncService extends Service {

    static final public String BROADCAST_ACTION = "com.codeu.teamjacob.groups.update";

    public static int REQUEST_DELAY = 20*1000;

    public String userKey;

    static Updater updater;


    class Updater extends Thread {

        public boolean isRunning = false;

        @Override
        public void run() {
            super.run();

            isRunning = true;
            while (isRunning) {



                if (!userKey.equals("")) {

                   GroupsSyncAdapter.syncGetGroups(getApplicationContext(), userKey);
                }

                Log.d("Hello",userKey);

                try {
                    Thread.sleep(REQUEST_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }


        public boolean isRunning() {
            return isRunning;
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        updater = new Updater();
        userKey = GroupsSyncAccount.getUserKey(getApplicationContext());
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {

        if (!updater.isRunning()) {
            updater.start();
            updater.isRunning = true;
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public synchronized void onDestroy() {
        super.onDestroy();

        if (updater != null && updater.isRunning) {
            updater.interrupt();
            updater.isRunning = false;
            updater = null;
        }



    }




}
