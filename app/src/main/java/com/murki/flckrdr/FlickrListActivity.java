package com.murki.flckrdr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.murki.flckrdr.model.FlickrPhotos;
import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.repository.FlickrRepository;
import com.murki.flckrdr.viewmodels.FlickrCardVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class FlickrListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_list);

        final RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_flickr_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
