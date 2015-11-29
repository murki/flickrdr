package com.murki.flckrdr;

import com.murki.flckrdr.viewmodels.FlickrCardVM;

import java.util.List;

import rx.Observable;

// TODO: Add cache eviction policy
// TODO: Make collection + generic
// TODO: Make thread-safe
public enum ObservableSingletonManager {
    INSTANCE {};

    private Observable<List<FlickrCardVM>> recenPhotosResponseObservable;

    public Observable<List<FlickrCardVM>> getRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable;
    }

    public void setRecenPhotosResponseObservable(Observable<List<FlickrCardVM>> obs) {
        recenPhotosResponseObservable = obs;
    }

    public boolean isRecenPhotosResponseObservable() {
        return recenPhotosResponseObservable != null;
    }

    public void removeRecenPhotosResponseObservable() {
        recenPhotosResponseObservable = null;
    }

}
