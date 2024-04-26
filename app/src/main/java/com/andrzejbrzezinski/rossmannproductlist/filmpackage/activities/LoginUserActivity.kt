package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.andrzejbrzezinski.rossmannproductlist.User
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityLoginUserBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.connection.LoadOfflineData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.dataclasses.Films
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.room.AppDatabase

import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ConnectionUsersViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewState
import com.andrzejbrzezinski.rossmannproductlist.objects.LoginState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import kotlinx.coroutines.*
@AndroidEntryPoint
class LoginUserActivity : AppCompatActivity() {
    private val viewModel:ConnectionUsersViewModel by viewModels()
    private lateinit var binding : ActivityLoginUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginUserBinding.inflate(layoutInflater)
        setContentView(binding.root)



        viewModel.loginViewState.observe(this, Observer { state ->

            when (state) {
                is ViewState.ShowLoginForm -> showLoginForm()
                is ViewState.Loading -> showLoading()
                is ViewState.LoginSuccess -> navigateToNextScreen(state.username)
                is ViewState.Error -> showError(state.message)

            }
        })
        binding.submitButton.setOnClickListener {
            if(!binding.username.text.isEmpty() && !binding.password.text.isEmpty()){


        viewModel.registerButtonClick(binding.username.text.toString(),binding.password.text.toString(), listOf())






            }

        }
    }


    private fun showLoginForm() {

    }

    private fun showLoading() {

    }

    private fun navigateToNextScreen(user: String) {
        val intent = Intent(binding.root.context, MainFilmsActivity::class.java)
        binding.root.context.startActivity(intent)
        //LoginState.account=true

    }

    private fun showError(message: String) {
        Timber.w(message)
    }
}