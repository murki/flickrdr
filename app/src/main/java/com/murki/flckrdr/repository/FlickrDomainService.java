package com.murki.flckrdr.repository;

import android.content.Context;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.murki.flckrdr.viewmodel.FlickrApiToVmMapping;
import com.murki.flckrdr.viewmodel.FlickrCardVM;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class FlickrDomainService {

    private final FlickrApiRepository flickrApiRepository;
    private final FlickrDiskRepository flickrDiskRepository;

    public FlickrDomainService(Context context) {
        flickrApiRepository = new FlickrApiRepository(); // TODO: Make Injectable Singleton
        flickrDiskRepository = new FlickrDiskRepository(context); // TODO: Make Injectable Singleton
    }

    @RxLogObservable
    public Observable<List<FlickrCardVM>> getRecentPhotos() {
        return Observable.concat(
                flickrApiRepository.getRecentPhotos(),
                flickrDiskRepository.getRecentPhotos())
                .first(new Func1<RecentPhotosResponse, Boolean>() {
                    @Override
                    public Boolean call(RecentPhotosResponse recentPhotosResponse) {
                        Log.d(FlickrDomainService.class.getCanonicalName(), "Frodo (fake) => First call finished! recentPhotosResponse=" + recentPhotosResponse);
                        return recentPhotosResponse != null && recentPhotosResponse.isUpToDate();
                    }
                })
                .map(FlickrApiToVmMapping.instance());
    }
}
