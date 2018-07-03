package com.yu.bundles.album.entity;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

import com.yu.bundles.album.model.AlbumLoader;

/**
 * Created by liyu on 2017/9/28.
 */

public class Album implements Parcelable {

    public static final String ALBUM_ID_ALL = String.valueOf(-1);

    public final String mId;
    public final String mCoverPath;
    public final String mDisplayName;
    public long mCount;

    Album(String id, String coverPath, String albumName, long count) {
        mId = id;
        mCoverPath = coverPath;
        mDisplayName = albumName;
        mCount = count;
    }

    public static Album create(Cursor cursor) {
        return new Album(
                cursor.getString(cursor.getColumnIndex("bucket_id")),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)),
                cursor.getString(cursor.getColumnIndex("bucket_display_name")),
                cursor.getLong(cursor.getColumnIndex(AlbumLoader.COLUMN_COUNT)));
    }

    public boolean isAll(){
        return mId.equals(ALBUM_ID_ALL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mCoverPath);
        dest.writeString(this.mDisplayName);
        dest.writeLong(this.mCount);
    }

    protected Album(Parcel in) {
        this.mId = in.readString();
        this.mCoverPath = in.readString();
        this.mDisplayName = in.readString();
        this.mCount = in.readLong();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };

    @Override
    public boolean equals(Object obj) {
        return mId.equals(((Album)obj).mId);
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(mId);
    }
}
