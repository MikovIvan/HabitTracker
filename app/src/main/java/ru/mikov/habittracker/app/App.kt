package ru.mikov.habittracker.app

import android.app.Application
import android.content.Context
import ru.mikov.data.remote.NetworkMonitor
import ru.mikov.habittracker.di.AppComponent
import ru.mikov.habittracker.di.AppModule
import ru.mikov.habittracker.di.DaggerAppComponent

class App : Application() {

    lateinit var appComponent: AppComponent

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
        NetworkMonitor(applicationContext()).registerNetworkMonitor()
    }

}