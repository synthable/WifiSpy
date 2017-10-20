package com.synthable.wifispy;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.synthable.wifispy.ui.fragment.AccessPointsFragment;
import com.synthable.wifispy.ui.fragment.TagsFragment;

public class MainDrawerActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        FragmentInteraction.OnInteractionListener {

    private static final int PERMISSION_REQUEST_LOCATION = 1;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AccessPointsFragment())
                .commit();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, PERMISSION_REQUEST_LOCATION);
        } else {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(WifiSpyService.isRunning) {
            WifiSpyService.stop(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onNavigationItemSelected(MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);

        switch(item.getItemId()) {
            default:
            case R.id.accessp_points:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AccessPointsFragment())
                        .commit();
                break;
            case R.id.tags:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new TagsFragment())
                        .commit();
                break;
            case R.id.map:
                break;
            case R.id.settings:
                break;
            case R.id.about:
                break;
        }

        return true;
    }

    @Override
    public void onFragemtnSetTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onFragemtnViewTags() {

    }
}
