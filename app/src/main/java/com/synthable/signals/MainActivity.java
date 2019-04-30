package com.synthable.signals;

import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.synthable.signals.ui.fragment.AccessPointsFragment;
import com.synthable.signals.ui.fragment.TagsFragment;

public class MainActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener,
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
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AccessPointsFragment())
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                Toast.makeText(this, R.string.settings, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_help:
                Toast.makeText(this, R.string.help, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_about:
                Toast.makeText(this, R.string.about, Toast.LENGTH_SHORT).show();
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
    public void onBackStackChanged() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(
                getSupportFragmentManager().getBackStackEntryCount() > 0
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        getSupportFragmentManager().popBackStack();
        return super.onSupportNavigateUp();
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
