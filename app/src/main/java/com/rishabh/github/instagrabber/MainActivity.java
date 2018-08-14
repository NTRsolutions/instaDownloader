package com.rishabh.github.instagrabber;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.rishabh.github.instagrabber.adaptor.TabsPagerAdapter;
import com.rishabh.github.instagrabber.tabs.DownloadFragment;
import com.rishabh.github.instagrabber.tabs.HistoryFragment;

public class MainActivity extends AppCompatActivity implements
        DownloadFragment.OnPostDownload {
    public ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    private TabsPagerAdapter mAdapter;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavDrawerToggel();
        Utilities.getStoragePermission(MainActivity.this);

    }

    private void initNavDrawerToggel() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initilization
        viewPager = findViewById(R.id.pager);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Download"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));

        tabLayout.setupWithViewPager(viewPager);


    }


    private void callInstagram() {
        String apppackage = "com.instagram.android";
        try {
            Intent i = getPackageManager().getLaunchIntentForPackage(apppackage);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "You have not installed Instagram", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void refreshList() {
        Fragment fragment = mAdapter.getFragment(1);
        if (fragment != null)
            ((HistoryFragment) fragment).refresh();

    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.instalogo:

                callInstagram();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public interface FragmentRefresh {
        void refresh();
    }
}
