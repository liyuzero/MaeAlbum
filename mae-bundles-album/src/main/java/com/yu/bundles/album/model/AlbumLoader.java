package com.yu.bundles.album.model;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.text.TextUtils;

import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.R;
import com.yu.bundles.album.entity.Album;
import com.yu.bundles.album.utils.MimeType;

/**
 * Created by liyu on 2017/9/28.
 */

public class AlbumLoader extends CursorLoader {
    public static final String COLUMN_COUNT = "count";
    private static final Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    static final String ORDER_BY = MediaStore.Images.Media.DATE_MODIFIED + " DESC";

    private static final String[] COLUMNS = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            COLUMN_COUNT};

    private static final String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            "bucket_id",
            "bucket_display_name",
            MediaStore.MediaColumns.DATA,
            "COUNT(*) AS " + COLUMN_COUNT};

    private AlbumLoader(Context context, String selection, String[] selectionArgs) {
        super(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    @Override
    public Cursor loadInBackground() {
        Cursor albums = super.loadInBackground();
        MatrixCursor allAlbum = new MatrixCursor(COLUMNS);
        int totalCount = 0;
        String allAlbumCoverPath = "";
        if (albums != null) {
            while (albums.moveToNext()) {
                totalCount += albums.getInt(albums.getColumnIndex(COLUMN_COUNT));
            }
            if (albums.moveToFirst()) {
                allAlbumCoverPath = albums.getString(albums.getColumnIndex(MediaStore.MediaColumns.DATA));
            }
        }
        allAlbum.addRow(new String[]{Album.ALBUM_ID_ALL, Album.ALBUM_ID_ALL, getContext().getString(R.string.mae_album_all), allAlbumCoverPath,
                String.valueOf(totalCount)});

        return new MergeCursor(new Cursor[]{allAlbum, albums});
    }

    public static CursorLoader newInstance(Context context) {
        return new AlbumLoader(context, getSelectionStr(), null);
    }

    private static String getSelectionStr() {
        return getMimeTypeFilter(ConfigBuilder.mimeTypes) + getFileTypeSelection()
                + " AND " + getSizeSelection() + ") GROUP BY (bucket_id";
    }

    static String getFileTypeSelection() {
        return ConfigBuilder.fileType == ConfigBuilder.FILE_TYPE.IMAGE_AND_VIDEO ?
                "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                        + " OR " + MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO + ")" :
                ConfigBuilder.fileType == ConfigBuilder.FILE_TYPE.IMAGE ?
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE :
                        MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
    }

    /*
    * 去除传入参数所指定的MimeType文件
    * */
    static String getMimeTypeFilter(MimeType[] mimeTypes) {
        String mimeTypeSql = "";
        if (mimeTypes == null || mimeTypes.length <= 0) {
            return mimeTypeSql;
        }
        for (MimeType mimeType : mimeTypes) {
            if (!TextUtils.isEmpty(mimeTypeSql)) {
                mimeTypeSql += " AND ";
            }
            mimeTypeSql += MediaStore.Files.FileColumns.MIME_TYPE + " != '" + mimeType + "'";
        }
        mimeTypeSql += " AND ";
        return mimeTypeSql;
    }

    static String getSizeSelection() {
        final int min;
        final int max;

        if (ConfigBuilder.MIN_FILE_SIZE < 0) {
            min = 0;
        } else {
            min = ConfigBuilder.MIN_FILE_SIZE;
        }

        if (ConfigBuilder.MAX_FILE_SIZE < 0) {
            max = Integer.MAX_VALUE;
        } else {
            max = ConfigBuilder.MAX_FILE_SIZE;
        }

        return MediaStore.MediaColumns.SIZE + " BETWEEN " + min + " AND " + max;

//        if (ConfigBuilder.MAX_FILE_SIZE < 0 && ConfigBuilder.MIN_FILE_SIZE < 0) {
//            return MediaStore.MediaColumns.SIZE + "> 0";
//        } else if (ConfigBuilder.MAX_FILE_SIZE < 0 && ConfigBuilder.MIN_FILE_SIZE >= 0) {
//            return MediaStore.MediaColumns.SIZE + ">" + ConfigBuilder.MIN_FILE_SIZE;
//        } else if (ConfigBuilder.MAX_FILE_SIZE >= 0 && ConfigBuilder.MIN_FILE_SIZE < 0) {
//            return MediaStore.MediaColumns.SIZE + "> 0 AND "
//                    + MediaStore.MediaColumns.SIZE + "<" + ConfigBuilder.MAX_FILE_SIZE;
//        } else {
//            return MediaStore.MediaColumns.SIZE + ">" + ConfigBuilder.MIN_FILE_SIZE + " AND "
//                    + MediaStore.MediaColumns.SIZE + "<" + ConfigBuilder.MAX_FILE_SIZE;
//        }
    }

}
