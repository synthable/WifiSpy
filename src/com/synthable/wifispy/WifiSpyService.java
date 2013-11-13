package com.synthable.wifispy;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class WifiSpyService extends Service implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener {

    public static final int UPDATE_INTERVAL = 1000 * 5;
    private static final int FASTEST_INTERVAL = 1000 * 1;
	public static final String TAG = "WIFISPY_SERVICE";
	public static boolean isRunning = false;

	private WifiManager mWifiManager;
	private WifiReceiver mWifiReceiver;

    private LocationClient mLocationClient;
    private LocationRequest mLocationRequest;
    private Location mLocation;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;
		Log.v("SERVICE", "onCreate()");

		/** Setup GPS listening **/
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();

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

		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
        }
        mLocationClient.disconnect();

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

	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		
	}
}
