# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-printmapping

# Bracer rules
#-keepclassmembers @kotlin.Metadata class * {
#    <methods>;
#}
#-keep class kotlin.Metadata { *; }

#-keep interface zlc.season.butterfly.annotation.Module
#-keep public class * extends zlc.season.butterfly.annotation.Module
#-keep public class * extends zlc.season.butterfly.Action

#-keep class zlc.season.butterfly.annotation.Agile
#-keep class zlc.season.butterfly.annotation.Evade
-keep class zlc.season.butterfly.annotation.EvadeImpl

-keep @zlc.season.butterfly.annotation.EvadeImpl class * {*;}
-keep class * {
    @zlc.season.butterfly.annotation.EvadeImpl *;
}


