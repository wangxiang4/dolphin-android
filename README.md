<h1 align="center">
    <b>
        <a href="https://godolphinx.org"><img src="https://godolphinx.org/images/dolphin-platform-logo.svg" /></a><br>
    </b>
</h1>

<p align="center"> 一个快速开发软件的平台 </p>

<p align="center">
    <a href="https://godolphinx.org/"><b>Website</b></a> •
    <a href="https://godolphinx.org/android/description.html"><b>Documentation</b></a>
</p>

<div align="center">
  <a href="https://github.com/wangxiang4/dolphin-android/blob/master/LICENSE">
    <img src="https://img.shields.io/npm/l/vue.svg?sanitize=true">
  </a>
  <a href="https://gitpod.io/#https://github.com/wangxiang4/dolphin-android">
    <img src="https://img.shields.io/badge/Gitpod-Ready--to--Code-blue?logo=gitpod&style=flat-square">
  </a>
  <a href="https://discord.gg/DREuQWrRYQ">
    <img src="https://img.shields.io/badge/chat-on%20discord-7289da.svg?sanitize=true"/>
  </a>
</div>

## 🐬 介绍
海豚生态计划-打造一个web端,安卓端,ios端的一个海豚开发平台生态圈,不接收任何商业化,并且完全免费开源(包含高级功能)。

## 💪 愿景
让人人都可以快速高效的开发软件

## ✨ 特性
- MVVM开发模式
- 采用最新的Androidx扩展库，并向后兼容各个Android版本
- Material-Dialogs一个漂亮的、流畅的、可定制的material design风格的对话框。
- 引入RxJava+RxAndroid，支持java响应式编程与安卓扩展异步UI事件响应式编程
- 引入RxBinding，支持用RxJava的形式来处理UI事件
- 引入RxLifecycle让安卓组件(活动或者碎片)生命周期同步,防止内存泄露
- 采用Retrofit、OkHttp、RxJava进行网络请求
- 集成AndroidUtilCode工具库，降低相关API的学习成本，提高工作效率，使Java拥有函数式语言般的优雅，让Java语言也可以甜甜的
- 集成Dagger2实现了IOC依赖控制反转,帮助您的应用程序拆分为松散耦合的组件,可以更轻松地开发、测试和维护这些组件
- 集成Calligraphy3字体风格库，实现字体个性化
- 集成Lombok，让代码变得更加简洁，消除冗长代码提高开发效率
- 集成腾讯bugly平台，支持异常上报和数据分析功能，帮助开发者快速定位bug
- 集成PictureSelector，支持微信风格的图片自定义选择器进行上传
- 集成友盟库，支持消息推送、社会化分享、QQ登录、微信登录、自定义启动引导页面
- 集成Glide，支持各种图片加载
- 集成腾讯MMKV，让 key-value 数据存储变得更加高效
- 支持地图导航，智能路线规划，后台刷新定位、媒体锁后台保活
- 集成AdvancedRecyclerView支持各种回收视图高级适配器操作(可滑动列表，可拖拽列表)

