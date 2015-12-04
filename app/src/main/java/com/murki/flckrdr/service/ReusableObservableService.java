package com.murki.flckrdr.service;

import android.support.annotation.IntDef;

import com.murki.flckrdr.model.RecentPhotosResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import rx.Observable;

public abstract class ReusableObservableService {

    @IntDef({FLICKR_GET_RECENT_PHOTOS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ServiceMethod {}

    public static final int FLICKR_GET_RECENT_PHOTOS = 0;

    public abstract int getMethodKey();

    public abstract Observable<RecentPhotosResponse> call();
}
