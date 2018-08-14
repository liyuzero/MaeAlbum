package com.yu.bundles.album;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yu.bundles.album.widget.MaeAlbumToolbar;

public class AlbumBaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ConfigBuilder.styleRes);
        init();
        super.onCreate(savedInstanceState);
    }

    private void init(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.argb(55,0,0,0));
        } else if (Build.VERSION.SDK_INT >= 19) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) getWindow().getDecorView();
            View statusBarView = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, getStatusBarHeight(this));
            statusBarView.setLayoutParams(params);
            statusBarView.setBackgroundColor(Color.argb(55,0,0,0));
            decorView.addView(statusBarView);
            ViewGroup parent = findViewById(android.R.id.content);
            int i = 0;
            for(int count = parent.getChildCount(); i < count; ++i) {
                View childView = parent.getChildAt(i);
                if (childView instanceof ViewGroup) {
                    childView.setFitsSystemWindows(true);
                    ((ViewGroup)childView).setClipToPadding(true);
                }
            }
        }
    }

    private int getStatusBarHeight(Context context) {
        if(context == null)
            return 0;
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void initToolbar(MaeAlbumToolbar toolbar){
        toolbar.init(this);
        toolbar.setNavigationIcon(ConfigBuilder.navigationIcon == null? R.drawable.mae_album_return_white : ConfigBuilder.navigationIcon);
        TextView rightView = toolbar.findViewById(R.id.toolbar_right);

        if(rightView != null){
            rightView.setAlpha(0.6f);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initToolbar((MaeAlbumToolbar) findViewById(R.id.toolbar));
    }
}
