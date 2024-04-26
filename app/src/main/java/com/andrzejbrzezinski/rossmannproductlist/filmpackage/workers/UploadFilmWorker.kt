package com.andrzejbrzezinski.rossmannproductlist.filmpackage.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.andrzejbrzezinski.rossmannproductlist.R
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.interfaces.ILoadData
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.ChildWorkerFactory
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.modules.LoadDataProvider
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.objects.SnackbarManager
import com.andrzejbrzezinski.rossmannproductlist.filmpackage.receivers.CancelUploadReceiver
import com.andrzejbrzezinski.rossmannproductlist.internetfunctions.InternetConnectionCondition
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@AssistedFactory
interface UploadFilmWorkerFactory : ChildWorkerFactory {
    override fun create(appContext: Context, params: WorkerParameters): UploadFilmWorker
}
@HiltWorker
class UploadFilmWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val loadDataProvider: LoadDataProvider
) : CoroutineWorker(context,workerParameters) {
    var isUploadCancelled: Boolean = false
    private val loadData: ILoadData
        get() = loadDataProvider.getLoadData(connection)
    var connection:Boolean = false
    fun checkInternetConnection(isConnected: Boolean)
    {
        connection = isConnected
    }

    override suspend fun doWork(): Result {
        var foregroundInfo =createForegroundInfo(0)
        setForegroundAsync(foregroundInfo)
        checkInternetConnection(InternetConnectionCondition(context).isNetworkAvailable())
        val filmUrl = inputData.getString("filmUrl") ?: return Result.failure()
        val filmTitle = inputData.getString("filmTitle") ?: return Result.failure()
        isUploadCancelled = isStopped
        return try {
                    val userName = loadData.getUserName()
                    userName?.let {
                        val newFilm = loadData.addFilmToStorage(filmUrl, it, filmTitle,{
                            foregroundInfo = createForegroundInfo((it * 100).toInt())
                        },{isStopped})
                        if(!isStopped)
                        loadData.addNewFilmToTest(newFilm, filmTitle, it)
                    }


            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }

    }
    private fun createForegroundInfo(progress: Int): ForegroundInfo {
        val channelId = "upload_channel"
        val title = "Przesyłanie filmu"
        val cancel = "Anuluj"

        val cancelIntent = Intent(context, CancelUploadReceiver::class.java).apply {
            action = "ACTION_CANCEL_UPLOAD"
            putExtra("WORK_ID", id.toString())
        }
        val cancelPendingIntent = PendingIntent.getBroadcast(context, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setTicker(title)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.drawable.ic_upload_foreground)
            .setOngoing(true)
            .setProgress(100, progress, false)
            .addAction(android.R.drawable.ic_delete, cancel, cancelPendingIntent)
            .build()

        if(progress==100 || isStopped)
        {val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
            SnackbarManager.dismissSnackbar()}
        else{
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
            SnackbarManager.updateUploadProgress(progress)}
        return ForegroundInfo(1, notification)
    }





}