package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andrzejbrzezinski.rossmannproductlist.User
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadFirebaseData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadOfflineData

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider

import com.andrzejbrzezinski.rossmannproductlist.firebaseProvider.Firebaseprovider
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

sealed class ViewState {
    object ShowLoginForm : ViewState()
    object Loading : ViewState()
    data class LoginSuccess(val username:String) : ViewState()
    data class Error(val message: String) : ViewState()
}
@HiltViewModel
class ConnectionUsersViewModel @Inject constructor(@ApplicationContext private val context: Context,private val loadDataProvider: LoadDataProvider): ViewModel() {
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    private lateinit var auth: FirebaseAuth
    init{
        //TestObject.testRepository.init(context)
        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
    }
    private val _loginViewState = MutableLiveData<ViewState>()
    val loginViewState: LiveData<ViewState> = _loginViewState
   /* private val loadFirebaseData:ILoadData = LoadFirebaseData()
    private val loadOfflineData = LoadOfflineData()*/

    var connection:Boolean = false
    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }
    fun registerButtonClick(username:String, password:String, liked:List<String>){
            auth= Firebase.auth
            viewModelScope.launch {
                _loginViewState.value = ViewState.Loading
                try {

                    if(loadData.loginAnonymously(auth))
                    loadData.loginAndGetUserData(username,password,liked)
                        _loginViewState.value=ViewState.LoginSuccess(LoginState.username)





                } catch (e: Exception) {
                    e.printStackTrace()
                    _loginViewState.value=ViewState.Error(e.message ?: "Unknown error")
                }
            }


/*
            val connection = Firebaseprovider.firebase.getReference("users")
            connection.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userlist = mutableListOf<User>()
                    for (userSnapshot in snapshot.children) {
                        if (userSnapshot.child("username")
                                .getValue(String::class.java) == username
                        ) {
                            LoginState.username = userSnapshot.key.toString()
                            _loginViewState.value =
                                ViewState.LoginSuccess()
                            cont.resume(ViewState.LoginSuccess())
                            return
                        }

                    }
                    val usersCount = snapshot.childrenCount
                    val nextUserId = usersCount + 1
                    val newUser = User(username, password, liked)
                    connection.child("User$nextUserId").setValue(newUser)
                    _loginViewState.value = ViewState.LoginSuccess()
                    LoginState.username = "User$nextUserId"
                    cont.resume(ViewState.LoginSuccess())


                }

                override fun onCancelled(error: DatabaseError) {

                    _loginViewState.value = ViewState.Error("loadUsers:onCancelled")
                    cont.resumeWithException(Exception("loadUsers:onCancelled"))
                }
            })*/
        }
        fun showLogin() {
            _loginViewState.value = ViewState.ShowLoginForm
        }



}


