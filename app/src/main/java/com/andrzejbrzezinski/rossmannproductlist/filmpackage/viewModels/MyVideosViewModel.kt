package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

sealed class ViewStateMyVideosLoading {
    object Loading : ViewStateMyVideosLoading()
    data class ShowData(val films: MutableList<FilmsDetailsWithThumbnail>,val howManyToLoad: Int) : ViewStateMyVideosLoading()
    data class ShowUpadtedData(val films: MutableList<FilmsDetailsWithThumbnail>, val howManyToLoad: Int):ViewStateMyVideosLoading()
    data class Error(val message: String) : ViewStateMyVideosLoading()
}

@HiltViewModel
class MyVideosViewModel @Inject constructor (@ApplicationContext private val context: Context, private val loadDataProvider: LoadDataProvider, private val loginStateService : LoginStateService) : ViewModel() {
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    var connection:Boolean = false
    var howManyToLoad:Int=8
    val mutex: Mutex = Mutex()
    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }
    private var myVideosLoaded = false
    private var likedVideosLoaded = false
    private val _viewstate = MutableLiveData<ViewStateMyVideosLoading>(ViewStateMyVideosLoading.Loading)
    val viewstate: LiveData<ViewStateMyVideosLoading> =_viewstate
    private var myVideos: MutableList<FilmsDetailsWithThumbnail>? = null
    private var likedVideos: MutableList<FilmsDetailsWithThumbnail>? = null
    init {
        //TestObject.testRepository.init(context)
        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
        //loadFilmsData()
        //loadUserName()
        loadData()

    }

    fun loadData()
    {
        viewModelScope.launch {

            try{
                if(myVideosLoaded)
                {
                    myVideos?.let {
                        _viewstate.value = ViewStateMyVideosLoading.ShowData(it,howManyToLoad)
                    }
                }
                else {
                    val username = loadData.getUserName()
                    val filmlist = loadData.downloadUserVideo(username)
//                val filmlist2=loadThumbnails(filmlist,howManyToLoad)
                    myVideos = filmlist
                    myVideosLoaded = true
                    _viewstate.value = ViewStateMyVideosLoading.ShowData(
                        loadThumbnails(filmlist, howManyToLoad),
                        howManyToLoad
                    )
                }
            }
            catch(e:Exception)
            {
                e.printStackTrace()
                _viewstate.value=ViewStateMyVideosLoading.Error(e.message ?: "Error while downloading films data")
            }
        }
    }
    fun loadLikedFilms() {
        viewModelScope.launch {
            try{
                if(likedVideosLoaded)
                {
                    likedVideos?.let {
                        _viewstate.value = ViewStateMyVideosLoading.ShowData(it,howManyToLoad)
                    }
                }
                else{
                    val username = loadData.getUserName()
                    val filmlist=loadData.downloadUserLikedVideo(username)
                    likedVideos = filmlist
                    likedVideosLoaded = true
                    _viewstate.value = ViewStateMyVideosLoading.ShowData(
                        loadThumbnails(filmlist, howManyToLoad),
                        howManyToLoad
                    )
                }
            }
            catch (e:Exception)
            {
                e.printStackTrace()
                _viewstate.value=ViewStateMyVideosLoading.Error(e.message?:"Error while downloading liked films data")
            }
        }
    }
    fun checkIfShouldLoadMore(dy: Int, visibleItemCount: Int, totalItemCount: Int, firstVisibleItemPosition: Int,films:MutableList<FilmsDetailsWithThumbnail>) {
        viewModelScope.launch {
        if (dy > 0 && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount && totalItemCount<films.size) {
            howManyToLoad += 4
            Log.i("Numbers","visibleItemCount: ${visibleItemCount}, totalItemCount: ${totalItemCount}, firstVisibleItemPosition: ${firstVisibleItemPosition}" )

            mutex.withLock {
                _viewstate.value=ViewStateMyVideosLoading.ShowUpadtedData(
                    loadThumbnails(films,howManyToLoad),howManyToLoad)
        }


        }
    }}
    suspend fun loadThumbnails(filmlist: MutableList<FilmsDetailsWithThumbnail>,howManyToLoad:Int):MutableList<FilmsDetailsWithThumbnail> = withContext(Dispatchers.IO)
    {
        Timber.i(filmlist.toString())
        //val filmListWithThumbnail= mutableListOf<FilmsDetailsWithThumbnail>()

        val filmsToLoad = filmlist.filterIndexed { index, film -> film.thumbnail == null && index < howManyToLoad }

        val filmListAsync=filmsToLoad.map {film->
            async {
                var thumbnail:Bitmap? =getVideoThumbnail(film.url)
                    FilmsDetailsWithThumbnail(url=film.url,name=film.name, owner = film.owner,thumbnail = thumbnail)
            }
        }
        val loadedThumbnails = filmListAsync.awaitAll()
        loadedThumbnails.forEach { loadedFilm ->
            val index = filmlist.indexOfFirst { it.url == loadedFilm.url }
            if (index != -1) {
                filmlist[index] = loadedFilm
            }
        }

        return@withContext filmlist


    }
    suspend fun getVideoThumbnail(videoPath: String?): Bitmap? = withContext(Dispatchers.IO){
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(videoPath)
            return@withContext retriever.getFrameAtTime(10000000, MediaMetadataRetriever.OPTION_CLOSEST)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return@withContext null
    }
    fun deleteVideo(film: FilmsDetailsWithThumbnail){
        viewModelScope.launch {
            try {

                loadData.deleteVideo(film)



            }catch (e:Exception){
                e.printStackTrace()

            }
        }



    }
}