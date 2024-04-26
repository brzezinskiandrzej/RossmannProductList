package com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _commentsRefreshTrigger = MutableLiveData<String?>()
    val commentsRefreshTrigger: LiveData<String?> = _commentsRefreshTrigger

    fun triggerCommentsRefresh(refresh: String?) {
        _commentsRefreshTrigger.value = refresh
    }
}
