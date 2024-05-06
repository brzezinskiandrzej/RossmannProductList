package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityLoginUserBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ConnectionUsersViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewState
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class LoginUserActivity : AppCompatActivity() {
    private val viewModel:ConnectionUsersViewModel by viewModels()
    private lateinit var binding : ActivityLoginUserBinding
    private val mFirebaseAnalytics: FirebaseAnalytics? = null
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onResume() {
        super.onResume()
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "my screen classs")
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "my custom screen name")
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
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