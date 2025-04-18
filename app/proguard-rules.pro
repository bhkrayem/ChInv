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
# Keep Room annotations and generated classes
# Keep all Room database classes and their members
-keep class androidx.room.** { *; }

# Keep class members (fields and methods) that are annotated with Room annotations
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}

# Prevent warnings about Room-related classes
-dontwarn androidx.room.**

# Optional: Keep Kotlin metadata (safe for reflection and coroutines)
-keep class kotlin.Metadata { *; }

# Optional: Keep lifecycle ViewModel (used in your app)
-keep class androidx.lifecycle.ViewModel { *; }
-keepclassmembers class androidx.lifecycle.ViewModel { *; }

# Optional: Keep coroutine internals used with Flow
-dontwarn kotlinx.coroutines.**