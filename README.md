# Flutter Incoming Call
## _Introduction_
This package provides you with the ability to receive calls in a wonderful way, like many applications such as WhatsApp or Messenger and others.
Through this article, I will explain to you how you can use this package and attach it to your project.
I don't need you to have native experience, but there is a simple modification that I will show you so that the application can work with you.
Before anything else, I would like you to watch this video to see the power of the package.

[![Watch the video](https://img.youtube.com/vi/-vF0c52i_KM/maxresdefault.jpg)](https://youtu.be/-vF0c52i_KM)

## _Supported platforms_
- Android
- iOS [Soon]

## _Features_
- displaying the Incoming call screen when push notification was delivered on the device.
- notifying the app about user action performed on the Incoming call screen (accept, reject).
- getting the data about last call.
- some customizations according to your app needs (ringtone, icon)

<img src="https://www.raed.net/img?id=119745" width="270" height="550">
<img src="https://www.raed.net/img?id=119746" width="270" height="550">

## _Configure Your Project_
This plugin doesn't require complicated configs, just connect your flutter app with firebase app project and do the next simple actions:

### Firebase app
- open android module inside project in android studio to work with it as android project.
- add the Google services config file `google-services.json` by path `your_app/android/app/`.
- add next string at the end of your build.gradle file by path `your_app/android/app/build.gradle`:

```
apply plugin: 'com.google.gms.google-services'
```

### Dependencies
Add the following dependencies for your app
```
    // firebase messaging (notification)
    implementation 'com.google.firebase:firebase-messaging:23.0.8' 
    // for pending intent in android 31 and above
    implementation 'androidx.work:work-runtime-ktx:2.7.1'
    // for multiDex
    implementation 'com.android.support:multidex:1.0.3' 
    // circle image for avatar (UI)
    implementation 'de.hdodenhof:circleimageview:3.1.0' 
    // download image url and put it into circle avatar (no direct way :()
    implementation 'com.github.bumptech.glide:glide:4.13.2' 
```

### Manifest
- add the following lines in mainfest file `your_app/android/app/src/main/AndroidMainfest.xml`:
  -- Register FirebaseMessaging file to enable recieve notification
```
        <service
            android:name=".services.MyFirebaseMessagingService"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```
- Register Activity:
  -- Add CallActivity [Page]
```
        <activity
            android:name=".CallActivity"
            android:theme="@style/LaunchTheme"
            android:showOnLockScreen="true"
            android:windowSoftInputMode="adjustResize" />
```
- Channel:
  -- register custom notification channel to customize it with sound and view.
```
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="call_notification" /> // name of notification channel
```

### Res
- copy the following files at path `your_app/android/app/src/main/res/values`:
  -- colors.xml **all colors we need for layout**
  -- dimen.xml **margin and padding**
  -- strings.xml **shared strings**
- copy layout and layout-v21 folders for UI.
- copy drawable and drawable-v21 folders for UI. **buttons and logo**
- create folder `res` and add your notification sound and call it `sound.mp3`

### Code
- copy all the files at path `your_app/android/app/src/main/kotlen/app-package-name/`
- don't forget to change `package com.flutter.incomming_call` or any `import com.flutter.incomming_call.<file>` in all files to your app package name

##### A simplified explanation of what the basic files of the application contain
-- `MainActivity.kt`
When you run a flutter app, this page is called as the main page in the application, it's contain :
```kotlen
        // channel that allow us to connect between flutter app and native android app
        override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine)
        // when user click accept or reject from call activity we send user action to main activity to send it to flutter app
        getCallStatusFromCallActivity()
        // get FCM Token for send notification
        getFCMToken()
        // create custom notification to custmize it 
        createCustomChannel()
        // delete notification from status bar when user click on it
        deleteParticularNotification()
```

-- `CallActivity.kt`
The strength of the application on this page will also be explained by the following..
```kotlen
        // after timer is ended the call is rejected
        setTimerFor60Sec()
        // work with buttons and caller text and image
        getReferenceOfView()
        // add on click to buttons
        listenToButtons()
        // delete notification from status bar when user click on it ( accept , reject )
        deleteNotification()
        // get data of user like name and photo from notification response 
        getUserDataFromNotificationData()
```

**Now** inside `services` folder we found..

-- `ActivityUtils.kt`
When the phone screen is off, these functions do their job
When the screen is off and a communication notification comes, the application will go to the contact page and automatically turn on the Shah via `fun Activity.turnScreenOnAndKeyguardOff()`
```kotlen
        // for turn on screen
        fun Activity.turnScreenOnAndKeyguardOff()
        // for turn off screen
        fun Activity.turnScreenOffAndKeyguardOn()
```

-- `MyFirebaseMessagingService.kt`
Here you can get notifications and act on them
```kotlen
        // this function listen for a new notification
        override fun handleIntent(intent: Intent)
        // this function listen for a new FCM Token 
        override fun onNewToken(token: String)
        // custom function used for show custom call notification
        private fun showNotification(intent: Intent)
        // check of app is in Background by check the package name is active [Background] or check app is active [Foreground]
        private fun isAppIsInBackground(context: Context): Boolean
```

-- `NotificationUtils.kt`
This file contains some important functions that show the notification or the call page and other functions that help us to make the notification.
```kotlen
        // This function creates and shows the notification
        fun Context.showNotificationWithFullScreen(title: String,
                                                    description: String,
                                                    callerName: String?,
                                                    callerImage: String?)
        // register buttons in notification layout (accept , reject) and add action for their
        private fun btnIntent(context: Context, 
                              notificationId: Int, 
                              callStatus: String, 
                              requestCode:Int ) : PendingIntent
        // When the phone screen is off, this function is responsible for showing the call page
        Context.getFullScreenIntent(callerName: String?,
                                    callerImage: String?,
                                    notificationId: Int?): PendingIntent
        // download user image from url and convert it to Bitmap format
        private fun getBitmapFromURL(src: String): Bitmap?
```

-- `Utils.kt`
object contains shared functions and properties.
function `handleIntent` in file `MyFirebaseMessagingService.kt` return Intent that contain notification data .. we convert it to map by `convertBundleToMap(extras: Bundle): Map<String, String>` to send notification data as Map to flutter app by PlatformChannel