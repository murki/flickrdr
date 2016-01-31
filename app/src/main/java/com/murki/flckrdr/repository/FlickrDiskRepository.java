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
import com.squareup.moshi.Types;

import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Timestamped;

public class FlickrDiskRepository {

    private final static String RECENT_PHOTOS_RESPONSE_KEY = "com.murki.flckrdr.model.RecentPhotosResponse_key";

    private final RxSharedPreferences rxPreferences;
    private final JsonAdapter<Timestamped<RecentPhotosResponse>> flickrPhotosJsonAdapter;

    public FlickrDiskRepository(Context context) {
        rxPreferences = RxSharedPreferences.create(PreferenceManager.getDefaultSharedPreferences(context));
        Moshi moshi = new Moshi.Builder().build();
        Type adapterType = Types.newParameterizedType(Timestamped.class, RecentPhotosResponse.class);
        flickrPhotosJsonAdapter = moshi.adapter(adapterType);
    }

//    @RxLogObservable
//    public Observable<Void> savePhotos(final RecentPhotosResponse photos) {
//        return Observable.create(new Observable.OnSubscribe<Void>() {
//            @Override
//            public void call(Subscriber<? super Void> subscriber) {
//                try {
//                    rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).set(photos);
//                    subscriber.onNext(null);
//                    subscriber.onCompleted();
//                } catch (Exception ex) {
//                    subscriber.onError(ex);
//                }
//            }
//        });
//    }

    public void savePhotos(Timestamped<RecentPhotosResponse> photos) {
        rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).set(photos);
    }

    @RxLogObservable
    public Observable<Timestamped<RecentPhotosResponse>> getRecentPhotos() {
        return Observable.create(new Observable.OnSubscribe<Timestamped<RecentPhotosResponse>>() {
            @Override
            public void call(Subscriber<? super Timestamped<RecentPhotosResponse>> subscriber) {
                try {
                    Timestamped<RecentPhotosResponse> photos = rxPreferences.getObject(RECENT_PHOTOS_RESPONSE_KEY, flickrPhotosAdapter).get();
                    subscriber.onNext(photos);
                    subscriber.onCompleted();
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        });
    }

    private final Preference.Adapter<Timestamped<RecentPhotosResponse>> flickrPhotosAdapter = new Preference.Adapter<Timestamped<RecentPhotosResponse>>() {
        @Override
        public Timestamped<RecentPhotosResponse> get(@NonNull String key, @NonNull SharedPreferences sharedPreferences) {
            Timestamped<RecentPhotosResponse> photos = null;
            try {
                String serializedPhotoList = sharedPreferences.getString(key, "");
                photos = flickrPhotosJsonAdapter.fromJson(serializedPhotoList);
            } catch (Exception e) {
                e.printStackTrace(); // TODO: Pass error to observable chain
            }
            return photos;
        }

        @Override
        public void set(@NonNull String key, @NonNull Timestamped<RecentPhotosResponse> flickrPhotos, @NonNull SharedPreferences.Editor editor) {
            String serializedPhotoList = flickrPhotosJsonAdapter.toJson(flickrPhotos);
            editor.putString(key, serializedPhotoList);
        }
    };

}