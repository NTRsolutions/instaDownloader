package com.tuanvn91.instagramdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tuanvn91.instagramdownloader.Model.AdsConfig;
import com.tuanvn91.instagramdownloader.adaptor.TabsPagerAdapter;
import com.tuanvn91.instagramdownloader.service.MyService;
import com.tuanvn91.instagramdownloader.tabs.DownloadFragment;
import com.tuanvn91.instagramdownloader.tabs.HistoryFragment;
import com.tuanvn91.instagramdownloader.utils.AppConstants;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        DownloadFragment.OnPostDownload {
    public ViewPager viewPager;
    Toolbar toolbar;
    TabLayout tabLayout;
    private TabsPagerAdapter mAdapter;

    private SharedPreferences mPrefs;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavDrawerToggel();
        Utilities.getStoragePermission(MainActivity.this);
        getAppConfig();


    }


    private void initNavDrawerToggel() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initilization
        viewPager = findViewById(R.id.pager);

        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Download"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));

        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
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

    private void getAppConfig() {
        mPrefs = getSharedPreferences("adsserver", 0);
        String uuid;
        if (mPrefs.contains("uuid")) {
            uuid = mPrefs.getString("uuid", UUID.randomUUID().toString());
        } else {
            uuid = UUID.randomUUID().toString();
            mPrefs.edit().putString("uuid", "insta"+uuid).commit();
        }

        OkHttpClient client = new OkHttpClient();
        Request okRequest = new Request.Builder()
                .url(AppConstants.URL_CLIENT_CONFIG + "?id_game="+getPackageName())
                .build();

        client.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                Gson gson = new GsonBuilder().create();//"{\"delayAds\":24,\"delayService\":24,\"idFullService\":\"/21617015150/734252/21734366950\",\"intervalService\":10,\"percentAds\":50}";//
                AdsConfig adsConfig = gson.fromJson(response.body().string(), AdsConfig.class);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putInt("intervalService",adsConfig.intervalService);
                editor.putString("idFullService",adsConfig.idFullService);
                editor.putInt("delayService",adsConfig.delayService);
                editor.putInt("delay_report",adsConfig.delay_report);
                editor.putString("idFullFbService",adsConfig.idFullFbService);

                if(!mPrefs.contains("delay_retention"))
                {
                    if(new Random().nextInt(100) < adsConfig.retention)
                    {
                        editor.putInt("delay_retention",adsConfig.delay_retention).commit();
                    }
                    else
                    {
                        editor.putInt("delay_retention",-1);
                    }
                }

                editor.commit();

                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent myIntent = new Intent(MainActivity.this, MyService.class);
                        startService(myIntent);
                    }
                });
            }
        });
    }
}
