package com.andrzejbrzezinski.rossmannproductlist.filmpackage.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import java.util.*

class CancelUploadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workId = intent.getStringExtra("WORK_ID")
        if (workId != null) {
            WorkManager.getInstance(context).cancelWorkById(UUID.fromString(workId))
        }
    }
}
