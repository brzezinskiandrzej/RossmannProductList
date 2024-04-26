package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadOfflineData

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginStateService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject
sealed class ViewStateComment {
    object ShowCommentForm : ViewStateComment()
    object WaitingForComment : ViewStateComment()
    data class CommentAddSuccess(val username:String) : ViewStateComment()
    data class Error(val message: String) : ViewStateComment()
}
@HiltViewModel
class CommentsViewModel @Inject constructor (@ApplicationContext private val context: Context, private val loadDataProvider: LoadDataProvider, private val loginStateService : LoginStateService) : ViewModel() {
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    init {
        //TestObject.testRepository.init(context)
        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
    }
    private val _loginViewState = MutableLiveData<ViewStateComment>()
    val loginViewState: LiveData<ViewStateComment> = _loginViewState
    var connection:Boolean = false
    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }
    fun addComment(filmnumber: Int,comment:String)
    {
        viewModelScope.launch {
            _loginViewState.value = ViewStateComment.WaitingForComment
            try{
                loadData.getUserName()?.let { loadData.addComment(filmnumber, it,comment) }
                _loginViewState.value=ViewStateComment.CommentAddSuccess(LoginState.username)
            }
            catch(e:Exception)
            {
                e.printStackTrace()
                _loginViewState.value=ViewStateComment.Error(e.message ?: "Unknown error")
            }
        }
    }
}
/*object TestObject{
    val testRepository = LoadOfflineData(LoginStateService())

}*/