package com.andrzejbrzezinski.rossmannproductlist.filmpackage.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.net.Uri
import android.os.Looper
import android.provider.MediaStore.Audio.Media
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.SeekBar
import androidx.recyclerview.widget.RecyclerView
import com.andrzejbrzezinski.rossmannproductlist.FilmWithComments
import com.andrzejbrzezinski.rossmannproductlist.FilmsDetails
import com.andrzejbrzezinski.rossmannproductlist.databinding.ItemVideoBinding
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.logging.Handler

class VideoPagerAdapter(var videoList: List<FilmWithComments> = emptyList()) :
    RecyclerView.Adapter<VideoPagerAdapter.ViewHolder>() {
    class ViewHolder(private val binding: ItemVideoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(videourl: FilmsDetails?) {
            binding.videoview.apply {
                (setVideoURI(Uri.parse(videourl?.url)))
                requestFocus()
                start()
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
        binding.videotitle.text = videourl?.name

        binding.startStopButton.setOnClickListener {
            if (binding.videoview.isPlaying) {
                binding.videoview.pause()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_pause)
            } else {
                binding.videoview.start()
                binding.startStopButton.setImageResource(android.R.drawable.ic_media_play)
            }
        }

        binding.seekBar.max = binding.videoview.duration
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    binding.videoview.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.videoview.setOnTouchListener { v, event ->
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

        val updateHandler = android.os.Handler(Looper.getMainLooper())
        fun formatTime(millis: Int): String {
            val minutes = TimeUnit.MILLISECONDS.toMinutes(millis.toLong())
            val seconds =
                TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) - TimeUnit.MINUTES.toSeconds(
                    minutes
                )
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }

        val updateRunnable = object : Runnable {
            override fun run() {
                binding.currentTime.text = formatTime(binding.videoview.currentPosition)
                binding.seekBar.progress = binding.videoview.currentPosition
                updateHandler.postDelayed(this, 1000)
            }
        }

        binding.videoview.setOnPreparedListener { mp ->
            val duration = binding.videoview.duration

            binding.totalTime.text = formatTime(duration)
            binding.seekBar.max = binding.videoview.duration
            updateHandler.postDelayed(updateRunnable, 1000)


        }
        binding.videoview.setOnCompletionListener {
            updateHandler.removeCallbacks(updateRunnable)
        }


    }

        fun focus() {
            binding.videoview.apply {
                requestFocus()
                start()
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VideoPagerAdapter.ViewHolder {
        return ViewHolder(
            ItemVideoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VideoPagerAdapter.ViewHolder, position: Int) {
        holder.bind(videoList[position].film)
    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    fun submitList(newVideoList: List<FilmWithComments>) {
        videoList = newVideoList
        notifyDataSetChanged()
    }

}