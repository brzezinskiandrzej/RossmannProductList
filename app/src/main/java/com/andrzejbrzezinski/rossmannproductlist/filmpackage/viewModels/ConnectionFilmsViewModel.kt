package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import android.content.Context
import android.view.View
import android.view.animation.Transformation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrzejbrzezinski.rossmannproductlist.CombinedComments
import com.andrzejbrzezinski.rossmannproductlist.CombinedFilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.CommentsQuantity
import com.andrzejbrzezinski.rossmannproductlist.FilmWithComments
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider
import com.andrzejbrzezinski.rossmannproductlist.firebaseProvider.Firebaseprovider
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class ViewStateFilmLoading {
    object Loading : ViewStateFilmLoading()
    data class ShowData(val films: List<FilmWithComments>, val commentsLoading:Boolean) : ViewStateFilmLoading()
    data class Error(val message: String) : ViewStateFilmLoading()
}
@HiltViewModel
class ConnectionFilmsViewModel @Inject constructor (@ApplicationContext private val context: Context, private val loadDataProvider: LoadDataProvider,private val loginStateService :LoginStateService) : ViewModel() {
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    //private val loadFirebaseData = LoadFirebaseData()
    //private val loadOfflineData = LoadOfflineData.getInstance()
    init {
        //TestObject.testRepository.init(context)
        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
        //loadFilmsData()
        //loadUserName()
        loadData()

    }
//    private val _loadDataViewState = MutableLiveData<ViewStateFilmLoading>()
//    val loadDataViewState: LiveData<ViewStateFilmLoading> = _loadDataViewState
//    private val _combinedLoadData = MediatorLiveData<List<CombinedFilmsDetails>>()
//    val combinedLoadData: LiveData<List<CombinedFilmsDetails>> = _combinedLoadData
    private val _viewstate = MutableLiveData<ViewStateFilmLoading>(ViewStateFilmLoading.Loading)
    val viewstate:LiveData<ViewStateFilmLoading> =_viewstate
//    private val _videosUrl = MutableLiveData<List<FilmsDetails>>()
//    val videosUrl: LiveData<List<FilmsDetails>> = _videosUrl
//    private val _commentsData = MutableLiveData<List<CombinedComments>>()
//    val commentsData: LiveData<List<CombinedComments>> = _commentsData
    var userName:MutableLiveData<String> = MutableLiveData()
    var connection:Boolean = false
    var isUserInteraction:Boolean = true


    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }
    fun checkUserInteraction(state:Boolean)
    {
        isUserInteraction=state
    }
    fun loadCommentsData(filmnumber: Int)
    {
        viewModelScope.launch {
            try{

                val value=_viewstate.value
                if(value is ViewStateFilmLoading.ShowData)
                {

                    _viewstate.value=ViewStateFilmLoading.ShowData(value.films,true)
                    val commentslist=loadData.downloadComments(filmnumber)
                    value.films[filmnumber-1].comments=commentslist
                    _viewstate.value=ViewStateFilmLoading.ShowData(value.films,false)
                }
                //_commentsData.postValue(commentslist)
            }
            catch(e:Exception)
            {
                e.printStackTrace()
            }
        }
    }
