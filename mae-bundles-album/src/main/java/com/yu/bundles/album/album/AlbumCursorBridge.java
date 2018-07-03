package com.yu.bundles.album.album;

import com.yu.bundles.album.entity.Album;

/**
 * Created by liyu on 2017/9/29.
 */

public interface AlbumCursorBridge {
    Album getCurrAlbum();
    void onAlbumClick(Album info);
}
