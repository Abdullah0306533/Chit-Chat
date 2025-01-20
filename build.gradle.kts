// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false


    //for migration of kapt to ksp
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    //Hilt
    id ("com.google.dagger.hilt.android") version "2.53.1" apply false
    alias(libs.plugins.google.gms.google.services) apply false
}