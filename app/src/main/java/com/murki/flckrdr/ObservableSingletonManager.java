package com.murki.flckrdr;

import android.util.SparseArray;

import rx.Observable;

// TODO: Add cache eviction policy
// TODO: Make collection + generic
// TODO: Make thread-safe
public enum ObservableSingletonManager {
    INSTANCE {};

    private SparseArray<Observable> inMemoryCache = new SparseArray<>();

    @SuppressWarnings("unchecked")
    public <T extends Observable> T getRecenPhotosResponseObservable() {
        return (T) inMemoryCache.get(0);
    }

    public void setRecenPhotosResponseObservable(Observable obs) {
        inMemoryCache.put(0, obs);
    }

    public boolean isRecenPhotosResponseObservable() {
        return inMemoryCache.get(0) != null;
    }

    public void removeRecenPhotosResponseObservable() {
        inMemoryCache.delete(0);
    }

}
