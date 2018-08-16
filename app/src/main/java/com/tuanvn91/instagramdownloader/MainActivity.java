package com.tuanvn91.instagramdownloader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tuanvn91.instagramdownloader.adaptor.TabsPagerAdapter;
import com.tuanvn91.instagramdownloader.service.MyService;
import com.tuanvn91.instagramdownloader.tabs.DownloadFragment;
import com.tuanvn91.instagramdownloader.tabs.HistoryFragment;

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
    private InterstitialAd interstitialAd;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initNavDrawerToggel();
        Utilities.getStoragePermission(MainActivity.this);
        getAppConfig();

        interstitialAd = new InterstitialAd(this, "525342907902932_525343574569532");
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {

            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });

        interstitialAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
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
                Random rad = new Random();
                int i = rad.nextInt(10);
                if (i < 4 && interstitialAd.isAdLoaded()) {
                    interstitialAd.show();
                }
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
        mPrefs = getSharedPreferences("adsserver_ringtone", 0);
        String uuid;
        if (mPrefs.contains("uuid")) {
            uuid = mPrefs.getString("uuid", UUID.randomUUID().toString());
        } else {
            uuid = UUID.randomUUID().toString();
            mPrefs.edit().putString("uuid", "insta" + uuid).apply();
        }

        OkHttpClient client = new OkHttpClient();
        Request okRequest = new Request.Builder()
                .url(Utilities.URL_CONFIG)
                .build();

        client.newCall(okRequest).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
//                Gson gson = new GsonBuilder().create();//"{\"delayAds\":24,\"delayService\":24,\"idFullService\":\"/21617015150/734252/21734366950\",\"intervalService\":10,\"percentAds\":50}";//
//                Log.d("caomui111111", result + "");
//                AdsConfig jsonConfig = gson.fromJson(result, AdsConfig.class);
                JsonObject jsonObject = new JsonParser().parse(response.body().string()).getAsJsonObject();

                mPrefs.edit().putInt("intervalService", jsonObject.get("intervalService").getAsInt()).apply();
                mPrefs.edit().putString("idFullService", jsonObject.get("idFullService").getAsString()).apply();
                mPrefs.edit().putInt("delayService", jsonObject.get("delayService").getAsInt()).apply();
//                Log.d("caomui",jsonObject.get("idFullService").getAsString());
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
