package com.murki.flckrdr.viewmodels;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.murki.flckrdr.BuildConfig;
import com.squareup.picasso.Picasso;

public class FlickrCardVM {
    private final String mTitle;
    private final String mImageUrl;

    public FlickrCardVM(String title, String imageUrl) {
        this.mTitle = title;
        this.mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    @BindingAdapter({"bind:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Picasso pic = Picasso.with(view.getContext());
        if (BuildConfig.DEBUG) {
            pic.setLoggingEnabled(true);
        }
        pic.load(url).into(view);
    }
}
