package com.kinstalk.her.skillnews.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class ImageLoader {

    public static void loadImage(Activity activity, String imageUrl, int placeholderResource, final ImageView view) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        Glide.with(activity)
                .load(imageUrl)
                .placeholder(placeholderResource)
                .error(placeholderResource)
                .crossFade()
                .skipMemoryCache(false)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        view.setImageDrawable(resource);
                    }
                });
    }
}
