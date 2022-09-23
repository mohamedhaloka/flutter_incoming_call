package com.flutter.incomming_call.services

import com.google.firebase.messaging.FirebaseMessagingService

import android.content.Intent

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context

import android.os.Build
import android.util.Log

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun handleIntent(intent: Intent) {
        val mBundle = intent.extras ?: return
//        Utils.data = Utils.convertBundleToMap(mBundle)
        Log.w("Notification data: ",mBundle.toString())

        val type = mBundle.getString("type")
        if(type == "2"){
            // Show call notification
            showCallNotification(intent)
            return
        }
        //otherwise show normal notification come from firebase
        super.handleIntent(intent)
    }

    override fun onNewToken(token: String) {
        Log.w("onNewToken ",token)
        super.onNewToken(token)
    }

    private fun showCallNotification(intent: Intent) {
        if(!isAppIsInBackground(applicationContext)) return

        val mBundle = intent.extras ?: return

        // Extract needed info for passing it to notification
        val title = mBundle.getString("gcm.notification.title")
        val body = mBundle.getString("gcm.notification.body")
        val callerImage = mBundle.getString("caller_image")
        val callerName = mBundle.getString("caller_name")

        intent.putExtra(CALLER_NAME,callerName)
        intent.putExtra(CALLER_IMAGE,callerImage)
        intent.putExtra(CALL_NOTIFICATION_DATA,mBundle)


        if (body != null && title != null) {
            showNotificationWithFullScreen(title,body,callerName,mBundle,callerImage)
        }
    }

    /// Check of app is in Background by check the package name is active [Background] or check app is active [Foreground]
    private fun isAppIsInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            val runningProcesses = am.runningAppProcesses
            for (processInfo in runningProcesses) {
                if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (activeProcess in processInfo.pkgList) {
                        if (activeProcess == context.packageName) {
                            isInBackground = false
                        }
                    }
                }
            }
        } else {
            val taskInfo = am.getRunningTasks(1)
            val componentInfo = taskInfo[0].topActivity
            if (componentInfo!!.packageName == context.packageName) {
                isInBackground = false
            }
        }
        return isInBackground
    }
}