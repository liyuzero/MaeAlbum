package com.yu.bundles.album.utils;

import java.util.ArrayList;

public class ImageQueue {
    private static ArrayList<String> sImages = new ArrayList<>(9);

    public static void update(String dst, String src) {
        if (dst == null || src == null || dst.equals(src))
            return;
        sImages.remove(dst);
        sImages.add(src);
    }

    /**
     * 添加新选中的图片
     *
     * @param path ： 新选中的图片的全路径
     */
    public static void add(String path) {
        if (sImages.contains(path))
            return;
        sImages.add(path);
    }

    /**
     * 取消选中的图片
     *
     * @param path ： 取消选中的图片的全路径
     */
    public static void remove(String path) {
        if (!sImages.contains(path))
            return;
        sImages.remove(path);
    }


    public static void clearSelected() {
        sImages.clear();
    }

    public static void init() {
        if (sImages == null) {
            sImages = new ArrayList<>(9);
        }
    }

    public static ArrayList<String> getSelectedImages() {
        return sImages;
    }

    public static int getImageSize() {
        return getSelectedImages().size();
    }

}
