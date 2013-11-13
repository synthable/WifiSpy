package com.synthable.wifispy.provider;

import com.synthable.wifispy.provider.WifiSpyContract.AccessPoints;

import android.content.ContentValues;

public class AccessPoint {

	private long id;
	private String bssid;
	private String ssid;
	private String capabilities;
	private int frequency;
	private int strength;
	private long lat;
	private long lng;

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
	public long getLat() {
		return lat;
	}
	public void setLat(long lat) {
		this.lat = lat;
	}
	public long getLng() {
		return lng;
	}
	public void setLng(long lng) {
		this.lng = lng;
	}
}
