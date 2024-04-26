package com.andrzejbrzezinski.rossmannproductlist.filmpackage.activities

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.registerReceiver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.activities.MainActivity
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityAddFilmBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.objects.SnackbarManager
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.receivers.CancelUploadReceiver
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.AddFilmViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ConnectionFilmsViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.SharedViewModel
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.viewModels.ViewStateAddFilm
import dagger.hilt.android.AndroidEntryPoint

import timber.log.Timber
import java.util.Locale
import java.util.UUID
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class AddFilmFragment : Fragment() {
    private lateinit var videoView: VideoView
    private var _binding: ActivityAddFilmBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddFilmViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModelFilms : ConnectionFilmsViewModel by viewModels()
    private val updateHandler = Handler(Looper.getMainLooper())
    private val cancelUploadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val workId = intent?.getStringExtra("WORK_ID")
            workId?.let {
                WorkManager.getInstance(requireContext()).cancelWorkById(UUID.fromString(it))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val filter = IntentFilter("ACTION_CANCEL_UPLOAD")
        requireActivity().registerReceiver(cancelUploadReceiver, filter)
        _binding = ActivityAddFilmBinding.inflate(inflater, container, false)
        return binding.root
    }
    val updateRunnable = object : Runnable {
        override fun run() {
            if (isAdded && _binding != null) {
                binding.seekBar.progress = binding.addVideoView.currentPosition
                updateHandler.postDelayed(this, 1000)
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewModel.addFilmViewState.observe(viewLifecycleOwner, Observer {state->
            when(state)
            {
                is ViewStateAddFilm.Loading->showLoadingAnimation()
                is ViewStateAddFilm.BeforeSending->beforeSending()
                is ViewStateAddFilm.FilmAdded-> refreshScreen()
                is ViewStateAddFilm.Error->showError(state.message)
                is ViewStateAddFilm.SendRequest -> navigateToPreviousScreen()
            }
        })
        videoView = binding.addVideoView
        val videoUrl:String=arguments?.getString("video_uri") ?: ""
        val videoUri: Uri = Uri.parse(videoUrl)
        videoView.setVideoURI(videoUri)
        videoView.start()


        binding.addFilmButton.setOnClickListener{

            if(binding.filmtitle.text.isEmpty())
                binding.filmtitle.hint="Comment is required"
            else
            {
                viewModel.addFilm(videoUrl,binding.filmtitle.text.toString())
                SnackbarManager.showUploadProgress(requireActivity(),0)
                observeUploadProgress()
            }
        }


        fun View.fadeIn(duration: Long = 500) {
            this.visibility = View.VISIBLE
            this.alpha = 0f
            ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                this.duration = duration
                start()
            }
        }


        fun View.fadeOut(duration: Long = 500, endAction: (() -> Unit)? = null) {
            ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                this.duration = duration
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        this@fadeOut.visibility = View.GONE
                        endAction?.invoke()
                    }
                })
                start()
            }
        }
        //binding.videotitle.text = videourl?.name

        binding.startStopButton.setOnClickListener {
            if (binding.addVideoView.isPlaying) {
                binding.addVideoView.pause()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                binding.addVideoView.start()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_play)
            }
        }

        binding.seekBar.max = binding.addVideoView.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    binding.addVideoView.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.addVideoView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.videoOverlay.fadeIn()
                    binding.mediaPlayerControls.fadeIn()
                    binding.startStopButton.fadeIn()

                    v.postDelayed({
                        binding.videoOverlay.fadeOut()
                        binding.mediaPlayerControls.fadeOut()
                        binding.startStopButton.fadeOut()
                    }, 3000)

                    true
                }

                MotionEvent.ACTION_UP -> {

                    v.performClick()
                    true
                }

                else -> false
            }

        }


        fun formatTime(millis: Int): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis.toLong())
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) - TimeUnit.MINUTES.toSeconds(
                    minutes
                )
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }



        binding.addVideoView.setOnPreparedListener { mp ->
            val duration = binding.addVideoView.duration

            binding.totalTime.text = formatTime(duration)
            binding.seekBar.max = binding.addVideoView.duration
            updateHandler.postDelayed(updateRunnable, 1000)


        }
        binding.addVideoView.setOnCompletionListener {
            updateHandler.removeCallbacks(updateRunnable)
        }
        updateHandler.post(updateRunnable)
    }



    private fun observeUploadProgress() {
        context?.let {
            WorkManager.getInstance(it).getWorkInfosByTagLiveData("uploadTag").observe(viewLifecycleOwner, Observer { workInfos ->
                workInfos?.let {
                    for (workInfo in it) {
                        if (workInfo.state == WorkInfo.State.RUNNING) {
                            val progress = workInfo.progress.getInt("Progress", 0)
                            updateNotification(progress)
                        } else if (workInfos != null && workInfo.state.isFinished) {
                            completeNotification()
                        }
                    }
                }

            })
        }
    }
    fun updateNotification(progress: Int) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val builder = NotificationCompat.Builder(requireContext(), MainActivity.CHANNEL_ID)
            .setContentTitle("Przesyłanie filmu")
            .setContentText("Postęp: $progress%")
            .setSmallIcon(R.drawable.ic_upload_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setProgress(100, progress, false)

        notificationManager.notify(1, builder.build())
    }

    fun completeNotification() {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)

    }
    private fun showError(message: String) {
        Timber.w(message)
    }

    private fun navigateToPreviousScreen() {


        (activity as MainFilmsActivity).showFilmsFragment()



    }
    private fun refreshScreen() {
        sharedViewModel.triggerCommentsRefresh("film")
    }
    private fun beforeSending() {
        Timber.i("Proba dodania filmu do bazy danych")
    }

    private fun showLoadingAnimation() {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        updateHandler.removeCallbacks(updateRunnable)
        super.onDestroyView()
        _binding = null
        requireActivity().unregisterReceiver(cancelUploadReceiver)
    }

}