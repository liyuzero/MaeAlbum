package com.yu.bundles.album.presenter;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.support.annotation.StringRes;

import com.yu.bundles.album.entity.AlbumInfo;

import java.util.ArrayList;


/**
 * 扫描完本地图片之后的回调，即MVP中的View层必须实现的接口
 */
public interface AlbumView {

    /**
     * 可以在该方法中进行相册展示
     */
    void refreshAlbumData(ArrayList<AlbumInfo> albumData);

    Activity getActivity();

    LoaderManager getLoadManager();

    void showMessage(@StringRes int messageId);

    void onAlbumLoad(Cursor cursor);

    void onAlbumReset();

    void notifyImageData(String albumId);

}
