### 该图片库包含的功能
- 图片加载库的二次封装(一行代码替换图片加载库)；
- 相册功能（类似朋友圈选择图片）
- 从相册/相机选择照片并裁剪
- 图片压缩；（使用Luban压缩）
- 上传图片到服务器；
- 上传图片到七牛；

### 添加依赖

在项目的build.gradle中添加：

    allprojects {
      repositories {
        ...
        maven { url 'https://jitpack.io' }
      }
    }

在APP模块下的build.gradle中添加依赖：

	dependencies {
	        implementation 'com.github.JuHonggang:ImageSet:v0.1'
	}
