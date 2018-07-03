package com.yu.bundles.album.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 相册目录信息
 */
public class AlbumInfo implements Parcelable {

    /**
     * 目录名
     */
    private String folderName;
    /**
     * 包含的所有图片信息
     */
    private ArrayList<ImageInfo> imageInfoList;
    /**
     * 第一张图片——只有文件名
     */
    private String frontCover;
    /**
     * 目录路径——全路径，包含文件夹名称
     */
    private String path;
    /**
     * 该目录下，最新修改的文件的修改时间
     */
    private long modifyTime;

    public AlbumInfo() {
    }

    /**
     * key: 相册名称
     * value: 相册内容 AlbumInfo
     */
    private static Map<String, AlbumInfo> is;

    /**
     * Create Album
     *
     * @param path album path
     * @return
     */
    public static AlbumInfo create(String path) {
        if (is.containsKey(path))
            return is.get(path);
        AlbumInfo i = new AlbumInfo();
        i.setPath(path);
        i.setImageInfoList(new ArrayList<ImageInfo>());
        is.put(path, i);
        return i;
    }

    /**
     * 相册目录初始化
     */
    public static void init() {
        if (is != null) {
            Collection<AlbumInfo> values = is.values();
            for (AlbumInfo i : values) {
                if (i.getImageInfoList() != null)
                    i.getImageInfoList().clear();
            }
        }
        is = new HashMap<>();
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFrontCover() {
        return frontCover;
    }

    public void setFrontCover(String frontCover) {
        this.frontCover = frontCover;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public ArrayList<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public void setImageInfoList(ArrayList<ImageInfo> imageInfoList) {
        this.imageInfoList = imageInfoList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumInfo)) {
            return false;
        }
        AlbumInfo other = (AlbumInfo) o;
        return this.path.equals(other.path) && this.folderName.equals(other.folderName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.folderName);
        dest.writeTypedList(this.imageInfoList);
        dest.writeString(this.frontCover);
        dest.writeString(this.path);
        dest.writeLong(this.modifyTime);
    }

    protected AlbumInfo(Parcel in) {
        this.folderName = in.readString();
        this.imageInfoList = in.createTypedArrayList(ImageInfo.CREATOR);
        this.frontCover = in.readString();
        this.path = in.readString();
        this.modifyTime = in.readLong();
    }

    public static final Creator<AlbumInfo> CREATOR = new Creator<AlbumInfo>() {
        @Override
        public AlbumInfo createFromParcel(Parcel source) {
            return new AlbumInfo(source);
        }

        @Override
        public AlbumInfo[] newArray(int size) {
            return new AlbumInfo[size];
        }
    };
}
