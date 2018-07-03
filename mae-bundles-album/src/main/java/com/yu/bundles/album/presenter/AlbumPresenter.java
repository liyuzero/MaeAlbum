package com.yu.bundles.album.presenter;

import com.yu.bundles.album.entity.Album;

/**
 * Created by liyu on 2017/9/28.
 */

public interface AlbumPresenter {
    void startScanAlbum();
    void startScanAlbumMedia(Album album);
    void onDestroy();
}
