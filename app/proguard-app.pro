# 忽略警告
#-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/xxxxxxxxx.jar

# 屏蔽打包提示第三方库内部缺少引用,由于第三方库内部有一些都是采用动态引用类
-dontwarn com.sun.**
-dontwarn com.umeng.**
-dontwarn java.lang.**
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn lombok.**
-dontwarn org.apache.tools.**
-dontwarn org.bouncycastle.**
-dontwarn org.conscrypt.**
-dontwarn org.eclipse.**
-dontwarn org.mapstruct.**
-dontwarn org.openjsse.**
-dontwarn org.openjsse.**


# 第三方依赖
# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

# OkHttp3
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**

# PictureSelector
-keep class com.luck.picture.lib.** { *; }

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# 禁止类名混淆，否则通过类名称找不到类
-keep class com.dolphin.demo.entity.**{*;}
-keep class com.dolphin.demo.ui.activity.**{*;}
-keep class com.dolphin.demo.ui.fragment.**{*;}