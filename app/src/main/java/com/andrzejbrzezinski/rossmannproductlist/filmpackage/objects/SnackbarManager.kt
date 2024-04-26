package com.andrzejbrzezinski.rossmannproductlist.filmpackage.objects

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.databinding.ActivityMainFilmsBinding
import com.andrzejbrzezinski.rossmannproductlist.databinding.ProgressNotificationAppBinding
import com.google.android.material.snackbar.Snackbar

object SnackbarManager {
    private var snackbar: Snackbar? = null

    fun showUploadProgress(context: Context, progress: Int) {
        val activity = context as Activity
        val snackbarView = LayoutInflater.from(activity).inflate(R.layout.progress_notification_app, activity.findViewById(android.R.id.content), false)
        snackbar = Snackbar.make(activity.findViewById(android.R.id.content), "", Snackbar.LENGTH_INDEFINITE)

        val textView = snackbarView.findViewById<TextView>(R.id.snackbar_text)
        val progressBar = snackbarView.findViewById<ProgressBar>(R.id.snackbar_progress)

        textView.text = "Twój film jest w trakcie przesyłania..."
        progressBar.progress = progress

        snackbar?.view?.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.transparent))
        snackbar?.view?.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)?.visibility = View.INVISIBLE
        val snackbarLayout = snackbar?.view as? Snackbar.SnackbarLayout
        snackbarLayout?.addView(snackbarView, 0)
        snackbar?.show()
    }

    fun updateUploadProgress(progress: Int) {
        val progressBar = snackbar?.view?.findViewById<ProgressBar>(R.id.snackbar_progress)
        progressBar?.progress = progress
        if (progress >= 100) {
            snackbar?.dismiss()
        }
    }

    fun dismissSnackbar() {
        snackbar?.dismiss()
    }
}
