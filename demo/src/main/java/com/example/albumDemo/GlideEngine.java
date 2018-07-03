package com.example.albumDemo;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.yu.bundles.album.R;
import com.yu.bundles.album.image.ImageEngine;
import com.yu.bundles.album.utils.MethodUtils;

import java.io.File;

/**
 * Created by liyu on 2017/9/30.
 */

public class GlideEngine implements ImageEngine {

    @Override
    public void loadImg(Context context, String path, ImageView imageView, boolean isNeedPlaceHolder, final AlbumEngineLoadListener... listeners) {
        BitmapRequestBuilder builder = Glide.with(context).load(path).asBitmap().error(R.mipmap.mae_album_img_default);
        if(isNeedPlaceHolder){
            builder = builder.placeholder(R.mipmap.mae_album_img_default);
        }
        if(MethodUtils.isNull(listeners)){
            builder.into(imageView);
        } else {
            builder.listener(new GlideRequestListener(listeners[0])).into(imageView);
        }
    }

    @Override
    public void loadGifImg(Context context, String path, ImageView imageView, boolean isNeedPlaceHolder, final AlbumEngineLoadListener... listeners) {
        GifRequestBuilder builder = Glide.with(context).load(path).asGif().priority(Priority.HIGH).diskCacheStrategy(DiskCacheStrategy.NONE).error(R.mipmap.mae_album_img_default);
        if(isNeedPlaceHolder){
            builder = builder.placeholder(R.mipmap.mae_album_img_default);
        }
        if(MethodUtils.isNull(listeners)){
            builder.into(imageView);
        } else {
            builder.listener(new GlideRequestListener(listeners[0])).into(imageView);
        }
    }

    private class GlideRequestListener implements RequestListener{
        private AlbumEngineLoadListener listener;

        private GlideRequestListener(AlbumEngineLoadListener listener) {
            this.listener = listener;
        }

        @Override
        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
            listener.onLoadComplete();
            return false;
        }

        @Override
        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
            listener.onLoadComplete();
            return false;
        }
    }

    @Override
    public File downloadFile(Context context, String url) {
        try {
            return Glide.with(context)
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}