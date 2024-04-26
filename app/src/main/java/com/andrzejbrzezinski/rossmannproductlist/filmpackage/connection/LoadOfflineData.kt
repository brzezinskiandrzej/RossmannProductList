package com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.andrzejbrzezinski.rossmannproductlist.CombinedComments
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.andrzejbrzezinski.rossmannproductlist.User
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Users
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.FilmDao
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.UserDao
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.SharedPreferencesModule
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.room.AppDatabase
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LoadOfflineData @Inject constructor(private val loginStateService :LoginStateService): ILoadData {
    private lateinit var filmDao:FilmDao
    private lateinit var userDao:UserDao
    fun init(context: Context)
    {
        Log.i("offlinetest", "${this@LoadOfflineData}")
        val db = AppDatabase.getDatabase(context)
        filmDao = db.filmDao()
        userDao = db.userDao()
    }

    override suspend fun loginAnonymously(auth: FirebaseAuth): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun loginAndGetUserData(
        username: String,
        password: String,
        liked: List<String>
    ):Boolean = withContext(Dispatchers.IO){
        Log.i("offlinetest", "${this@LoadOfflineData}")

        val users = userDao.getAllUsers()
        users.map{user->
            User(username = user.username, password = user.password, liked = user.liked?.lineSequence()?.toList())
        }.toMutableList()
        var userExist = false
        for(i in users)
        {
            if(i.username==username)
            {
                loginStateService.setUser(i.username,true)
                //LoginState.username = i.username
                userExist=true
                break
            }
        }
        if(!userExist)
        {
            try {
                userDao.insertUser(Users(users.size+1, mutableListOf<Int>().joinToString(),password,username))
                loginStateService.setUser(username,true)
                //LoginState.username=username
                true
            }catch (e:Exception)
            {
                throw e
            }

        }
        else{
            true
        }
    }

    override suspend fun downloadVideo(): MutableList<FilmsDetails> = withContext(Dispatchers.IO) {


            Log.i("loadfind","${this@LoadOfflineData}")
            val films = filmDao.getAllFilms()
            films.map{film ->
                FilmsDetails(url = film.url, name= film.title, owner=film.owner)
            }.toMutableList()


    }

    override suspend fun downloadUserVideo(username: String?): MutableList<FilmsDetailsWithThumbnail> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadUserLikedVideo(username: String?): MutableList<FilmsDetailsWithThumbnail> {
        TODO("Not yet implemented")
    }

    override suspend fun downloadExactVideo(videoNumer: String?): FilmsDetailsWithThumbnail {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVideo(film: FilmsDetailsWithThumbnail): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getUserName(): String? {
        try{
            return userDao.getUsername(loginStateService.username)
        }catch (e:Exception)
        {
            throw e
        }

    }

    override suspend fun deleteUser(): Boolean {
        try{
            val returninfo = userDao.deleteUser(loginStateService.username)
            return true
        }catch (e:Exception)
        {


            throw e
        }
    }

    override suspend fun likeVideo(filmnumber: Int, add: Boolean): Boolean {
        val liked = userDao.selectliked(loginStateService.username)
        val updatedliked = liked?.split(",")?.mapNotNull { it.toIntOrNull() }?.toMutableList()
        if(!add)
        {
            try{
                updatedliked?.remove(filmnumber)
                userDao.updateliked(loginStateService.username,updatedliked?.joinToString(",")?:"")
                return true
            }catch (e:Exception){ throw e}

        }
        else{
try {
    updatedliked?.add(filmnumber)
    userDao.updateliked(loginStateService.username,updatedliked?.joinToString(",")?:"")
    return true
}catch (e:Exception)
{
    throw e
}

        }
    }

    override fun getDataAsList(dataSnapshot: DataSnapshot): List<String> {
        TODO("Not yet implemented")
    }

    override suspend fun addComment(filmnumber: Int, username: String, comment: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun downloadComments(filmnumber: Int): MutableList<CombinedComments> {
        TODO("Not yet implemented")
    }

    override suspend fun addFilmToStorage(
        url: String?,
        username: String,
        fimTitle: String,
        progressListener: (Float) -> Unit,
        isStopped: () -> Boolean
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun addNewFilmToTest(newFilm: String, filmTitle: String, it: String): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        @Volatile private var instance: LoadOfflineData? = null

//        fun getInstance(): LoadOfflineData =
//            instance ?: synchronized(this) {
//                instance ?: LoadOfflineData(LoginStateService(sharedPreferences)).also { instance = it }
//            }
    }
}


/*
object TestObject{


   val testRepository = LoadOfflineData(LoginStateService(null))

}*/