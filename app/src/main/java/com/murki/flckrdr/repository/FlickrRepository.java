package com.murki.flckrdr.repository;

import com.murki.flckrdr.model.RecentPhotosResponse;

import retrofit.Call;
import retrofit.MoshiConverterFactory;
import retrofit.Result;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import retrofit.http.GET;
import rx.Observable;

public class FlickrRepository {

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String API_KEY = "4f721bbafa75bf6d2cb5af54f937bb70";
    private static IFlickrAPI flickrAPI;

    public FlickrRepository() {
        if (flickrAPI == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ENDPOINT)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();

            flickrAPI = retrofit.create(IFlickrAPI.class);
        }
    }

    public Observable<Result<RecentPhotosResponse>> getRecentPhotos() {
        return flickrAPI.getRecentPhotos();
    }

    private interface IFlickrAPI {
        @GET("?method=flickr.photos.getRecent&format=json&nojsoncallback=1&extras=url_n&api_key=" + API_KEY)
        Observable<Result<RecentPhotosResponse>> getRecentPhotos();
    }
}
