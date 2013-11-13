package com.synthable.wifispy;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WifiSpyService extends Service {

	public static final String TAG = "WIFISPY_SERVICE";
	public static boolean isRunning = false;

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		Log.v("SERVICE", "onCreate()");

		/** Turn on Wifi if not already **/
		mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
        	mWifiManager.setWifiEnabled(true);
        }

        /** Register receiver for wifi scan results and start scanning **/
        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mWifiReceiver);
		isRunning = false;
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

	class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> aps = mWifiManager.getScanResults();
            for(int i = 0; i < aps.size(); i++) {
                Log.v("SCANRESULT", aps.get(i).SSID);
            }
        }
    }
}
