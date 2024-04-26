package com.andrzejbrzezinski.rossmannproductlist.objects

import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginStateService@Inject constructor(private val sharedPreferences: SharedPreferences?){


    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }


    val account: Boolean?
        get() = sharedPreferences?.getBoolean(KEY_IS_LOGGED_IN, false)

    val username: String
        get() = sharedPreferences?.getString(KEY_USERNAME, "") ?: ""


    fun setUser(username: String, isAccountActive: Boolean) {
        sharedPreferences?.edit()?.apply {
            putString(KEY_USERNAME, username)
            putBoolean(KEY_IS_LOGGED_IN, isAccountActive)
            apply()
        }
    }
}