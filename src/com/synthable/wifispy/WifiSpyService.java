package com.synthable.wifispy;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;

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
    private Location mCurrentLocation;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		isRunning = true;

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
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
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

	/**
	 * Loop through the scanned result set to check for a known Access Point.
	 * Update the Lat/Long of the Access Point if the signal strength is stronger than last recorded.
	 */
	class WifiReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> results = mWifiManager.getScanResults();
            for(ScanResult result : results) {
            	AccessPoint ap = new AccessPoint(result);

            	/** Check if we already have this Access Point via BSSID **/
            	Cursor cursor = getContentResolver().query(
        			AccessPoints.URI, AccessPoints.PROJECTION,
        			AccessPoints.Columns.BSSID+"=?", new String[]{ ap.getBssid() }, null
        		);
            	if(cursor.getCount() != 0) {
            		cursor.moveToFirst();
                	AccessPoint old = new AccessPoint(cursor);
                	if(ap.getStrength() > old.getStrength()) {
                		ap.setLat((int) (mCurrentLocation.getLatitude() * 1E6));
                		ap.setLng((int) (mCurrentLocation.getLongitude() * 1E6));
                	}
            	} else {
            		ap.setLat((int) (mCurrentLocation.getLatitude() * 1E6));
            		ap.setLng((int) (mCurrentLocation.getLongitude() * 1E6));
            	}
            	cursor.close();

            	getContentResolver().insert(AccessPoints.URI, ap.toContentValues());
            }
        }
    }

	@Override
	public void onLocationChanged(Location location) {
		mCurrentLocation = location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
		mCurrentLocation = mLocationClient.getLastLocation();
	}

	@Override
	public void onDisconnected() {
		
	}
}
