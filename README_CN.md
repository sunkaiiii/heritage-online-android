
# heritage-online-android

English version README:[README_EN](https://github.com/sunkaiiii/heritage-online-android/blob/master/README_EN.md)

[![996.ICU](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu) 

E迹 非遗掌上信息化平台（毕设）

#### v4.1.0 <br>

1.UI调整

2.支持主题颜色切换

3.支持暗色模式

4.已读新闻标记和缓存

5.新闻列表底部相关新闻

6.全新的应用图标

7.图片查看页保存图片

8.搜搜页支持复合搜索


#### v4.0.0 <br>

1.首页信息流更换

2.首页新闻全新样式

3.新增论坛和专题报道页

4.删除登录注册页

5.删除民间页

6.添加人物页

7.删除发现页

8.删除与账号体系相关的一切功能，包括我的收藏，我的赞，我的帖子

9.删除设置页

10.添加非遗项目页

11.重构了网络请求相关方法

12.删除大量无用代码

#### v3.4 <br>

1、全面适配Android P

2、添加主题颜色切换功能

3、首页界面微调

4、优化了首页的网络性能

5、个人中心界面调整以适应主题

6、bug修复

#### v3.3.1 <br>

1、现在大部分页面的图片均可以点击查看大图 

2、修复了一些闪退问题

3、代码结构调整

#### v3.3 <br>

1、首页界面重做

2、重新制作搜索功能

3、部署全局HTTPS

4、发现页添加同城选项

5、帖子详情页界面调整

6、新闻页添加跳转动画

7、bug修复

#### v3.2.1 <br>

1、去除了不必要的软件权限

2、修复了一些闪退的bug

3、修改图片压缩功能的实现。


#### v3.2 <br>

1、个人中心新增“我的消息”

2、消息推送功能回归

3、适配了shortcuts

4、一些布局的调整

5、项目升级，打包混淆

6、修复了bug


#### v3.1 <br>

1、个人中心新增“我的赞”

2、登陆后可以收藏喜欢的内容了

3、个人中心新增“我的收藏”

4、添加了一些页面跳转动画，交互更好

5、改进我的帖子页面在小屏手机上的显示效果

6、诸多细节体验的优化和Bug修复


#### v3.0 <br>

1、首页及各二级页面重做，信息流全面更换

2、民间页及各二级页面重做，信息流全面更换

3、民间页删除老版本各“预约功能”

4、发现页界面重做

5、个人中心界面重做

6、个人中心删除“我的预约”

7、登陆、注册页界面重做

8、密码、找回密码答案将使用RSA加密保证安全性

9、实验性的Material Design [1]

10、更换了后台

11、现在程序最低可支持到Android 4.0 [2]

  
[1]：目前的界面设计还需要不断地学习改进。

[2]：Android 4.4及以下的版本显示效果并不好，会有一些Bug
  

___

### 介绍

1、学习安卓制作的第二个app，展示了Android各基本功能的使用，主要提供了阅览信息、发帖互动、账号登录注册的功能

  

2、因水平有限，且前后开发周期较长，程序代码风格可能不尽相同。不断学习优化中……

  

3、这是一个以非物质文化遗产为主题的应用（毕设选题），主要数据来源于一些非遗主题网站的内容爬取。

  

4、出于学习的需要，完成功能需求的时候稍微控制了一下第三方库的使用。

  

5、其中，四个navigation bar对应的首页，民间页是负责获取以json包装的信息，并解析json显示，发现页提供了简单的发帖、回帖、点赞的功能。个人中心的完成了一些“微博”式的功能。



6、后端服务请参考 [heritage-webservice](https://github.com/sunkaiiii/heritage_webservice )



7、2.x/1.x的老版本的后台请参考[heritage_webservice_old](https://github.com/sunkaiiii/heritage_webservice_old)


  
8、APP前期由Java编写，在v2.0版本开始，陆续转用Kotlin实现新的功能，并在v2.5版本将全部代码迁移到Kotlin上。目前为纯Kotlin编写的APP

  
  

#### 小米商店下载体验地址：http://app.xiaomi.com/detail/549196

#### Google Play 下载体验地址: https://play.google.com/store/apps/details?id=com.datong.heritage_online

  
  

___

#### 使用到的第三方库



[okhttp](https://github.com/square/okhttp)：网络请求

  

[PhotoView](https://github.com/chrisbanes/PhotoView)：图片预览



[glide](https://github.com/bumptech/glide)：图片加载

  
  
  

![](https://sunkaiiii.github.io/docs/images/1.png)![](https://sunkaiiii.github.io/docs/images/2.png)![](https://sunkaiiii.github.io/docs/images/3.png)![](https://sunkaiiii.github.io/docs/images/11.png)![](https://sunkaiiii.github.io/docs/images/4.png)![](https://sunkaiiii.github.io/docs/images/5.png)![](https://sunkaiiii.github.io/docs/images/6.png)![](https://sunkaiiii.github.io/docs/images/7.png)![](https://sunkaiiii.github.io/docs/images/8.png)![](https://sunkaiiii.github.io/docs/images/9.png)![](https://sunkaiiii.github.io/docs/images/10.png)
