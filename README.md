相册组件
====
相册模块，类似于知乎相册; 可单独使用;
图片加载由外界指定，可参考demo，内部不提供默认实现
支持多进程

## 效果展示
![组件演示](https://github.com/zhaoyubetter/MarkdownPhotos/raw/master/gif/album_demo.gif)

##功能类别
- 提供访问系统相册，将选择的目标通过绝对地址回传给调用方；
- 查看大图功能（PhotoView）；
- 通过style可指定样式；

## 使用方法
### 配置步骤
> 1. 在项目的根目录gradle新增仓库gradle配置如下：
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
> 2. 在具体的module中新增依赖，新增依赖：
```
compile 'com.github.liyuzero:MaeAlbum:1.2.7'
```

### 使用步骤
#### 打开相册选择图片：
>采用链式编程的方式逐个进行参数设置；
(如果喜欢用回调，则可设置 callback，不喜欢 则用 onActivityForResult, 如果设置了
callback， onActivityResult 则不会调用了)：

在`Activity` or `Fragment`中：
```
    MaeAlbum.from(this)
                .maxSize(9)
                .column(3)
//                .imageEngine()      // 指定图片加载引擎
//                .mimeTypeFilter()   // 格式的过滤
//                .fileSizeLimit()    // 图片大小过滤
                .callback(new AlbumListener() {
                    @Override
                    public void onSelected(List<String> ps) {   // 选择完毕回调
                        show(ps);
                    }

                    @Override
                    public void onFull(List<String> ps, String p) {  // 选满了的回调
                        Toast.makeText(getApplicationContext(), "选满了", Toast.LENGTH_SHORT).show();
                    }
                }).forResult(20);
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            final List<String> strings = MEChelsea.obtainPathResult(data);      // 返回图片的地址
        }
    }
```

#### 预览图片：
> 用于展示网络上的图片功能

```
MEChelsea.startPreview(Context context, ArrayList<String> urls, int currPos)
```

#### 更多请参考`demo` 模块的示例代码；

## TODO：
1. 预览图片时：添加可将预览图片保存到系统相册功能；
2. 添加只选择单一图片功能（如：扫一扫时，选择图片）；


## Thanks
- [glide](https://github.com/bumptech/glide)
- photoView
- [Matisse](https://github.com/zhihu/Matisse)

