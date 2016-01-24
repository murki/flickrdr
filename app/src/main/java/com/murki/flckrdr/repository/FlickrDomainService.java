package com.murki.flckrdr.repository;

import android.content.Context;
import android.util.Log;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.murki.flckrdr.viewmodel.FlickrApiToVmMapping;
import com.murki.flckrdr.viewmodel.FlickrCardVM;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
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
        return Observable.merge(
                flickrDiskRepository.getRecentPhotos(),
                flickrApiRepository.getRecentPhotos().doOnNext(new Action1<RecentPhotosResponse>() {
                    @Override
                    public void call(RecentPhotosResponse recentPhotosResponse) {
                        flickrDiskRepository.savePhotosNonRx(recentPhotosResponse); // TODO: Make it work with chained Observables
                    }
                }))
                .filter(new Func1<RecentPhotosResponse, Boolean>() {
                    @Override
                    public Boolean call(RecentPhotosResponse recentPhotosResponse) {
                        Log.d(FlickrDomainService.class.getCanonicalName(), "Frodo (fake) => Call finished! Filtering results. recentPhotosResponse=" + recentPhotosResponse);
                        // TODO: implemente right filter:
                        // filter it, if timestamp of new arrived (emission) data is less than timestamp of already displayed data — ignore it.
                        // filter(data -> data.timeStamp >= displayedData.timeStamp)
                        return recentPhotosResponse != null && recentPhotosResponse.isUpToDate();
                    }
                })
                .map(FlickrApiToVmMapping.instance());
    }
}
