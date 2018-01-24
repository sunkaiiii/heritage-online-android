# heritage-online-android
E迹 非遗掌上信息化平台（毕设）

#### v2.6.1 <br>
1、浏览其他用户的主页的时候，点击其头像可以打开头像大图。 

#### 这个应该算stable版本了吧……

___
### 介绍
1、学习安卓制作的第二个app，展示了Android各基本功能的使用，主要提供了阅览信息、发帖互动、账号登录注册的功能
2、因水平有限，且前后开发周期较长，程序代码风格可能不尽相同。不断学习优化中……
3、这是一个以非物质文化遗产为主题的应用（毕设选题），主要数据来源于一些非遗主题网站的内容爬取。
4、出于学习的需要，完成功能需求的时候稍微控制了一下第三方库的使用。
5、其中，四个navigation bar对应的首页，民间页是负责获取以json包装的信息，并解析json显示，发现页提供了简单的发帖、回帖、点赞的功能。个人中心的完成了一些“微博”式的功能。
6、因功能需求但水平有限，在消息推送上使用了“小米推送”作为第三方推送
7、后端服务请参考  [heritage-webservice](https://github.com/sunkaiiii/heritage-webservice )
8、APP前期由Java编写，在Build 2.0版本开始，陆续转用Kotlin实现新的功能，并在近期完成了全部代码迁移到Kotlin上。目前为纯Kotlin编写的APP

___
使用到的第三方库
ksoap2：网络请求
[roundedimageview](https://github.com/vinc3m1/RoundedImageView)：圆形头像
[PhotoView](https://github.com/chrisbanes/PhotoView)：图片预览
[Luban](https://github.com/Curzibn/Luban)：图片压缩


![](https://sunkaiiii.github.io/docs/images/image1.jpg)![](https://sunkaiiii.github.io/docs/images/image2.jpg)![](https://sunkaiiii.github.io/docs/images/image3.jpg)![](https://sunkaiiii.github.io/docs/images/image4.jpg) ![](https://sunkaiiii.github.io/docs/images/image5.jpg)




