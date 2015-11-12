package com.synthable.wifispy;

import java.util.List;

import com.synthable.wifispy.provider.DbContract.AccessPoints;
import com.synthable.wifispy.provider.model.AccessPoint;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;

public class WifiSpyService extends Service {

    public static final String TAG = "WIFISPY_SERVICE";
    public static boolean isRunning = false;

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

        mWifiReceiver = new WifiReceiver();
        registerReceiver(mWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiReceiver);
        isRunning = false;

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
//                        ap.setLat((int) (mCurrentLocation.getLatitude() * 1E6));
//                        ap.setLng((int) (mCurrentLocation.getLongitude() * 1E6));
                    }
                } else {
//                    ap.setLat((int) (mCurrentLocation.getLatitude() * 1E6));
//                    ap.setLng((int) (mCurrentLocation.getLongitude() * 1E6));
                }
                cursor.close();

                getContentResolver().insert(AccessPoints.URI, ap.toContentValues());
            }
        }
    }
}