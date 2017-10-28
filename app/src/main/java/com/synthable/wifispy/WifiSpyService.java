package com.synthable.wifispy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synthable.wifispy.provider.DbContract.AccessPointTags;
import com.synthable.wifispy.provider.DbContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;
import com.synthable.wifispy.provider.model.AccessPointTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WifiSpyService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int UPDATE_INTERVAL = 1000 * 5;
    private static final int FASTEST_INTERVAL = 1000 * 1;

    public static final String EXTRA_TAG_IDS = "tag_ids";
    public static final String CHANNEL = "WIFISPY_SERVICE";

    public static boolean isRunning = false;
    public static boolean isScanning = false;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private HashSet<Long> mTagIds;
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
        mGoogleApiClient.connect();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        /** Turn on Wifi if not already **/
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL)
                .setChannelId(CHANNEL)
                .setSound(null)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.we_are_scanning))
                .setContentText(getString(R.string.click_to_view))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .build();

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL, getString(R.string.notification_channel_scanning), NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(channel);
        }

        startForeground(1, notification);
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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTagIds = (HashSet<Long>) intent.getSerializableExtra(EXTRA_TAG_IDS);
        return super.onStartCommand(intent, flags, startId);
    }

    public static void start(Context c, HashSet<Long> tagIds) {
        Intent i = new Intent(c, WifiSpyService.class);
        i.putExtra(EXTRA_TAG_IDS, tagIds);
        ContextCompat.startForegroundService(c, i);
    }

    public static void stop(Context c) {
        Intent i = new Intent(c, WifiSpyService.class);
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
        Log.d("onConnectionSuspended", i +"");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("onConnectionFailed", connectionResult.getErrorMessage());
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

                ArrayList<ContentValues> values = new ArrayList<>();
                for(Long id : mTagIds) {
                    AccessPointTag apTag = new AccessPointTag(ap.getBssid(), id);
                    values.add(apTag.toContentValues());
                    getContentResolver().insert(AccessPointTags.URI, apTag.toContentValues());
                }
            }
        }
    }
}