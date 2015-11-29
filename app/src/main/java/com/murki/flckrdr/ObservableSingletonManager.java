package com.murki.flckrdr;

import com.murki.flckrdr.model.RecentPhotosResponse;

import rx.Observable;

// TODO: Add cache eviction policy
public enum ObservableSingletonManager {
    INSTANCE {};

    private Observable<RecentPhotosResponse> recenPhotosResponseObservable;

    public Observable<RecentPhotosResponse> getRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable;
    }

    public void setRecenPhotosResponseObservable(Observable<RecentPhotosResponse> obs) {
        recenPhotosResponseObservable = obs;
    }

    public boolean isRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable != null;
    }

    public void removeRecenPhotosResponseObservable() {
        recenPhotosResponseObservable = null;
    }

}
