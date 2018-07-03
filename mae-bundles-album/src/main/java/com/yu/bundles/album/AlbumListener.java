package com.yu.bundles.album;

import java.util.List;

/**
 * 选择图片对外提供的接口回调
 */
public interface AlbumListener {
    /**
     * 当用户点击确认按钮时 （确认选择了）
     *
     * @param ps ： 所选的图片的路径
     */
    void onSelected(List<String> ps);

    /**
     * 当选择的图片超过设置的max数时
     *
     * @param ps : 当前选择的图片
     * @param p  : 用户点击的图片
     */
    void onFull(List<String> ps, String p);
}
