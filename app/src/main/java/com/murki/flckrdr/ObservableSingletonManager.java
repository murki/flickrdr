package com.murki.flckrdr;

import com.murki.flckrdr.model.RecentPhotosResponse;

import retrofit.Result;
import rx.Observable;

// TODO: Add cache eviction policy
public enum ObservableSingletonManager {
    INSTANCE {};

    private Observable<Result<RecentPhotosResponse>> mRecenPhotosResponseObservable;

    public Observable<Result<RecentPhotosResponse>> getRecenPhotosResponseObservable() {
        return mRecenPhotosResponseObservable;
    }

    public void setRecenPhotosResponseObservable(Observable<Result<RecentPhotosResponse>> obs) {
        mRecenPhotosResponseObservable = obs;
    }

    public boolean isRecenPhotosResponseObservable() {
        return mRecenPhotosResponseObservable != null;
    }

    public void removeRecenPhotosResponseObservable() {
        mRecenPhotosResponseObservable = null;
    }

}
