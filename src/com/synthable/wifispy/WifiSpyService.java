package com.synthable.wifispy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WifiSpyService extends Service {

	public static final String TAG = "WIFISPY_SERVICE";

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		WifiSpy.sServiceIsRunning = true;
		Log.v("SERVICE", "onCreate()");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		WifiSpy.sServiceIsRunning = false;
		Log.v("SERVICE", "onDestroy()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("SERVICE", "onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	public static void start(Context c) {
		Intent i = new Intent(c, WifiSpyService.class);
		i.addCategory(TAG);
		c.startService(i);
	}

	public static void stop(Context c) {
		Intent i = new Intent(c, WifiSpyService.class);
		i.addCategory(TAG);
		c.stopService(i);
	}
}