//    fun loadFilmsData() {
//    viewModelScope.launch {
//
//    try{
//
//            val filmlist=loadData.downloadVideo()
//            _videosUrl.postValue(filmlist)
//
//
//
//
//
//        }
//    catch(e: Exception){
//        e.printStackTrace()
//    }}
//
//
//
//
///*
//        Firebaseprovider.firebase.getReference("test").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                val filmlist = mutableListOf<FilmsDetails>()
//                for (userSnapshot in dataSnapshot.children) {
//
//                    val name = userSnapshot.child("name").getValue(String::class.java)
//
//                    val url = userSnapshot.child("url").getValue(String::class.java)
//
//                    filmlist.add(FilmsDetails(url = url, name = name))
//
//
//                }
//
//                _videosUrl.postValue(filmlist)
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//
//                Log.w(ContentValues.TAG, "loadUsers:onCancelled", databaseError.toException())
//            }
//        })
//*/
//    }

    fun likeVideo(filmnumber: Int, add: Boolean) {

        viewModelScope.launch {
            try{

                    loadData.likeVideo(filmnumber,add)



            }catch(e:Exception)
            {
                e.printStackTrace()
            }
            finally {
                 Timber.i("UserInteraction Blocked LikedVideo function")
            }
        }
        /*val likedRef = Firebaseprovider.firebase.getReference("users/${LoginState.username}").child("liked")

        likedRef.get().addOnSuccessListener { dataSnapshot ->
            val currentLikedMovies = getDataAsList(dataSnapshot)
            val newLikedMovie = "Film$filmnumber"
            if (!add) {
                if (currentLikedMovies.contains(newLikedMovie)) {
                    var i = 0
                    for (userSnapshot in dataSnapshot.children) {

                        i = userSnapshot.getKey()?.toInt() ?: 0
                        val temp = "${userSnapshot.getValue(String::class.java)}"
                        //userSnapshot.child("$i").getValue(String::class.java)?.let { Log.w(TAG, it) }
                        if (temp == newLikedMovie) {
                            likedRef.child("$i").removeValue()
                            Timber.d("Movie disliked succesfully")
                            break
                        }
                    }/*
                    likedRef.setValue(currentLikedMovies2).addOnSuccessListener {
                        Log.d("Firebase", "Movie disliked succesfully")
                    }.addOnFailureListener { exception ->
                        Log.e("Firebase", "Error while data update: ", exception)
                    }*/
                } else {

                    Timber.d("Movie isn't on liked list of this user")
                }
            } else {
                val updatedLikedMovies = currentLikedMovies.toMutableList().apply {
                    add(newLikedMovie)
                }

                likedRef.setValue(updatedLikedMovies).addOnSuccessListener {
                    Timber.d("Movie liked succesfully")
                }.addOnFailureListener { exception ->

                    Timber.e("Error while data update:${exception}")
                }
            }
        }.addOnFailureListener { exception ->
            Timber.e("Error while data read:$exception")
        }
*/

    }

    fun isLiked(currentvideo: Int, callback: (Boolean) -> Unit) {
        val currentactivevideo = "Film$currentvideo"
        val likedRef = Firebaseprovider.firebase.getReference("users/${loginStateService.username}").child("liked")
        likedRef.get().addOnSuccessListener { dataSnapshot ->
            callback.invoke(dataSnapshot.children.any { it.getValue(String::class.java) == currentactivevideo })

        }


    }
    fun loadUserName(){
        viewModelScope.launch {
            try {


                    val username=loadData.getUserName()
                    userName.postValue(username)




            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        /*
        val activeuser =Firebaseprovider.firebase.getReference("users/${LoginState.username}").child("username")
        activeuser.get().addOnSuccessListener {snapshot->


            val name = snapshot.getValue(String::class.java)
            userName.postValue(name)

        }

            .addOnFailureListener{
                Timber.e("Error on downloading user name")
            }

*/


    }
    fun deleteUser(){
        viewModelScope.launch {
            try {

                    loadData.deleteUser()


                loginStateService.setUser("",false)
            }catch (e:Exception){
                e.printStackTrace()

            }
        }
        /*val activeuser = Firebaseprovider.firebase.getReference("users").child(LoginState.username)
            activeuser.removeValue()
            .addOnSuccessListener {
                Timber.e("User deleted successfully")
            }
            .addOnFailureListener { e ->
               Timber.e("Error on deleting user")
            }*/


    }

    fun logGout()
    {
        viewModelScope.launch {
            try {
                loginStateService.setUser("",false)
            }
            catch (e:Exception)
            {
                e.printStackTrace()
            }
        }
    }

    fun loadData()
    {
        viewModelScope.launch {

            try{

                val username=loadData.getUserName()
                val filmlist=loadData.downloadVideo()
                val films=filmlist.map {
                    FilmWithComments(it,null,username)
                }
                _viewstate.value= ViewStateFilmLoading.ShowData(films,false)
                }
            catch(e:Exception)
            {
                e.printStackTrace()
                _viewstate.value=ViewStateFilmLoading.Error(e.message ?: "Error while downloading films data")
            }
        }
    }




}
