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
    private final CompositeSubscription mSubscriptions = new CompositeSubscription();
    private Observable<Result<RecentPhotosResponse>> mRecentPhotosObservable;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshView;

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

        mSwipeRefreshView = (SwipeRefreshLayout) findViewById(R.id.flickr_swipe_refresh);
//        mSwipeRefreshView.setColorSchemeResources(R.color.accent);
        mSwipeRefreshView.setOnRefreshListener(this);

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
        mSubscriptions.unsubscribe();
    }

    private void loadResults(boolean useCacheIfAvaliable) {
        mSwipeRefreshView.setRefreshing(true);
        // TODO: Move abstraction of caching down to repository level
        if (useCacheIfAvaliable && ObservableSingletonManager.INSTANCE.isRecenPhotosResponseObservable()) {
            Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - fetching cached observable");
            mRecentPhotosObservable = ObservableSingletonManager.INSTANCE.getRecenPhotosResponseObservable();
        } else {
            Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - creating and caching observable");
            FlickrRepository flickrRepository = new FlickrRepository();
            mRecentPhotosObservable = flickrRepository.getRecentPhotos()
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            ObservableSingletonManager.INSTANCE.setRecenPhotosResponseObservable(mRecentPhotosObservable);
        }

        Log.i(CLASSNAME, "loadResults(" + useCacheIfAvaliable + ") - subscribing to observable");
        mSubscriptions.add(mRecentPhotosObservable.subscribe(flickrRecentPhotosCallback, flickrRecentPhotosErrorCallback));
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
            mSwipeRefreshView.setRefreshing(false);
            // specify an adapter
            RecyclerView.Adapter mAdapter = new FlickrListAdapter(flickrCardVMs);
            mRecyclerView.setAdapter(mAdapter);
        }
    };

    private final Action1<Throwable> flickrRecentPhotosErrorCallback = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(CLASSNAME, "flickrRecentPhotosErrorCallback.call() - ERROR - uncaching observable", throwable);
            mSwipeRefreshView.setRefreshing(false);
            ObservableSingletonManager.INSTANCE.removeRecenPhotosResponseObservable();
        }
    };

}
