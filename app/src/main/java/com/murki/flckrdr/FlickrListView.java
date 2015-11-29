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
import com.murki.flckrdr.viewmodels.FlickrCardVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Result;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
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

        Log.i(CLASSNAME, "onFinishInflate() - recyclerView setup");

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.flickr_swipe_refresh);
//        swipeRefreshLayout.setColorSchemeResources(R.color.accent);
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

    private void loadResults(boolean useCacheIfAvaliable) {
        swipeRefreshLayout.setRefreshing(true);
        // TODO: Move abstraction of caching down to repository/service level
        Observable<Result<RecentPhotosResponse>> recentPhotosObservable;
        if (useCacheIfAvaliable && ObservableSingletonManager.INSTANCE.isRecenPhotosResponseObservable()) {
            Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - fetching cached observable");
            recentPhotosObservable = ObservableSingletonManager.INSTANCE.getRecenPhotosResponseObservable();
        } else {
            Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - creating and caching observable");
            FlickrRepository flickrRepository = new FlickrRepository();
            recentPhotosObservable = flickrRepository.getRecentPhotos()
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            ObservableSingletonManager.INSTANCE.setRecenPhotosResponseObservable(recentPhotosObservable);
        }

        Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - subscribing to observable");
        subscriptions.add(recentPhotosObservable.subscribe(flickrRecentPhotosCallback, flickrRecentPhotosErrorCallback));
    }

    private final Action1<Result<RecentPhotosResponse>> flickrRecentPhotosCallback = new Action1<Result<RecentPhotosResponse>>() {
        @Override
        public void call(Result<RecentPhotosResponse> recentPhotosResponseResult) {
            List<FlickrPhoto> photoList = recentPhotosResponseResult.response().body().photos.photo;
            Log.i(CLASSNAME, "flickrRecentPhotosCallback.call() - Response list size=" + photoList.size());
            List<FlickrCardVM> flickrCardVMs = new ArrayList<>(photoList.size());
            for (FlickrPhoto photo : photoList) {
                flickrCardVMs.add(new FlickrCardVM(photo.title, photo.url_n));
            }
            swipeRefreshLayout.setRefreshing(false);
            // specify an adapter
            RecyclerView.Adapter mAdapter = new FlickrListAdapter(flickrCardVMs);
            recyclerView.setAdapter(mAdapter);
        }
    };

    private final Action1<Throwable> flickrRecentPhotosErrorCallback = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(CLASSNAME, "flickrRecentPhotosErrorCallback.call() - ERROR - uncaching observable", throwable);
            swipeRefreshLayout.setRefreshing(false);
            ObservableSingletonManager.INSTANCE.removeRecenPhotosResponseObservable();
        }
    };

}
