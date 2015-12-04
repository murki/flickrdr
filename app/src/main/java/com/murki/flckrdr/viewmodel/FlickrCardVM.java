package com.murki.flckrdr.viewmodel;

import android.databinding.BindingAdapter;
import android.util.Log;
import android.widget.ImageView;

import com.murki.flckrdr.BuildConfig;
import com.murki.flckrdr.model.FlickrPhoto;
import com.murki.flckrdr.model.RecentPhotosResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class FlickrCardVM {
    private static final String CLASSNAME = FlickrCardVM.class.getCanonicalName();

    private final String title;
    private final String imageUrl;

    public FlickrCardVM(String title, String imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso pic = Picasso.with(view.getContext());
        if (BuildConfig.DEBUG) {
            pic.setLoggingEnabled(true);
        }
        pic.load(url).into(view);
    }

    // TODO: Move to lazy-load singleton class
    public static final Func1<RecentPhotosResponse, List<FlickrCardVM>> flickrApiToVmMapping = new Func1<RecentPhotosResponse, List<FlickrCardVM>>() {
        @Override
        public List<FlickrCardVM> call(RecentPhotosResponse recentPhotosResponse) {
            List<FlickrPhoto> photoList = recentPhotosResponse.photos.photo;
            Log.i(CLASSNAME, "flickrApiToVmMapping.call() - Response list size=" + photoList.size());
            List<FlickrCardVM> flickrCardVMs = new ArrayList<>(photoList.size());
            for (FlickrPhoto photo : photoList) {
                flickrCardVMs.add(new FlickrCardVM(photo.title, photo.url_n));
            }
            return flickrCardVMs;
        }
    };
}
