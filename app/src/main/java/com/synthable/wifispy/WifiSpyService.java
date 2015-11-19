package com.synthable.wifispy;

import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synthable.wifispy.provider.DbContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;

import android.app.NotificationManager;
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
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int UPDATE_INTERVAL = 1000 * 5;
    private static final int FASTEST_INTERVAL = 1000 * 1;

    public static final String TAG = "WIFISPY_SERVICE";
    public static boolean isRunning = false;
    public static boolean isScanning = false;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private double mLatitude;
    private double mLongitude;

    private WifiManager mWifiManager;
    private WifiReceiver mWifiReceiver;

    private NotificationManager mNotificationManager;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isRunning = true;

        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /** Turn on Wifi if not already **/
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

/*        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("WifiSpy is scanning...")
                        .setContentText("Click to view")
                        .setOngoing(true);

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
        isRunning = false;
        isScanning = false;

        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
        }

//        mNotificationManager.cancelAll();
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

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

            startScanning();
        }

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLatitude = mLastLocation.getLatitude();
        mLongitude = mLastLocation.getLongitude();

        startScanning();
    }

    private void startScanning() {
        if(!isScanning) {
            isScanning = true;
            mWifiManager.startScan();
        }
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
                        ap.setLat(mLatitude);
                        ap.setLng(mLongitude);
                    }
                } else {
                    ap.setLat(mLatitude);
                    ap.setLng(mLongitude);
                }
                cursor.close();

                getContentResolver().insert(AccessPoints.URI, ap.toContentValues());
            }
        }
    }
}