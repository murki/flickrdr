package com.murki.flckrdr;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.murki.flckrdr.repository.FlickrRepository;
import com.murki.flckrdr.viewmodel.FlickrCardVM;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class FlickrListView extends RelativeLayout implements SwipeRefreshLayout.OnRefreshListener {

    private static final String CLASSNAME = FlickrListView.class.getCanonicalName();
    private final CompositeSubscription subscriptions = new CompositeSubscription();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

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

        Log.i(CLASSNAME, "onFinishInflate() - setup views");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.flickr_swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // TODO: Distinguish between rotation and navigation
        loadResults(true);
    }

    @Override
    public void onRefresh() {
        loadResults(false);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(CLASSNAME, "onDetachedFromWindow() - unsubscribing from all subscribed observables");
        subscriptions.unsubscribe();
    }

    private void loadResults(boolean useCacheIfAvailable) {
        swipeRefreshLayout.setRefreshing(true);
        // TODO: Move abstraction of caching down to repository/service level
        Observable<List<FlickrCardVM>> recentPhotosObservable;
        recentPhotosObservable = ObservableSingletonManager.INSTANCE.getObservable(ObservableSingletonManager.FLICKR_GET_RECENT_PHOTOS);
        if (!useCacheIfAvailable || recentPhotosObservable == null) {
            Log.i(CLASSNAME, "loadResults(" + useCacheIfAvailable + ") - creating and caching observable");
            FlickrRepository flickrRepository = new FlickrRepository();
            recentPhotosObservable = flickrRepository
                    .getRecentPhotos()
                    .map(flickrApiToVmMapping)
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            ObservableSingletonManager.INSTANCE.putObservable(ObservableSingletonManager.FLICKR_GET_RECENT_PHOTOS, recentPhotosObservable);
        }

        Log.i(CLASSNAME, "loadResults(" + useCacheIfAvailable + ") - subscribing to observable");
        subscriptions.add(recentPhotosObservable.subscribe(flickrRecentPhotosOnNext, flickrRecentPhotosOnError));
    }

    private final Func1<RecentPhotosResponse, List<FlickrCardVM>> flickrApiToVmMapping = new Func1<RecentPhotosResponse, List<FlickrCardVM>>() {
        @Override
        public List<FlickrCardVM> call(RecentPhotosResponse recentPhotosResponse) {
            List<FlickrPhoto> photoList = recentPhotosResponse.photos.photo;
            Log.i(CLASSNAME, "flickrApiToVmMapping.call() - Response list size=" + photoList.size());
            List<FlickrCardVM> flickrCardVMs = new ArrayList<>(photoList.size());
            for (FlickrPhoto photo : photoList) {
                flickrCardVMs.add(new FlickrCardVM(photo.title, photo.url_n));
            }
            return flickrCardVMs;
        }
    };

    private final Action1<List<FlickrCardVM>> flickrRecentPhotosOnNext = new Action1<List<FlickrCardVM>>() {
        @Override
        public void call(List<FlickrCardVM> flickrCardVMs) {
            Log.i(CLASSNAME, "flickrRecentPhotosOnNext.call() - Displaying card VMs in Adapter");
            swipeRefreshLayout.setRefreshing(false);
            // specify an adapter
//            RecyclerView.Adapter mAdapter = new FlickrListAdapter(flickrCardVMs);
//            recyclerView.setAdapter(mAdapter);
        }
    };

    private final Action1<Throwable> flickrRecentPhotosOnError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(CLASSNAME, "flickrRecentPhotosOnError.call() - ERROR - uncaching observable", throwable);
            swipeRefreshLayout.setRefreshing(false);
            ObservableSingletonManager.INSTANCE.removeObservable(ObservableSingletonManager.FLICKR_GET_RECENT_PHOTOS);
        }
    };

}
