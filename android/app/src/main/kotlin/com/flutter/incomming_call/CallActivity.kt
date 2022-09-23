package com.flutter.incomming_call

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import io.flutter.embedding.android.FlutterActivity

import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.view.KeyEvent
import android.widget.TextView
import com.bumptech.glide.Glide
import com.flutter.incomming_call.services.*

class CallActivity: FlutterActivity() {
    private val acceptStatue = "Accept"
    private val rejectStatue = "Reject"

    private lateinit var rejectBtn : LinearLayout
    private lateinit var acceptBtn : LinearLayout
    private lateinit var callerName : TextView
    private lateinit var remainingCountDown : TextView
    private lateinit var callerImage : de.hdodenhof.circleimageview.CircleImageView

    private var timer: CountDownTimer? = null

    private lateinit var mediaPlayer : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.incoming_call)
        println("onCreate Lock Screen")

        turnScreenOnAndKeyguardOff()
        setTimerFor60Sec()
        mediaPlayer = MediaPlayer.create(this, R.raw.sound)
        getReferenceOfView()
        listenToButtons()
        deleteNotification()
        getUserDataFromNotificationData()
        mediaPlayer.start()
        mediaPlayer.isLooping = true
    }

    override fun onDestroy() {
        super.onDestroy()
        println("onDestroy Lock Screen")

        mediaPlayer.stop()
        timer?.cancel()
        turnScreenOffAndKeyguardOn()
    }

    private  fun getUserDataFromNotificationData(){
        val mBundle = intent.getBundleExtra(CALL_NOTIFICATION_DATA)
        if(mBundle != null){
            Utils.data = Utils.convertBundleToMap(mBundle)
        }
        val callerNameTxt = intent.getStringExtra(CALLER_NAME)
        val callerImageTxt = intent.getStringExtra(CALLER_IMAGE)
        callerName.text = callerNameTxt
        Glide.with(this).load(callerImageTxt).into(callerImage)
    }

    private fun getReferenceOfView(){
        rejectBtn = findViewById(R.id.reject_btn)
        acceptBtn = findViewById(R.id.accept_btn)
        callerName = findViewById(R.id.tvNameCaller)
        callerImage = findViewById(R.id.ivAvatar)
        remainingCountDown  = findViewById(R.id.remainingTime)
    }

    private fun listenToButtons() {
        rejectBtn.setOnClickListener {
            closeActivityAndApp(rejectStatue)
        }
        acceptBtn.setOnClickListener {
            closeActivityAndApp(acceptStatue)
        }
    }

    private  fun closeActivityAndApp(message: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask()
        } else {
            finishAffinity()
        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(CALL_STATUS, message)
        }
        startActivity(intent)
    }

    private fun deleteNotification(){
            val notificationManager = context
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            closeActivityAndApp(rejectStatue)
            false
        } else super.onKeyDown(keyCode, event)
    }

    private fun setTimerFor60Sec(){
        timer =  object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                println("seconds remaining: " + millisUntilFinished / 1000)
                remainingCountDown.text = ("00:" + millisUntilFinished / 1000)
            }

            override fun onFinish() {
                closeActivityAndApp(rejectStatue)
                cancel()
            }
        }
        timer?.start()
    }

}

const val CALL_STATUS : String = "CALL_STATUS"