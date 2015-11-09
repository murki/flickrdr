package com.murki.flckrdr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.FlickrPhotos;
import com.murki.flckrdr.repository.FlickrRepository;
import com.murki.flckrdr.viewmodels.FlickrCardVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FlickrListView extends RelativeLayout {

    private RecyclerView mRecyclerView;

    public FlickrListView(Context context) {
        super(context);
    }

    public FlickrListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlickrListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FlickrListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        // use a linear layout manager
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FlickrRepository flickrRepository = new FlickrRepository();

        Call<FlickrPhotos> flickrPhotos = flickrRepository.getRecentPhotos();

        flickrPhotos.enqueue(new Callback<FlickrPhotos>() {
            @Override
            public void onResponse(Response<FlickrPhotos> response, Retrofit retrofit) {
                List<FlickrCardVM> flickrCardVMs = new ArrayList<>(response.body().photos.photo.size());
                for (FlickrPhoto photo : response.body().photos.photo) {
                    flickrCardVMs.add(new FlickrCardVM(photo.title, photo.url_n));
                }
                // specify an adapter
                RecyclerView.Adapter mAdapter = new FlickrListAdapter(flickrCardVMs);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("ALARMR", "Error", t);
            }
        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
