package com.flutter.incomming_call

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.NonNull
import com.flutter.incomming_call.services.CALL_NOTIFICATION_DATA
import com.flutter.incomming_call.services.NOTIFICATION_ID
import com.flutter.incomming_call.services.Utils
import com.google.firebase.messaging.FirebaseMessaging
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.util.*


class MainActivity: FlutterActivity() {
    private val channel = "call_action"
    private var message : String = ""

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler {
                call, result ->
            if (call.method == "getUserAction") {
                val resultMap: MutableMap<String, Any> = HashMap<String, Any>()
                resultMap["user_action"] = message
                resultMap["last_notification_data"] = Utils.data
                result.success(resultMap)
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getCallStatusFromCallActivity()
        getFCMToken()
        createCustomChannel()
        deleteParticularNotification()
    }

    private  fun getCallStatusFromCallActivity(){
        val mBundle = intent.getBundleExtra(CALL_NOTIFICATION_DATA)
        if(mBundle != null){
            Utils.data = Utils.convertBundleToMap(mBundle)
        }
        message = intent.getStringExtra(CALL_STATUS).toString()
    }

    private fun createCustomChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri: Uri = Uri.parse(
                "android.resource://" +
                        applicationContext.packageName +
                        "/" +
                        R.raw.sound
            )
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(Utils.CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            mChannel.setSound(soundUri, audioAttributes)
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    private fun deleteParticularNotification(){
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, -1)
        if (notificationId > 0) {
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(notificationId)
        }
    }

    private fun getFCMToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if(it.isComplete){
                println("Your FCM Token: "+it.result.toString())
            }
        }
    }
}

