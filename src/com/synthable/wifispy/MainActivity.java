package com.synthable.wifispy;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.view.Menu;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int code = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(code != ConnectionResult.SUCCESS) {
			GooglePlayServicesUtil.getErrorDialog(code, this, 0, new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					finish();
				}
			}).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
