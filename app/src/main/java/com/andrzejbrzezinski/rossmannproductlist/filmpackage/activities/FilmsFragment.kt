package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMainFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.NavHeaderBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters.CommentsAdapter
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters.VideoPagerAdapter
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ConnectionFilmsViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.SharedViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewStateFilmLoading
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.UUID

@AndroidEntryPoint
class FilmsFragment : Fragment() {

    val viewModel by viewModels<ConnectionFilmsViewModel>()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private var _binding: ActivityFilmsBinding? = null
    private val binding get() = _binding!!
    private val bindingMainFilms : ActivityMainFilmsBinding by lazy{
        ActivityMainFilmsBinding.inflate(layoutInflater)
    }
    val adapter by lazy{
        VideoPagerAdapter()
    }
    val commentsAdapter by lazy{
        CommentsAdapter()
    }
    var currentVideoIndex=0
    var commentsQuantity=0;
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingBackground: FrameLayout
    private lateinit var commentsLoadingSpinner: ProgressBar
    private lateinit var commentsloadingBackground : FrameLayout
    private lateinit var addFilmResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var addCommentResultLauncher: ActivityResultLauncher<Intent>
    private var userInteracted = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = ActivityFilmsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //videobinding= ItemVideoBinding.inflate(layoutInflater)

        //setContentView(binding.root)
        progressBar = binding.loadingSpinner
        loadingBackground = binding.loadingBackground
        commentsLoadingSpinner = binding.loadingSpinnerComments
        commentsloadingBackground = binding.loadingBackgroundComments
        // Firebase.analytics.logEvent("data_log",null)
//        var adapter= VideoPagerAdapter(emptyList())
//        var commentsAdapter = CommentsAdapter(emptyList())
        //viewModel.loadCommentsData(1)

        sharedViewModel.commentsRefreshTrigger.observe(viewLifecycleOwner) { refresh ->

           if(refresh!=null)
           {
               if (refresh=="comment") {
                   viewModel.loadCommentsData(currentVideoIndex + 1)

               }
               else if(refresh=="film")
               {
                   viewModel.loadData()
                   viewModel.loadCommentsData(currentVideoIndex + 1)
               }

               sharedViewModel.triggerCommentsRefresh(null)
           }

        }
        viewModel.viewstate.observe(viewLifecycleOwner, Observer {
            when (it) {
                ViewStateFilmLoading.Loading -> Loading()
                is ViewStateFilmLoading.ShowData -> {
                    hideLoadingWithAnimation()
                    displayData(it)
                }

                is ViewStateFilmLoading.Error -> showError(it.message)

            }
        })
        // val loadOfflineData = LoadOfflineData()
        //LoadOfflineData.getInstance().init(applicationContext)


//
//        viewModel.combinedLoadData.observe(this,Observer{data->
//            val combineddata = data.firstOrNull()
//            if(combineddata != null)
//            {
//                val films=combineddata.filmsDetails
//                adapter.videoList=films
//                adapter.submitList(films)
//                commentsQuantity=films.size-1
//                val comments = combineddata.combinedComments
//                commentsAdapter.commentsList=comments
//                commentsAdapter.submitList(comments)
//                if(comments.isNotEmpty())
//                    binding.commentsQuantity.text="Comments : ${comments.size}"
//                else
//                    binding.commentsQuantity.text="Comments not available"
//                val username = combineddata.username
//                val headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))
//
//                headerBinding.currentUser.text="User: $username"
//            }
//
//
//        })

