package com.yu.bundles.album.model;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.entity.ImageInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liyu on 2017/9/29.
 */

public class AlbumMediaModelImpl implements LoaderManager.LoaderCallbacks<Cursor>, AlbumMediaModel {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ALBUM = "args_album";

    private Context mContext;
    private LoaderManager mLoaderManager;
    private AlbumMediaCallbacks mCallbacks;
    public static HashMap<String, ArrayList<ImageInfo>> cacheMap = new HashMap<>();
    private String curId;

    public AlbumMediaModelImpl(Context mContext, LoaderManager mLoaderManager, AlbumMediaCallbacks mCallbacks) {
        this.mContext = mContext;
        this.mLoaderManager = mLoaderManager;
        this.mCallbacks = mCallbacks;
    }

    @Override
    public void loadAlbumMediaData(Album album) {
        if (mCallbacks == null) {
            return;
        }
        if (album == null) {
            mCallbacks.onAlbumMediaLoad(null);
            return;
        }
        this.curId = album.mId;
        ArrayList<ImageInfo> cacheList = cacheMap.get(album.mId);
        if (cacheList != null) {
            mCallbacks.onAlbumMediaLoad(album.mId);
        } else {
            Bundle args = new Bundle();
            args.putParcelable(ARGS_ALBUM, album);
            if (mLoaderManager.getLoader(LOADER_ID) == null) {
                mLoaderManager.initLoader(LOADER_ID, args, this);
            } else {
                mLoaderManager.restartLoader(LOADER_ID, args, this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return AlbumMediaLoader.newInstance(mContext, (Album) args.getParcelable(ARGS_ALBUM));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ArrayList<ImageInfo> cacheList = new ArrayList<>();
        while (data.moveToNext()) {
            cacheList.add(ImageInfo.create(data));
        }
        cacheMap.put(curId, cacheList);
        if (mCallbacks != null) {
            mCallbacks.onAlbumMediaLoad(curId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mCallbacks != null) {
            mCallbacks.onAlbumMediaReset();
        }
    }

    public void onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID);
        mCallbacks = null;
    }

    public interface AlbumMediaCallbacks {
        void onAlbumMediaLoad(String albumId);
        void onAlbumMediaReset();
    }

}
