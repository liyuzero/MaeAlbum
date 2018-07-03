package com.yu.bundles.album.presenter;

import android.app.Activity;
import android.database.Cursor;
import android.support.annotation.StringRes;
import android.support.v4.app.LoaderManager;

/**
 * Created by liyu on 2017/9/29.
 */

public interface AlbumCursorView {
    Activity getActivity();
    LoaderManager getSupportLoaderManager();
    void showMessage(@StringRes int messageId);
    void onAlbumLoad(Cursor cursor);
    void onAlbumReset();
}
