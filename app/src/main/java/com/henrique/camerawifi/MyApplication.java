package com.henrique.camerawifi;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.ref.WeakReference;

/**
 * Created by henrique.pereira on 28/06/2017.
 */

public class MyApplication extends Application {

    private static final ServiceConnection BridgeServiceConnnection;
    private static WeakReference<BridgeService> mBsRef;

    public void onCreate() {
        super.onCreate();
        Intent intent = new Intent();
        intent.setClass(this, BridgeService.class);
        startService(intent);
        bindService(intent, BridgeServiceConnnection, 1);
    }

    static {
        BridgeServiceConnnection = new C03651();
    }

    static class C03651 implements ServiceConnection {
        C03651() {
        }

        public void onServiceDisconnected(ComponentName name) {
            if (MyApplication.mBsRef != null) {
                MyApplication.mBsRef.clear();
            }
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            if (MyApplication.mBsRef != null) {
                MyApplication.mBsRef.clear();
            }
            MyApplication.mBsRef = new WeakReference(((BridgeService.ControllerBinder) service).getBridgeService());
        }
    }
}
