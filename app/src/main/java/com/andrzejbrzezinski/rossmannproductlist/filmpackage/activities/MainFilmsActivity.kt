package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.activities.MainActivity
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityAddFilmBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMainFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.NavHeaderBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ConnectionFilmsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFilmsActivity : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

     val binding : ActivityMainFilmsBinding by lazy{
        ActivityMainFilmsBinding.inflate(layoutInflater)
    }

    val viewModel by viewModels<ConnectionFilmsViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_foreground)
        drawerLayout=binding.drawerLayout
        navigationView=binding.navView


        navigationView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId)
            {
                R.id.nav_item1->{

                }
                R.id.nav_item2 -> {
                    viewModel.logGout()
                    finish()
                    //onBackPressed()
                }
                R.id.nav_item3 -> {
                    openFileChooser()
                }
                R.id.nav_item4->{
                    showMyVideosFragment()
                    drawerLayout.closeDrawers()
                }
            }
            menuItem.isChecked = true

            true
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FilmsFragment())
                .commit()
        }
    }
    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "video/*"
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    companion object {
        private const val PICK_VIDEO_REQUEST = 1
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            val selectedVideoUri = data.data
//            val intent = Intent(this, AddFilmFragment::class.java).apply {
//                putExtra("video_uri", selectedVideoUri.toString())
//            }
//            addFilmResultLauncher.launch(intent)
            showAddFilmsFragment(selectedVideoUri.toString())

        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
            R.id.action_main_activity ->{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        super.onResume()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }


        //viewModel.loadData(binding.videopager.currentItem+1)

    }

    fun showFilmsFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, FilmsFragment())
            .commit()

    }
    fun showMyVideosFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, MyVideosFragment())
            .addToBackStack(null)
            .commit()
    }
    fun showAddCommentsFragment(filmNumber :Int) {
        val addCommentFragment = AddCommentFragment().apply {
            arguments = Bundle().apply {
                putInt("VIDEO_NUMBER", filmNumber)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addCommentFragment)
            .addToBackStack(null)
            .commit()
    }

    fun showAddFilmsFragment(filmUrl:String) {
        val addFilmFragment = AddFilmFragment().apply {
            arguments = Bundle().apply {
                putString("video_uri", filmUrl)
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, addFilmFragment)
            .addToBackStack(null)
            .commit()
    }

}