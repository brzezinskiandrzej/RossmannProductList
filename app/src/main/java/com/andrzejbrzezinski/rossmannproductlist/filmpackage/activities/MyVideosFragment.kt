package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetailsWithThumbnail
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMainFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMyVideosBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.NavHeaderBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters.MyVideosAdapter
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.MyVideosViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewStateMyVideosLoading
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyVideosFragment: Fragment(),MyVideosAdapter.OnFilmInteractionListener {
    val viewModel by viewModels<MyVideosViewModel>()
    private var _binding: ActivityMyVideosBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingBackground: FrameLayout
    private var allFilms: MutableList<FilmsDetailsWithThumbnail> = mutableListOf()
    private lateinit var myVideosAdapter:MyVideosAdapter
    private val bindingMainFilms : ActivityMainFilmsBinding by lazy{
        ActivityMainFilmsBinding.inflate(layoutInflater)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = ActivityMyVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val toolbar = binding.toolbar
//        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
//        val collapsingToolbar = binding.collapsingToolbar



        myVideosAdapter= MyVideosAdapter(this)
        progressBar = binding.loadingSpinner
        loadingBackground = binding.loadingBackground
        binding.myVideosList.apply {
            layoutManager=GridLayoutManager(context,2)
            adapter=myVideosAdapter
        }
        viewModel.viewstate.observe(viewLifecycleOwner, Observer {
            when (it) {
                ViewStateMyVideosLoading.Loading -> Loading()
                is ViewStateMyVideosLoading.ShowData -> {
                    displayData(it)
                }

                is ViewStateMyVideosLoading.Error -> showError(it.message)
                is ViewStateMyVideosLoading.ShowUpadtedData -> displayUpdatedData(it)
            }
        })


//

        binding.menuIcon.setOnClickListener { view->
            showPopupMenu(view)
        }


        binding.myVideosList.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerview: RecyclerView, dx: Int, dy: Int){
                super.onScrolled(recyclerview, dx, dy)
                if(dy>0){
                    val layout = binding.myVideosList.layoutManager as GridLayoutManager
                    val visible = layout.childCount
                    val total = layout.itemCount
                    val past = layout.findFirstVisibleItemPosition()
                    viewModel.checkIfShouldLoadMore(dy,visible,total,past,allFilms)

                }

            }
        })


    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showPopupMenu(view: View?) {
        val popup = PopupMenu(context, view)
            popup.menuInflater.inflate(R.menu.sections_menu, popup.menu)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.my_videos -> {
                        binding.sectionIcon.setImageResource(R.drawable.ic_my_profile_media_foreground)
                        binding.textView.text = "My Videos"
                        showLoadingWithAnimation()
                        viewModel.loadData()
                        true
                    }
                    R.id.liked_videos -> {
                        binding.sectionIcon.setImageResource(R.drawable.ic_menu_liked_media_foreground)
                        binding.textView.text = "Liked Videos"
                        showLoadingWithAnimation()
                        viewModel.loadLikedFilms()
                        true
                    }
                    else -> false
                }

            }
            for (i in 0 until popup.menu.size()) {
                val menuItem = popup.menu.getItem(i)
                val spanString = SpannableString(menuItem.title.toString())
                spanString.setSpan(ForegroundColorSpan(Color.BLACK), 0, spanString.length, 0)
                menuItem.title = spanString
            }

        popup.show()


    }

    private fun displayUpdatedData(it: ViewStateMyVideosLoading.ShowUpadtedData) {
        allFilms=it.films
        myVideosAdapter.updateList(allFilms.take(it.howManyToLoad))


    }

    private fun showError(message: String) {
        Timber.w(message)
    }

    private fun displayData(it: ViewStateMyVideosLoading.ShowData) {
        allFilms=it.films

        (activity as? MainFilmsActivity)?.let { activity ->
            val mainActivityBinding = activity.binding
            val headerView = mainActivityBinding.navView.getHeaderView(0)
            val headerBinding = NavHeaderBinding.bind(headerView)
            headerBinding.currentUser.text = "User: ${it.films[0].owner.toString()}"
        }

        myVideosAdapter.updateList(allFilms.take(it.howManyToLoad))

        hideLoadingWithAnimation()
    }

    private fun Loading() {
        loadingBackground.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingWithAnimation() {
        loadingBackground.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    loadingBackground.visibility = View.GONE
                }
            })
        progressBar.visibility = View.GONE
    }
    private fun showLoadingWithAnimation() {
        loadingBackground.alpha = 0f
        loadingBackground.visibility = View.VISIBLE
        loadingBackground.animate()
            .alpha(1f)
            .setDuration(500)
            .setListener(null)
            .start()
        progressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDeleteFilm(film: FilmsDetailsWithThumbnail) {
        viewModel.deleteVideo(film)
        val index = allFilms.indexOf(film)
        if (index != -1) {
            allFilms.removeAt(index)
            myVideosAdapter.notifyItemRemoved(index)
            myVideosAdapter.notifyItemRangeChanged(index, allFilms.size - index)
        }
    }
}