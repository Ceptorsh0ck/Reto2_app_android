package com.example.reto2_app_android

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.room.Room
import com.example.reto2_app_android.data.repository.local.database.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlin.properties.Delegates

@HiltAndroidApp
class MyApp : Application() {

    companion object {
        lateinit var context: Context
        lateinit var userPreferences: UserPreferences
        lateinit var rootPreferences: RootPreferences
        var wifiStatus by Delegates.notNull<Boolean>()

        lateinit var db: AppDatabase

    }

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    override fun onCreate() {
        super.onCreate()
        context = this;
        userPreferences = UserPreferences()
        rootPreferences = RootPreferences()

        db = Room
            .databaseBuilder(this,AppDatabase::class.java,getString(R.string.room_database_name))
            .build()
        // Required initialization logic here!

    }



    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    override fun onLowMemory() {
        super.onLowMemory()
    }
    override fun onTerminate() {
        super.onTerminate()
        if (!userPreferences.isRememberMeEnabled()) {
            userPreferences.unLogUser()
        }
    }

}