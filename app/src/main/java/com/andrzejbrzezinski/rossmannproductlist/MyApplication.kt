package com.andrzejbrzezinski.rossmannproductlist

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.BuildConfig
import androidx.room.CoroutinesRoom
import androidx.room.Room
import androidx.work.Configuration
import androidx.work.WorkManager
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Films
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Users
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.room.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: Application(),androidx.work.Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        initializeDatabase()

        val configuration = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

        WorkManager.initialize(
            this,
            configuration
        )}


    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()


    private fun initializeDatabase()
    {
        val db = AppDatabase.getDatabase(applicationContext)
        val filmDao= db.filmDao()
        val userDao= db.userDao()
        CoroutineScope(Dispatchers.IO).launch{
            if(filmDao.getAllFilms().isEmpty()){
                filmDao.insertFilm(Films(1, "anonymous", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","dsdsd"))
                filmDao.insertFilm(Films(2, "Elephant Dream", "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4","dsdsd"))
                filmDao.insertFilm(Films(3,"For Bigger Blazes","https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4","dsdsd"))
            }
            if(userDao.getAllUsers().isEmpty())
            {
                userDao.insertUser(Users(1, mutableListOf<Int>().joinToString(),"edhdufhsad","dsdsd"))
                userDao.insertUser(Users(2, mutableListOf(0,1).joinToString(","),"haslo1234!","Andrzej"))
                userDao.insertUser(Users(3, mutableListOf<Int>().joinToString(),"haslo1234!","Maciek"))
                userDao.insertUser(Users(4, mutableListOf(0).joinToString(","),"hasloPrzemek","Przemek"))
                userDao.insertUser(Users(5, mutableListOf<Int>().joinToString(),"fffffff","Andrzej"))
            }
        }

    }
}