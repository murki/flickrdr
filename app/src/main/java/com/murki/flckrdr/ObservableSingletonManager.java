package com.murki.flckrdr;

import com.murki.flckrdr.model.RecentPhotosResponse;

import retrofit.Result;
import rx.Observable;

// TODO: Add cache eviction policy
public enum ObservableSingletonManager {
    INSTANCE {};

    private Observable<Result<RecentPhotosResponse>> recenPhotosResponseObservable;

    public Observable<Result<RecentPhotosResponse>> getRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable;
    }

    public void setRecenPhotosResponseObservable(Observable<Result<RecentPhotosResponse>> obs) {
        recenPhotosResponseObservable = obs;
    }

    public boolean isRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable != null;
    }

    public void removeRecenPhotosResponseObservable() {
        recenPhotosResponseObservable = null;
    }

}
