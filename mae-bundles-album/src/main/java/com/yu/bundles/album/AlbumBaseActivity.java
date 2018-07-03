package com.yu.bundles.album;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.yu.bundles.album.utils.MethodUtils;
import com.yu.bundles.album.widget.MaeAlbumToolbar;

public class AlbumBaseActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(ConfigBuilder.styleRes);
        init(this);
        super.onCreate(savedInstanceState);
    }

    private void init(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().setStatusBarColor(MethodUtils.getColorByAttrId(getApplicationContext(), R.attr.colorPrimaryDark));
        }
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
