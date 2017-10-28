package com.synthable.wifispy;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.synthable.wifispy.ui.fragment.AccessPointsFragment;
import com.synthable.wifispy.ui.fragment.TagsFragment;

public class MainActivity extends AppCompatActivity implements
        FragmentInteraction.OnInteractionListener {

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_LOCATION);
        } else {

        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AccessPointsFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        /*if(WifiSpyService.isRunning) {
            WifiSpyService.stop(this);
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_help:
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_about:
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                if( grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish();
                }
            }
        }
    }

    @Override
    public void onFragemtnSetTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragemtnViewTags() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TagsFragment())
                .addToBackStack(null)
                .commit();
    }
}
