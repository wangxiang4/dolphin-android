-dontwarn com.umeng.**

# 友盟相关 SDK
-keep class com.umeng.** {*;}

# QQ 和 微信 SDK
-keep class com.tencent.** {*;}

# 禁止类名混淆，否则通过类名称找不到类
-keep class com.dolphin.umeng.entity.**{*;}