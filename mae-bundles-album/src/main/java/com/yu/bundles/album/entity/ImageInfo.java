package com.yu.bundles.album.entity;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;


import com.yu.bundles.album.utils.ImageQueue;
import com.yu.bundles.album.utils.MimeType;

import java.io.File;

/**
 * 图片信息
 * <p/>
 * Created by Clock on 2016/1/26.
 */
public class ImageInfo implements Parcelable {
    public static final long ID_CAPTURE = -1;
    /**
     * 图片名——只含有文件名，不包含全部路径
     */
    private String name;
    /**
     * 图片路径——不包含文件名
     */
    private String path;
    private long modifyTime;

    // 添加小图路径
    private String thumbnailPath;


    // 添加类型
    private String mimeType;

    private Uri uri;

    private long id;

    private long size;

    private long duration; // only for video, in ms

    public Uri getContentUri() {
        return uri;
    }

    public ImageInfo(long id, String name, String path, String mimeType, long size, long duration) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.mimeType = mimeType;
        this.size = size;
        this.duration = duration;
        if (this.mimeType == null) {
            this.mimeType = "";
        }

        Uri contentUri;
        if (isImage()) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (isVideo()) {
            contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        } else {
            contentUri = MediaStore.Files.getContentUri("external");
        }
        this.uri = ContentUris.withAppendedId(contentUri, id);
    }

    public static ImageInfo create(Cursor cursor){
        String data = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        File file = new File(data);
        return new ImageInfo(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                file.getName(), data.equals("")? "": file.getParentFile().getAbsolutePath(),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    public long getSize() {
        return size;
    }

    public long getDuration() {
        return duration;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ImageInfo))
            return false;
        if (this == o) return true;
        ImageInfo imageInfo = (ImageInfo) o;
        return getFullPath().equals(imageInfo.getFullPath());
    }

    /**
     * 获取当前图片的全路径
     */
    public String getFullPath() {
        return this.path + File.separatorChar + name;
    }

    public boolean isSelected() {
        return ImageQueue.getSelectedImages().contains(this.getFullPath());
    }

    public boolean isGif() {
        return MimeType.GIF.toString().equals(mimeType);
    }

    public boolean isImage() {
        return mimeType.equals(MimeType.JPEG.toString())
                || mimeType.equals(MimeType.PNG.toString())
                || mimeType.equals(MimeType.GIF.toString())
                || mimeType.equals(MimeType.BMP.toString())
                || mimeType.equals(MimeType.WEBP.toString());
    }


    public boolean isVideo() {
        return mimeType.equals(MimeType.MPEG.toString())
                || mimeType.equals(MimeType.MP4.toString())
                || mimeType.equals(MimeType.QUICKTIME.toString())
                || mimeType.equals(MimeType.THREEGPP.toString())
                || mimeType.equals(MimeType.THREEGPP2.toString())
                || mimeType.equals(MimeType.MKV.toString())
                || mimeType.equals(MimeType.WEBM.toString())
                || mimeType.equals(MimeType.TS.toString())
                || mimeType.equals(MimeType.AVI.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeLong(this.modifyTime);
        dest.writeString(this.thumbnailPath);
        dest.writeString(this.mimeType);
        dest.writeParcelable(this.uri, flags);
        dest.writeLong(this.id);
        dest.writeLong(this.size);
        dest.writeLong(this.duration);
    }

    protected ImageInfo(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.modifyTime = in.readLong();
        this.thumbnailPath = in.readString();
        this.mimeType = in.readString();
        this.uri = in.readParcelable(Uri.class.getClassLoader());
        this.id = in.readLong();
        this.size = in.readLong();
        this.duration = in.readLong();
    }

    public static final Creator<ImageInfo> CREATOR = new Creator<ImageInfo>() {
        @Override
        public ImageInfo createFromParcel(Parcel source) {
            return new ImageInfo(source);
        }

        @Override
        public ImageInfo[] newArray(int size) {
            return new ImageInfo[size];
        }
    };
}
