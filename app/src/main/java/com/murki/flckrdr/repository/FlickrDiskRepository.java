package com.murki.flckrdr.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.f2prateek.rx.preferences.Preference;
import com.f2prateek.rx.preferences.RxSharedPreferences;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import rx.Observable;
import rx.Subscriber;

public class FlickrDiskRepository {

    private final static String RECENT_PHOTOS_RESPONSE_KEY = "com.murki.flckrdr.model.RecentPhotosResponse_key";

    private final RxSharedPreferences rxPreferences;
    private final JsonAdapter<RecentPhotosResponse> flickrPhotosJsonAdapter;

    public FlickrDiskRepository(Context context) {
        rxPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context));
        Moshi moshi = new Moshi.Builder().build();
        flickrPhotosJsonAdapter = moshi.adapter(RecentPhotosResponse.class);
    }

    @RxLogObservable
    public Observable<Void> savePhotos(final RecentPhotosResponse photos) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).set(photos);
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }

    public void savePhotosNonRx(RecentPhotosResponse photos) {
        rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).set(photos);
    }

    @RxLogObservable
    public Observable<RecentPhotosResponse> getRecentPhotos() {
        return Observable.create(new Observable.OnSubscribe<RecentPhotosResponse>() {
            @Override
            public void call(Subscriber<? super RecentPhotosResponse> subscriber) {
                try {
                    RecentPhotosResponse photos = rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).get();
                    subscriber.onNext(photos);
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }

    private final Preference.Adapter<RecentPhotosResponse> flickrPhotosAdapter = new Preference.Adapter<RecentPhotosResponse>() {
        @Override
        public RecentPhotosResponse get(@NonNull String key, @NonNull SharedPreferences sharedPreferences) {
            RecentPhotosResponse photos = null;
            try {
                String serializedPhotoList = sharedPreferences.getString(key, "");
                photos = flickrPhotosJsonAdapter.fromJson(serializedPhotoList);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: Pass error to observable chain
            }
            return photos;
        }

        @Override
        public void set(@NonNull String key, @NonNull RecentPhotosResponse flickrPhotos, @NonNull SharedPreferences.Editor editor) {
            String serializedPhotoList = flickrPhotosJsonAdapter.toJson(flickrPhotos);
            editor.putString(key, serializedPhotoList);
        }
    };

}