        //val internetConnection = InternetConnectionCondition(this)
        //viewModel.checkInternetConnection(internetConnection.isNetworkAvailable())
        /* viewModel.videosUrl.observe(this, Observer { films->


            adapter.videoList=films



            adapter.submitList(films)
            commentsQuantity=films.size-1

        })
        viewModel.commentsData.observe(this,Observer{comments->

            commentsAdapter.commentsList=comments
            commentsAdapter.submitList(comments)
            if(comments.isNotEmpty())
                binding.commentsQuantity.text="Comments : ${comments.size}"
            else
                binding.commentsQuantity.text="Comments not available"
        })
        viewModel.userName.observe(this, Observer {name->
        //binding.currentusername.text="Current Username: $name"
            val headerBinding = NavHeaderBinding.bind(binding.navView.getHeaderView(0))

            headerBinding.currentUser.text="User: $name"

        })*/
        binding.videopager.adapter = adapter
        binding.commentsList.adapter = commentsAdapter
        binding.commentsList.layoutManager = LinearLayoutManager(context)
        fun checkifchangevisibility() {
            viewModel.isLiked(binding.videopager.currentItem+1) { isLiked ->

                binding.videoliked.setOnCheckedChangeListener(null)
                binding.videoliked.setChecked(isLiked)
                setLikedCheckboxListener()

            }
            if (currentVideoIndex == adapter.videoList.size - 1) {
                binding.nextvideo.visibility = View.INVISIBLE
                binding.previousvideo.visibility = View.VISIBLE
            } else if (currentVideoIndex == 0) {
                binding.previousvideo.visibility = View.INVISIBLE
                binding.nextvideo.visibility = View.VISIBLE
            } else {
                binding.previousvideo.visibility = View.VISIBLE
                binding.nextvideo.visibility = View.VISIBLE
            }
        }
        binding.nextvideo.setOnClickListener {
            if (adapter.videoList.isEmpty()) return@setOnClickListener
            binding.videopager.currentItem += 1


            /*val videoView = (binding.videopager.get(0) as RecyclerView).layoutManager?.findViewByPosition(binding.videopager.currentItem)
            if(videoView!=null){
                var videobinding : ItemVideoBinding = ItemVideoBinding.bind(videoView)
                videobinding.videoview.start()
            }*/


            /*videobinding.videoview.apply {
                requestFocus()
                start()
            }*/
        }
        binding.videopager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentVideoIndex = position
                checkifchangevisibility()
                viewModel.loadCommentsData(position + 1)
                // viewModel.loadData(position+1)


            }
        })


        binding.previousvideo.setOnClickListener {
            if (adapter.videoList.isEmpty()) return@setOnClickListener
            binding.videopager.currentItem -= 1


            /*videobinding.videoview.apply {
                requestFocus()
                start()
            }*/
        }
        setLikedCheckboxListener()

        binding.buttonAddComment.setOnClickListener {

            (activity as MainFilmsActivity).showAddCommentsFragment(binding.videopager.currentItem+1)

            //binding.root.context.startActivity(intent)
        }
        /*
       val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference("test")
        val videosUrl = mutableListOf<String>()
        val videostitle = mutableListOf<String>()
        var currentVideoIndex = 0
        firebase.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                videosUrl.clear()
                for (userSnapshot in dataSnapshot.children) {
                    val name = userSnapshot.child("name").getValue(String::class.java)
                    name?.let{videostitle.add(it)}
                    val url = userSnapshot.child("url").getValue(String::class.java)
                    url?.let{videosUrl.add(it)}






                }
                Log.i(TAG,videosUrl[currentVideoIndex])
                binding.videotitle.text=videostitle[currentVideoIndex]
                binding.videoview.apply{
                    (setVideoURI(Uri.parse(videosUrl[currentVideoIndex])))
                    requestFocus()
                    start()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Obsłuż błąd
                Log.w(TAG, "loadUsers:onCancelled", databaseError.toException())
            }
        })

    binding.nextvideo.setOnClickListener{
        if (videosUrl.isEmpty()) return@setOnClickListener
        currentVideoIndex = (currentVideoIndex+1)%videosUrl.size
        binding.videoview.apply{
            (setVideoURI(Uri.parse(videosUrl[currentVideoIndex])))
            requestFocus()
            start()
        }
        binding.videotitle.text=videostitle[currentVideoIndex]
    }*/
        val sharedPreferences = context?.getSharedPreferences("UploadWorkPreferences", Context.MODE_PRIVATE)
        val workIdString = sharedPreferences?.getString("uploadWorkId", null)
        val workId = if (workIdString != null) UUID.fromString(workIdString) else null
        if (workId != null) {
            WorkManager.getInstance(requireContext())
                .getWorkInfoByIdLiveData(workId)
                .observe(viewLifecycleOwner) { workInfo ->
                    if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {
                        sharedViewModel.triggerCommentsRefresh("film")
                    }
                }
        }
    }

    private fun displayData(it: ViewStateFilmLoading.ShowData) {
        //val films=combineddata.filmsDetails
        adapter.videoList = it.films
        adapter.submitList(it.films)
        if (it.commentsLoading == false) {

            commentsQuantity = it.films.size - 1
            val comments = it.films[currentVideoIndex].comments
            commentsAdapter.commentsList = comments
            commentsAdapter.submitList(comments)
            if (comments?.isNotEmpty() == true)
                binding.commentsQuantity.text = "Comments : ${comments.size}"
            else
                binding.commentsQuantity.text = "Comments not available"

            commentsloadingBackground.visibility = View.GONE
            commentsLoadingSpinner.visibility = View.GONE
        } else {
            commentsloadingBackground.visibility = View.VISIBLE
            commentsLoadingSpinner.visibility = View.VISIBLE
        }
        val username = it.films[currentVideoIndex].username
        val headerBinding = NavHeaderBinding.bind(bindingMainFilms.navView.getHeaderView(0))

        headerBinding.currentUser.text = "User: $username"
    }

    private fun showError(message: String) {
        Timber.w(message)
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
    private fun hideLoadingWithAnimationComments() {
        commentsloadingBackground.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    commentsloadingBackground.visibility = View.GONE
                }
            })
        commentsLoadingSpinner.visibility = View.GONE
    }
    private fun setLikedCheckboxListener() {
        binding.videoliked.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                userInteracted = true
            }
            false
        }
        binding.videoliked.setOnCheckedChangeListener { _, isChecked ->
            if (!userInteracted) {
                return@setOnCheckedChangeListener
            }
            if (isChecked) {

                viewModel.likeVideo(binding.videopager.currentItem+1, true)
            } else {
                viewModel.likeVideo(binding.videopager.currentItem+1, false)

            }
            userInteracted=false
            viewModel.checkUserInteraction(true)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}