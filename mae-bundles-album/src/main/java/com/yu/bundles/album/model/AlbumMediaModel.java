package com.yu.bundles.album.model;

import com.yu.bundles.album.entity.Album;

/**
 * Created by liyu on 2017/9/30.
 */

public interface AlbumMediaModel {
    void loadAlbumMediaData(Album album);
    void onDestroy();
}
