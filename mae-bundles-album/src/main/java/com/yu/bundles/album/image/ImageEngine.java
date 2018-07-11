package com.yu.bundles.album.image;

import android.content.Context;
import android.support.annotation.Keep;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by liyu on 2017/9/30.
 */

@Keep
public interface ImageEngine {
    File downloadFile(Context context, Object pathInfo);
    void loadImg(Context context, Object pathInfo, ImageView imageView, boolean isNeedPlaceHolder, AlbumEngineLoadListener... listeners);
    void loadGifImg(Context context, Object pathInfo, ImageView imageView, boolean isNeedPlaceHolder, AlbumEngineLoadListener... listeners);
    void onOuterPreviewPageSelected(Object path, boolean isLoadImgSuccess);

    interface AlbumEngineLoadListener {
        void onLoadComplete();
    }
}