## <img width="28" style="vertical-align:middle" src="https://godolphinx.org/images/hacktoberfest-logo.svg"> 黑客节
加入[Github HackToberFest](https://hacktoberfest.com/) 开始为此项目做出贡献.

## 🍀 基础准备
- 了解 **RxJava**
- 了解 **Androidx**
- 了解 **Dagger2**

## 🔨 开发目录

```
├─ dolphin-android -- Android海豚APP
│  ├─ app -- app目录
│  │  ├─ src -- 源代码
│  │  │  ├─ androidTest -- 安卓插桩测试单元测试
│  │  │  ├─ main -- 源代码入口
│  │  │  │  ├─ java -- Java源代码根目录
│  │  │  │  │  ├─ com.dolphin.demo -- 包名
│  │  │  │  │  │  ├─ app -- 初始化应用程序
│  │  │  │  │  │  ├─ constant -- 全局常量
│  │  │  │  │  │  ├─ di -- dagger2依赖注入
│  │  │  │  │  │  │  ├─ component -- 组件媒介
│  │  │  │  │  │  │  ├─ module -- 注入模块提供
│  │  │  │  │  │  │  ├─ scope -- 局部单例生命周期注解
│  │  │  │  │  │  ├─ engine -- 图片选择器第三方组件引擎
│  │  │  │  │  │  ├─ entity -- 数据实体类
│  │  │  │  │  │  ├─ listener -- 监听器
│  │  │  │  │  │  ├─ mapper -- api请求数据映射层
│  │  │  │  │  │  ├─ service -- 业务逻辑处理层
│  │  │  │  │  │  ├─ ui -- 活动界面控制层
│  │  │  │  │  │  │  ├─ activity -- 活动窗口
│  │  │  │  │  │  │  ├─ adapter -- 回收视图适配器
│  │  │  │  │  │  │  ├─ fragment -- 活动碎片局部窗口
│  │  │  │  │  │  │  ├─ vm -- 视图模型
│  │  │  │  │  │  ├─ util -- 全局工具类
│  │  │  │  ├─ res -- 资源文件
│  │  │  │  │  ├─ drawable -- 默认图片目录(像素密度目录中找不到就使用默认的)
│  │  │  │  │  ├─ drawable-nodpi -- 像素密度无关的图片目录
│  │  │  │  │  ├─ drawable-xxhdpi -- 1080*1920 分辨率下的图片目录
│  │  │  │  │  ├─ drawable-xxxhdpi -- 1440*2560 分辨率下的图片目录
│  │  │  │  │  ├─ layout -- 界面布局文件
│  │  │  │  │  ├─ values -- 参数配置
│  │  │  │  │  ├─ xml -- 配置文件
│  │  │  ├─ unitTest -- 本地单元测试
│  ├─ gradle -- gradle下载配置
│  ├─ library -- 基础依赖库
│  │  ├─ core -- 核心库
│  │  │  ├─ libs -- 本地离线依赖
│  │  │  ├─ src -- 源代码
│  │  │  │  ├─ main -- 源代码入口
│  │  │  │  │  ├─ assets -- 外部文件的资源目录，不会被编译
│  │  │  │  │  ├─ java -- Java源代码根目录
│  │  │  │  │  │  ├─ com.dolphin.core -- 包名
│  │  │  │  │  │  │  ├─ amap -- 高德地图相关
│  │  │  │  │  │  │  ├─ base -- 基础核心类
│  │  │  │  │  │  │  ├─ binding -- 扩展组件的属性绑定
│  │  │  │  │  │  │  │  ├─ command -- 绑定视图命令回调
│  │  │  │  │  │  │  │  ├─ viewadapter -- 绑定视图适配器
│  │  │  │  │  │  │  ├─ bus -- 全局组件事件订阅发布通信
│  │  │  │  │  │  │  ├─ constant -- 全局常量
│  │  │  │  │  │  │  ├─ crash -- 自定义应用程序崩溃
│  │  │  │  │  │  │  ├─ entity -- 数据实体类
│  │  │  │  │  │  │  ├─ enums -- 枚举定义
│  │  │  │  │  │  │  ├─ http -- http请求
│  │  │  │  │  │  │  │  ├─ api -- 响应信息
│  │  │  │  │  │  │  │  ├─ exception -- 请求错误处理
│  │  │  │  │  │  │  │  ├─ file -- 文件流相关处理
│  │  │  │  │  │  │  │  ├─ interceptor -- 请求拦截器
│  │  │  │  │  │  │  │  ├─ observer -- 基础文件响应可观测监听
│  │  │  │  │  │  │  ├─ listener -- 监听器
│  │  │  │  │  │  │  ├─ service -- 安卓后台服务
│  │  │  │  │  │  │  ├─ util -- 全局工具类
│  │  │  │  │  │  │  ├─ widget -- 安卓组件扩展
│  │  │  │  │  ├─ res -- 资源文件
│  │  │  │  │  │  ├─ drawable-hdpi -- 480*800/480*854 分辨率下的图片目录
│  │  │  │  │  │  ├─ drawable-mdpi -- 320*480 分辨率下的图片目录
│  │  │  │  │  │  ├─ drawable-xhdpi -- 720*1280 分辨率下的图片目录
│  │  │  │  │  │  ├─ drawable-xxhdpi -- 1080*1920 分辨率下的图片目录
│  │  │  │  │  │  ├─ drawable-xxxhdpi -- 1440*2560 分辨率下的图片目录
│  │  │  │  │  │  ├─ layout -- 界面布局文件
│  │  │  │  │  │  ├─ raw -- 原始文件目录
│  │  │  │  │  │  ├─ values -- 参数配置
│  │  │  │  │  ├─ res-sw -- 最小宽度屏幕适配资源
│  │  ├─ umeng -- 友盟库
│  │  │  ├─ libs -- 本地离线依赖
│  │  │  ├─ src -- 源代码
│  │  │  │  ├─ main -- 源代码入口
│  │  │  │  │  ├─ java -- Java源代码根目录
│  │  │  │  │  │  ├─ com.dolphin.umeng -- 包名
│  │  │  │  │  │  │  ├─ entity -- 数据实体类
│  │  │  │  │  │  │  ├─ enums -- 枚举定义
│  │  │  │  │  │  │  ├─ listener -- 监听器
│  │  │  │  │  ├─ res -- 资源文件
│  │  │  │  │  │  ├─ layout -- 界面布局文件
│  │  │  │  │  │  ├─ xml -- 配置文件
```


## 🤔 一起讨论
加入我们的 [Discord](https://discord.gg/DREuQWrRYQ) 开始与大家交流。

## 🤗 我想成为开发团队的一员！
欢迎😀！我们正在寻找有才华的开发者加入我们，让海豚开发平台变得更好！如果您想加入开发团队，请联系我们，非常欢迎您加入我们！💖

## 在线一键设置
您可以使用 Gitpod，一个在线 IDE（开源免费）来在线贡献或运行示例。

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/wangxiang4/dolphin-android)

## 📄 执照
[Dolphin Development Platform 是获得MIT许可](https://github.com/wangxiang4/dolphin-android/blob/master/LICENSE) 的开源软件 。

