package com.flutter.incomming_call.services

import android.os.Bundle
import java.util.HashMap

object Utils {
    const val CHANNEL_ID = "call_notification"
    var data: Map<String, String> = HashMap()

    fun convertBundleToMap(extras: Bundle): Map<String, String> {
        val map: MutableMap<String, String> = HashMap<String, String>()
        val ks: Set<String> = extras.keySet()
        val iterator = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            val value = extras.getString(key)
            if (value != null) {
                map[key] = value
            }
        }
        return map
    }
}