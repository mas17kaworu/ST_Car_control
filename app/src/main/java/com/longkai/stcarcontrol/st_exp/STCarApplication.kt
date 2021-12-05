package com.longkai.stcarcontrol.st_exp

import android.Manifest
import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler
import de.mindpipe.android.logging.log4j.LogConfigurator
import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils
import org.apache.log4j.Level
import java.io.File
import java.lang.Exception

/**
 * Created by Administrator on 2017/12/10.
 */

val Context.appPrefsDataStore : DataStore<Preferences> by preferencesDataStore("app")

class STCarApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // CrashHandler.getsInstance().init(this)

//        Log.i("longkai", "Karl test isExternalStorageWritable = " + FileUtils.isExternalStorageWritable());
//        Log.i("longkai", "Karl test INTERNAL_PATH = " + INTERNAL_PATH);
//        logConfig();
//        FileUtils.createSDDir(INTERNAL_PATH + "testlk");
    }

    companion object {
        const val inUIDebugMode = false
        @JvmStatic
        fun logConfig() {
            val logConfigurator = LogConfigurator()
            logConfigurator.fileName = (FileUtils.INTERNAL_PATH
                    + "ST_DEMO_CAR" + File.separator + "logs"
                    + File.separator + "communication.txt")
            logConfigurator.rootLevel = Level.ALL
            logConfigurator.setLevel("org.apache", Level.ALL)
            logConfigurator.filePattern = "%d %-5p [%c{2}]-[%L] %m%n"
            logConfigurator.maxFileSize = (1024 * 1024 * 5).toLong()
            logConfigurator.isImmediateFlush = true
            logConfigurator.configure()
        }

        private const val REQUEST_EXTERNAL_STORAGE = 1
        var permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        @JvmStatic
        fun verifyStoragePermissions(activity: Activity?) {
            try {
                val permission = ActivityCompat.checkSelfPermission(
                    activity!!,
                    "android.permission.WRITE_EXTERNAL_STORAGE"
                )
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                        activity,
                        permissions,
                        REQUEST_EXTERNAL_STORAGE
                    )
                } else {
                    logConfig()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}