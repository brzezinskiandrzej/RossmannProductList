package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities.MainFilmsActivity
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.objects.SnackbarManager
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.workers.UploadFilmWorker
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class ViewStateAddFilm{
    object Loading:ViewStateAddFilm()
    object BeforeSending:ViewStateAddFilm()
    object SendRequest:ViewStateAddFilm()
    object FilmAdded:ViewStateAddFilm()
    data class Error(val message: String):ViewStateAddFilm()
}

@HiltViewModel
class AddFilmViewModel @Inject constructor (@ApplicationContext private val context: Context, private val loadDataProvider: LoadDataProvider, private val loginStateService : LoginStateService) : ViewModel() {
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    init {

        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
    }

    private val _addFilmViewState = MutableLiveData<ViewStateAddFilm>()
    val addFilmViewState: LiveData<ViewStateAddFilm> = _addFilmViewState
    private val _workInfoLiveData = MutableLiveData<UUID?>()
    val workInfoLiveData: LiveData<UUID?> = _workInfoLiveData
    var connection:Boolean = false
    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }
    fun addFilm(filmUrl: String?,filmTitle: String)
    {
        _addFilmViewState.value=ViewStateAddFilm.SendRequest

        val workData = workDataOf("filmUrl" to filmUrl, "filmTitle" to filmTitle)

        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadFilmWorker>()
            .setInputData(workData)
            .build()

        WorkManager.getInstance(context).enqueue(uploadWorkRequest)
        _workInfoLiveData.value=uploadWorkRequest.id
        val sharedPreferences = context.getSharedPreferences("UploadWorkPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("uploadWorkId", uploadWorkRequest.id.toString()).apply()
        observeWorkerResult(uploadWorkRequest.id)


//        viewModelScope.launch {
//            _addFilmViewState.value = ViewStateAddFilm.BeforeSending
//            try{
//                loadData.getUserName()?.let {
//                    val newFilm=loadData.addFilmToStorage(filmUrl,it,filmTitle)
//                    loadData.addNewFilmToTest(newFilm,filmTitle,it)
//                    _addFilmViewState.value=ViewStateAddFilm.FilmAdded
//                }
//            }catch(e:Exception)
//            {
//                e.printStackTrace()
//                _addFilmViewState.value=ViewStateAddFilm.Error(e.message ?: "Unknown error during Film Adding")
//            }
//        }
    }
    private fun observeWorkerResult(workId: UUID) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workId)
            .observeForever { workInfo ->
                if (workInfo != null) {
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {

                            _addFilmViewState.value=ViewStateAddFilm.FilmAdded
                            WorkManager.getInstance(context).pruneWork()
                        }
                        WorkInfo.State.FAILED -> {
                            _addFilmViewState.value=ViewStateAddFilm.Error("Unknown error during Film Adding")
                        }

                        else -> {}
                    }
                }
            }
    }



}