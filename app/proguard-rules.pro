# Keep Room database classes and members
-keep class androidx.room.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
    @androidx.room.* <fields>;
}
-dontwarn androidx.room.**

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }

# Keep ViewModel classes
-keep class androidx.lifecycle.ViewModel { *; }
-keepclassmembers class androidx.lifecycle.ViewModel { *; }

# Don't warn about coroutines
-dontwarn kotlinx.coroutines.**
