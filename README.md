
# heritage-online-android

E迹 非遗掌上信息化平台（毕设）

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

10、更换了后台 [2]

11、现在程序最低可支持到Android 4.0 [3]

  
[1]：我并不认为随便用了CardView和CollapsingToolbarLayout的App就可以叫做Material Design了，MD的设计在我来看是一个需要很长时间的学习才能够把握的设计元素，于是我现在的程序界面应该还属于“实验性的Material Design”。目前，主要是学习、改写身边熟悉的App的设计风格（于是就出现了很Instagram风格的“发现”页），[material.io](https://material.io/guidelines/)的指导方针实际上在App里也并没有严格的遵守，还需要不断地学习改进。

[2]：一开始的的后台用的是跑在Java平台下的Tomcat服务器，服务器最早仅仅是用来试手的，但随着客户端功能变多，服务端所有的东西都堆成了一坨。于是为了方便，就在新版本里新开了一个后台服务，使用了Go作为开发语言，Go语言下的[gin](https://github.com/gin-gonic/gin)作为http server

[3]：Android 4.4及以下的版本显示效果并不好，会有一些Bug
  

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

  

[roundedimageview](https://github.com/vinc3m1/RoundedImageView)：圆形头像



[okhttp](https://github.com/square/okhttp)：网络请求

  

[PhotoView](https://github.com/chrisbanes/PhotoView)：图片预览

  

[Luban](https://github.com/Curzibn/Luban)：图片压缩。项目并没有直接使用Luban，但是使用的压缩算法来源于Luban提供的算法，对参数进行了一定的修改



[glide](https://github.com/bumptech/glide)：图片加载

  
  
  

![](https://sunkaiiii.github.io/docs/images/1.png)![](https://sunkaiiii.github.io/docs/images/2.png)![](https://sunkaiiii.github.io/docs/images/3.png)![](https://sunkaiiii.github.io/docs/images/4.png)![](https://sunkaiiii.github.io/docs/images/5.png)![](https://sunkaiiii.github.io/docs/images/6.png)![](https://sunkaiiii.github.io/docs/images/7.png)![](https://sunkaiiii.github.io/docs/images/8.png)![](https://sunkaiiii.github.io/docs/images/9.png)![](https://sunkaiiii.github.io/docs/images/10.png)
