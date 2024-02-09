plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Parcelable
    id("org.jetbrains.kotlin.plugin.parcelize")
    //Room
    
    id("kotlin-kapt")
    //NetworkConnectionManager

    id("dagger.hilt.android.plugin")
    //Firebase
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.reto2_app_android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.reto2_app_android"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations += listOf("es","en_US","eu")


    }

    androidResources {
        //Auto Locale generator
        //generateLocaleConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        create("customDebugType") {
            isDebuggable = true
        }


    }
    compileOptions {
        // Room - Cambiado de 1.8 a 17
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        // Room - Cambiado de 1.8 a 17
        jvmTarget = "17"
    }
    buildFeatures {
        // Habilitar Binding
        viewBinding = true
    }
}

dependencies {

    //NetworkConnectionManager
    implementation ("javax.inject:javax.inject:1")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.0")
    val hiltVersion = "2.50"
    implementation ("com.google.dagger:hilt-android:$hiltVersion")
    kapt ("com.google.dagger:hilt-compiler:$hiltVersion")
    //NetworkConnectionManager - For instrumentation tests
    androidTestImplementation  ("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptAndroidTest ("com.google.dagger:hilt-compiler:$hiltVersion")

    //NetworkConnectionManager - For local unit tests
    testImplementation ("com.google.dagger:hilt-android-testing:$hiltVersion")
    kaptTest ("com.google.dagger:hilt-compiler:$hiltVersion")


    //Ver contraseña
    implementation("com.google.android.material:material:1.11.0")


    implementation("androidx.annotation:annotation:1.6.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.preference:preference:1.2.0")
    // Room
    implementation ("androidx.room:room-runtime:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")

    // ADD para utilizar viewmodels
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    //Fragment
    val fragmentVersion = "1.6.2"
    implementation("androidx.fragment:fragment-ktx:$fragmentVersion")

    // ADD retrofit + gson para la conversion de strings en json a objetos y viceversa
    val retrofitVersion = "2.9.0"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")


    // by viewmodels entre otros
    implementation("androidx.activity:activity-compose:1.7.2")
    // para las listas
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    // sockets
    implementation("io.socket:socket.io-client:2.0.0")
    // viewmodels
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    // conversiones
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.jboss.spec.javax.sql:jboss-javax-sql-api_7.0_spec:2.0.0.Final")
    implementation ("com.squareup.moshi:moshi:1.12.0")
    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))

    // para utilizar eventBus
    implementation("org.greenrobot:eventbus:3.2.0")



        implementation("androidx.annotation:annotation:1.6.0")
        // To use the Java-compatible @Experimental API annotation
        implementation("androidx.annotation:annotation-experimental:1.4.0-dev01")

}

kapt {
    //NetworkConnectionManager
    correctErrorTypes = true
    javacOptions {
        option("-Adagger.hilt.android.internal.disableAndroidSuperclassValidation=true")
    }
}
