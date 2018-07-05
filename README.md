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
	        implementation 'com.github.JuHonggang:ImageSet:v0.11'
	}

### License

	Copyright (c) 2018 Freeman

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
