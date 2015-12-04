package com.murki.flckrdr.service;

import android.util.Log;

import com.murki.flckrdr.ObservableSingletonManager;
import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.murki.flckrdr.repository.FlickrRepository;
import com.murki.flckrdr.viewmodel.FlickrCardVM;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class FlickrGetRecentPhotosService extends ReusableObservableService {

    private static final String CLASSNAME = FlickrGetRecentPhotosService.class.getCanonicalName();

    private final FlickrRepository flickrRepository;

    public FlickrGetRecentPhotosService(FlickrRepository flickrRepository) {
        this.flickrRepository = flickrRepository;
    }

    public FlickrGetRecentPhotosService() {
        this(new FlickrRepository());
    }

    @Override
    public @ServiceMethod int getMethodKey() {
        return FLICKR_GET_RECENT_PHOTOS;
    }

    @Override
    public Observable<RecentPhotosResponse> call() {
        Observable<RecentPhotosResponse> recentPhotosObservable = ObservableSingletonManager.INSTANCE.getObservable(getMethodKey());

        if (recentPhotosObservable == null) {
            recentPhotosObservable = flickrRepository
                    .getRecentPhotos()
                    .cache()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());

            ObservableSingletonManager.INSTANCE.putObservable(getMethodKey(), recentPhotosObservable);
        }

        return recentPhotosObservable;
    }

}
