package com.synthable.wifispy.provider;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.wifi.ScanResult;

public class AccessPoint {

	private long id;
	private String bssid;
	private String ssid;
	private String capabilities;
	private int frequency;
	private int strength;
	private double lat;
	private double lng;

	public AccessPoint() {
	}

	public AccessPoint(ScanResult result) {
		bssid = result.BSSID;
    	ssid = result.SSID;
    	capabilities = result.capabilities;
    	frequency = result.frequency;
    	strength = result.level;
	}

	public AccessPoint(Cursor cursor) {
		bssid = cursor.getString(cursor.getColumnIndex(AccessPoints.Columns.BSSID));
		ssid = cursor.getString(cursor.getColumnIndex(AccessPoints.Columns.SSID));
		capabilities = cursor.getString(cursor.getColumnIndex(AccessPoints.Columns.CAPABILITIES));
		frequency = cursor.getInt(cursor.getColumnIndex(AccessPoints.Columns.FREQUENCY));
		strength = cursor.getInt(cursor.getColumnIndex(AccessPoints.Columns.STRENGHT));
		lat = cursor.getLong(cursor.getColumnIndex(AccessPoints.Columns.LAT));
		lng = cursor.getLong(cursor.getColumnIndex(AccessPoints.Columns.LNG));
	}

	public ContentValues toContentValues() {
		ContentValues v = new ContentValues();
		v.put(AccessPoints.Columns.BSSID, bssid);
		v.put(AccessPoints.Columns.SSID, ssid);
		v.put(AccessPoints.Columns.CAPABILITIES, capabilities);
		v.put(AccessPoints.Columns.FREQUENCY, frequency);
		v.put(AccessPoints.Columns.STRENGHT, strength);
		v.put(AccessPoints.Columns.LAT, lat);
		v.put(AccessPoints.Columns.LNG, lng);
		return v;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getBssid() {
		return bssid;
	}
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public String getCapabilities() {
		return capabilities;
	}
	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public int getStrength() {
		return strength;
	}
	public void setStrength(int strength) {
		this.strength = strength;
	}
	public double getLat() {
		return lat;
	}
	public void setLat(double lat) {
		this.lat = lat;
	}
	public double getLng() {
		return lng;
	}
	public void setLng(double lng) {
		this.lng = lng;
	}
}
