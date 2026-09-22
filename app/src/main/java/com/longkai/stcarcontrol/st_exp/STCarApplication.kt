package com.longkai.stcarcontrol.st_exp

import com.longkai.stcarcontrol.st_exp.Utils.CrashHandler
import de.mindpipe.android.logging.log4j.LogConfigurator
import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.multidex.MultiDex
import com.longkai.stcarcontrol.st_exp.STCarApplication
import com.longkai.stcarcontrol.st_exp.Utils.CustomExceptionHandler
import com.longkai.stcarcontrol.st_exp.Utils.FileUtils
import com.longkai.stcarcontrol.st_exp.compose.data.AppContainer
import com.longkai.stcarcontrol.st_exp.compose.data.AppContainerImpl
import org.apache.log4j.Level
import java.io.File
import kotlin.properties.Delegates

/**
 * Created by Administrator on 2017/12/10.
 */

val Context.appPrefsDataStore : DataStore<Preferences> by preferencesDataStore("app")

class STCarApplication : Application() {


    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        CONTEXT = applicationContext
        Thread.setDefaultUncaughtExceptionHandler(CustomExceptionHandler(this))

        // CrashHandler.getsInstance().init(this)

//        Log.i("longkai", "Karl test isExternalStorageWritable = " + FileUtils.isExternalStorageWritable());
//        Log.i("longkai", "Karl test INTERNAL_PATH = " + INTERNAL_PATH);
//        logConfig();
//        FileUtils.createSDDir(INTERNAL_PATH + "testlk");
        appContainer = AppContainerImpl(this, inUIDebugMode)
        MultiDex.install(this)
    }

    companion object {
        var CONTEXT: Context by Delegates.notNull()
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
            // logConfigurator.configure()
        }

    }
}