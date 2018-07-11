package com.example.albumDemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yu.bundles.album.AlbumListener;
import com.yu.bundles.album.ConfigBuilder;
import com.yu.bundles.album.MaeAlbum;
import com.yu.bundles.album.OnPreviewLongClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView openAlbumView;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaeAlbum.setImageEngine(new GlideEngine());
        MaeAlbum.setOnPreviewLongClickListener(new OnPreviewLongClickListener() {
            @Override
            public void onPreviewLongClick(Object imgPath) {
                Toast.makeText(getApplicationContext(), (String)imgPath, Toast.LENGTH_SHORT).show();
            }
        });

        result = findViewById(R.id.result);
        openAlbumView = (TextView) findViewById(R.id.open_album_blue);
        findViewById(R.id.open_album_red).setOnClickListener(this);
        findViewById(R.id.open_album_user).setOnClickListener(this);
        findViewById(R.id.outer_album_overview).setOnClickListener(this);
        openAlbumView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        MaeAlbum.setNavigationIcon(R.mipmap.mae_album_ic_action_back);
        switch (view.getId()) {
            case R.id.open_album_blue:
                MaeAlbum.setStyle(R.style.MyAlumBlue);
                blueStyle();
                break;
            case R.id.open_album_red:
                MaeAlbum.setStyle(R.style.MyAlumRed);
                redStyle();
                break;
            case R.id.open_album_user:
                MaeAlbum.setStyle(R.style.MyAlumWhite);
                userStyle();
                break;
            case R.id.outer_album_overview:
                MaeAlbum.setStyle(R.style.MyAlumWhite);
                ArrayList<String> list = new ArrayList<>();
                list.add("http://imgsrc.baidu.com/image/c0%3Dshijue1%2C0%2C0%2C294%2C40/sign=74c4b391865494ee932f075a459c8a8b/f11f3a292df5e0fe1e3fbb2f566034a85edf72fc.jpg");
                list.add("http://c.hiphotos.baidu.com/zhidao/pic/item/dcc451da81cb39dbccfc0e4ad4160924ab1830e9.jpg");
                MaeAlbum.startPreview(this, list, 0);
                break;
        }
    }

    private void userStyle() {
        MaeAlbum.from(this)
                .maxSize(9)
                .column(3)
                .forResult(new AlbumListener() {
                    @Override
                    public void onSelected(List<String> ps) {   // 选择完毕回调
                        show(ps);
                    }

                    @Override
                    public void onFull(List<String> ps, String p) {  // 选满了的回调
                        Toast.makeText(getApplicationContext(), "选满了", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void blueStyle() {
        MaeAlbum.from(this)
                .maxSize(1)
                .column(3)
                .fileType(ConfigBuilder.FILE_TYPE.VIDEO)
                .setIsShowCapture(true)
                .forResult(new AlbumListener() {
                    @Override
                    public void onSelected(List<String> ps) {   // 选择完毕回调
                        show(ps);
                    }

                    @Override
                    public void onFull(List<String> ps, String p) {  // 选满了的回调
                        Toast.makeText(getApplicationContext(), "选满了", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redStyle() {
        MaeAlbum.from(this)
                .maxSize(9)
                .column(3)
                .fileType(ConfigBuilder.FILE_TYPE.IMAGE_AND_VIDEO)
                .setIsShowCapture(true)
//                .imageEngine()      // 指定图片加载引擎
//                .mimeTypeFilter()   // 格式的过滤
//                .fileSizeLimit()    // 图片大小过滤
                .forResult(new AlbumListener() {
                    @Override
                    public void onSelected(List<String> ps) {   // 选择完毕回调
                        show(ps);
                    }

                    @Override
                    public void onFull(List<String> ps, String p) {  // 选满了的回调
                        Toast.makeText(getApplicationContext(), "选满了", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final List<String> strings = MaeAlbum.obtainPathResult(data);      // 返回图片的地址
            show(strings);
        }
    }

    private void show(List<String> strings) {
        if (strings == null || strings.size() <= 0) {
            return;
        }
        String html = "";
        for (String str : strings) {
            html += str + "<p>";
        }
        result.setText(Html.fromHtml(html));
    }
}
