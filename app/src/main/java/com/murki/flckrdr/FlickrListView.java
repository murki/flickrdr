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
import com.murki.flckrdr.service.FlickrGetRecentPhotosService;
import com.murki.flckrdr.service.ReusableObservableService;
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
        loadResults();
    }

    @Override
    public void onRefresh() {
        loadResults();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i(CLASSNAME, "onDetachedFromWindow() - unsubscribing from all subscribed observables");
        subscriptions.unsubscribe();
    }

    private void loadResults() {
        swipeRefreshLayout.setRefreshing(true);

        FlickrGetRecentPhotosService service = new FlickrGetRecentPhotosService();
        Observable<List<FlickrCardVM>> recentPhotosObservable = service.call().map(FlickrCardVM.flickrApiToVmMapping);
        subscriptions.add(recentPhotosObservable.subscribe(flickrRecentPhotosOnNext, flickrRecentPhotosOnError));

        Log.i(CLASSNAME, "loadResults() - fetched observable + subscribed to it");
    }

    private final Action1<List<FlickrCardVM>> flickrRecentPhotosOnNext = new Action1<List<FlickrCardVM>>() {
        @Override
        public void call(List<FlickrCardVM> flickrCardVMs) {
            Log.i(CLASSNAME, "flickrRecentPhotosOnNext.call() - Displaying card VMs in Adapter");
            swipeRefreshLayout.setRefreshing(false);
            // specify an adapter
            RecyclerView.Adapter mAdapter = new FlickrListAdapter(flickrCardVMs);
            recyclerView.setAdapter(mAdapter);
        }
    };

    // TODO: move this onError logic to Service
    private final Action1<Throwable> flickrRecentPhotosOnError = new Action1<Throwable>() {
        @Override
        public void call(Throwable throwable) {
            Log.e(CLASSNAME, "flickrRecentPhotosOnError.call() - ERROR - uncaching observable", throwable);
            swipeRefreshLayout.setRefreshing(false);
            ObservableSingletonManager.INSTANCE.removeObservable(ReusableObservableService.FLICKR_GET_RECENT_PHOTOS);
        }
    };

}
