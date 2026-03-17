package com.example.smart_planner

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class SmartPlannerApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Проверяем, является ли сборка отладочной через метаданные
        if (isDebug()) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun isDebug(): Boolean {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            (packageInfo.applicationInfo?.flags?.and(android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE)) != 0
        } catch (e: Exception) {
            false
        }
    }
}