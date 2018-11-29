package com.yu.bundles.album.model;

import android.content.Context;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.entity.ImageInfo;

/**
 * Created by liyu on 2017/9/29.
 */

public class AlbumMediaLoader extends CursorLoader {

    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            "duration"};

    private Album curAlbum;

    private AlbumMediaLoader(Context context, Album album, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, getSelectionStr(album), selectionArgs, AlbumLoader.ORDER_BY);
        this.curAlbum = album;
    }

    static CursorLoader newInstance(Context context, Album album) {
        return new AlbumMediaLoader(context, album, null);
    }

    private static String getSelectionStr(Album album) {
        String selection = AlbumLoader.getMimeTypeFilter(ConfigBuilder.mimeTypes) + AlbumLoader.
                getFileTypeSelection() + " AND " + AlbumLoader.getSizeSelection();
        return album.isAll()? selection: selection + " AND bucket_id = " + album.mId;
    }

    private static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    public Cursor loadInBackground() {
        if(!ConfigBuilder.IS_SHOW_CAPTURE || !hasCameraFeature(getContext()) || !curAlbum.isAll()){
            return super.loadInBackground();
        }
        MatrixCursor dummy = new MatrixCursor(PROJECTION);
        dummy.addRow(new Object[]{ImageInfo.ID_CAPTURE, "", "", 0, 0});
        return new MergeCursor(new Cursor[]{dummy, super.loadInBackground()});
    }
}
