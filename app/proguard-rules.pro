# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/mod/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep public class cn.modificator.launcher.R$*{
    public static final int *;
}
#-keep class org.apache.** {*;}
#-keep interface org.apache.** {*;}
#-dontwarn org.apache.**
#-dontwarn org.slf4j.**

-keeppackagenames doNotKeepAThing
-renamesourcefileattribute SourceFile
-keepattributes LineNumberTable,SourceFile

-repackageclasses ''

-optimizationpasses 10