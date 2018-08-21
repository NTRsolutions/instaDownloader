package com.tuanvn91.instagramdownloader.tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.tuanvn91.instagramdownloader.MainActivity;
import com.tuanvn91.instagramdownloader.R;
import com.tuanvn91.instagramdownloader.adaptor.ImageRecyclerAdaptor;

public class HistoryFragment extends Fragment implements MainActivity.FragmentRefresh {

    ImageView ivSettings;
    private FragmentActivity mContext;
    private RecyclerView rvInsta;
    private AdView adView;

    //DB
//	private DBController dbcon;
    private ImageRecyclerAdaptor imageRecyclerAdaptor;

    public static HistoryFragment newInstance() {
        //Bundle args = new Bundle();
        //args.putString(ARG_PAGE, title);
//		HistoryFragment fragment =
        //fragment.setRetainInstance(true);
        //fragment.setArguments(args);
        return new HistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Tag1", "MoviesFrag");
    }

    @Override
    public void onDestroyView() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroyView();
    }

    @Override
    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        mContext = getActivity();
        adView = new AdView(mContext, "445705105939811_445706462606342", AdSize.BANNER_HEIGHT_50);//ID a Long
        RelativeLayout relativeLayout = rootView.findViewById(R.id.banner);
        relativeLayout.addView(adView);
        adView.loadAd();

        adView.setAdListener(new AdListener() {
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
        //DB
//		dbcon = new DBController(mContext);

        rvInsta = (RecyclerView) rootView.findViewById(R.id.rvInstaImages);
        imageRecyclerAdaptor = new ImageRecyclerAdaptor(mContext);
        rvInsta.setAdapter(imageRecyclerAdaptor);
        rvInsta.setLayoutManager(new LinearLayoutManager(mContext));
        rvInsta.setHasFixedSize(true);
        rvInsta.setItemViewCacheSize(20);
        rvInsta.setDrawingCacheEnabled(true);
        rvInsta.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //rvInsta.s/

        imageRecyclerAdaptor.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void refresh() {
        if (imageRecyclerAdaptor != null) {
            imageRecyclerAdaptor.onRefreshh();
        }
    }
}