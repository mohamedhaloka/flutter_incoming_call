
package com.flutter.incomming_call.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.flutter.incomming_call.*
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

const val CALLER_NAME= "CALLER_NAME"
const val CALLER_IMAGE= "CALLER_IMAGE"
const val NOTIFICATION_ID= "NOTIFICATION_ID"
const val CALL_NOTIFICATION_DATA= "CALL_NOTIFICATION_DATA"

fun Context.showNotificationWithFullScreen(
    title: String,
    description: String,
    callerName: String?,
    bundle: Bundle,
    callerImage: String?) {
    val notificationId = Random.nextInt(1000)
    val icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

    val notificationLayout = RemoteViews(applicationContext.packageName, R.layout.notification_alert_layout)

    val acceptPendingIntent = btnIntent(this,notificationId,"Accept",0,bundle)
    val rejectPendingIntent = btnIntent(this,notificationId,"Reject",1,bundle)
    notificationLayout.setOnClickPendingIntent(R.id.btnAccept, acceptPendingIntent)
    notificationLayout.setOnClickPendingIntent(R.id.btnDecline, rejectPendingIntent)

    notificationLayout.setTextViewText(R.id.txtTitle, callerName)
    notificationLayout.setImageViewBitmap(R.id.notify_alert_caller_image, callerImage?.let { getBitmapFromURL(it) })

    val builder = NotificationCompat.Builder(this, Utils.CHANNEL_ID)
        .setLargeIcon(icon)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setDefaults(NotificationCompat.DEFAULT_ALL)
        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        .setAutoCancel(true)
        .setOngoing(true)
        .setLights(Color.GRAY, 1000, 300)
        .setDefaults(Notification.DEFAULT_VIBRATE)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTimeoutAfter(60000)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCustomContentView(notificationLayout)
        .setFullScreenIntent(getFullScreenIntent(callerName,callerImage,notificationId,bundle), true)


    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    notificationManager.cancelAll()
    with(notificationManager) {
        val notification = builder.build()

        notify(notificationId, notification)
    }
}

private fun btnIntent(context: Context, notificationId: Int, callStatus: String, requestCode:Int ,bundle: Bundle):PendingIntent{
    val intent = Intent(context,MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        putExtra(NOTIFICATION_ID,notificationId)
        putExtra(CALL_STATUS, callStatus)
        putExtra(CALL_NOTIFICATION_DATA,bundle)

    }
    var intentFlagType = PendingIntent.FLAG_CANCEL_CURRENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        intentFlagType = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
    }

    return PendingIntent.getActivity(context, requestCode, intent, intentFlagType)
}

/// Configure Full Screen View
/** It show when phone screen turn off */
private fun Context.getFullScreenIntent(callerName: String?,
                                        callerImage: String?,
                                        notificationId: Int?,
bundle: Bundle): PendingIntent {
    val destination = CallActivity::class.java

    val intent = Intent(this, destination).apply {
        putExtra(NOTIFICATION_ID,notificationId)
        putExtra(CALLER_NAME,callerName)
        putExtra(CALLER_IMAGE,callerImage)
        putExtra(CALL_NOTIFICATION_DATA,bundle)
    }
    var intentFlagType = PendingIntent.FLAG_ONE_SHOT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        intentFlagType = PendingIntent.FLAG_IMMUTABLE
    }
    return PendingIntent.getActivity(this, 0, intent, intentFlagType)
}

/// Download Image form url and convert it to [Bitmap]
private fun getBitmapFromURL(src: String): Bitmap? {
    return try {
        Log.e("src", src)
        val url = URL(src)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        val myBitmap: Bitmap = BitmapFactory.decodeStream(input)
        Log.e("Bitmap", "returned")
        myBitmap
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("Exception", e.toString())
        null
    }
}

