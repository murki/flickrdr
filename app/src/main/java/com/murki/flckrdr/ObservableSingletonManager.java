package com.murki.flckrdr;

import android.util.SparseArray;

import com.murki.flckrdr.service.ReusableObservableService;

import rx.Observable;

// TODO: Add cache eviction policy
// TODO: Make collection + generic
// TODO: Make thread-safe
public enum ObservableSingletonManager {
    INSTANCE {};

    private SparseArray<Observable> inMemoryCache = new SparseArray<>();

    @SuppressWarnings("unchecked")
    public <T extends Observable> T getObservable(@ReusableObservableService.ServiceMethod int key) {
        return (T) inMemoryCache.get(key);
    }

    public void putObservable(@ReusableObservableService.ServiceMethod int key,
                              Observable obs) {
        inMemoryCache.put(key, obs);
    }

    public void removeObservable(@ReusableObservableService.ServiceMethod int key) {
        inMemoryCache.delete(key);
    }

}
