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

<kbd><img src="https://b.top4top.io/p_2456wzeqs1.png" width="270" height="550"/></kbd>
<kbd><img src="https://c.top4top.io/p_2456hqzol2.png" width="270" height="550"/></kbd>

---
## _Configure Your Project_
This plugin doesn't require complicated configs, just connect your flutter app with firebase app project and do the next simple actions:

### Firebase app
- open android module inside project in android studio to work with it as android project.
- add the Google services config file `google-services.json` by path `your_app/android/app/`.
- add next string at the end of your build.gradle file by path `your_app/android/build.gradle`:
```
dependencies {
    classpath 'com.google.gms:google-services:4.3.10'
    ...
    }
```
- add next string at the end of your build.gradle file by path `your_app/android/app/build.gradle`:

```
apply plugin: 'com.google.gms.google-services'
```

- add set this:
```
defaultConfig {
        ...
        minSdkVersion 20
        ...
        multiDexEnabled true
        }
```

### Dependencies
Add the following dependencies for your app
```
    // for ui
     implementation 'com.google.android.material:material:1.5.0'
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
```xml
        <service
            android:name=".services.MyFirebaseMessagingService"
            android:exported="false" >
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```
- Permissions:
  -- Add the following permissions.
```xml
        <!--allow app to use network in release version-->
        <uses-permission android:name="android.permission.INTERNET" />
        <!--allow app for showing full screen [Call Activity] when user click the notification or when phone screen is turn off-->
        <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
```
- Register Activity:
  -- Add CallActivity [Page]
```xml
        <activity
            android:name=".CallActivity"
            android:theme="@style/LaunchTheme"
            android:showOnLockScreen="true"
            android:windowSoftInputMode="adjustResize" />
```
- Channel:
  -- register custom notification channel to customize it with sound and view.
```xml
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="@string/channel_id" /> <!--name of notification channel>
```

### Res
- copy the following files at path `your_app/android/app/src/main/res/values`:
  -- colors.xml **all colors we need for layout**
  -- dimen.xml **margin and padding**
  -- strings.xml **shared strings**
- copy layout and layout-v21 folders for UI.
- copy drawable and drawable-v21 folders for UI. **buttons and logo**
- create folder `res` and add your notification sound and call it `sound.mp3`

## _The simplest code you can see to implement this feature_ 🫡 🖤
### Code - [Native Side]
- copy all the files at path `your_app/android/app/src/main/kotlen/app-package-name/`
- don't forget to change `package com.flutter.incomming_call` or any `import com.flutter.incomming_call.<file>` in all files to your app package name

##### A simplified explanation of what the basic files of the application contain
-- `MainActivity.kt`
When you run a flutter app, this page is called as the main page in the application, it's contain :
```Kotlin
        // channel that allow us to connect between flutter app and native android app
        override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine)
        // when user click accept or reject from call activity we send user action to main activity to send it to flutter app
        fun getCallStatusFromCallActivity()
        // get FCM Token for send notification
        fun getFCMToken()
        // create custom notification to custmize it 
        fun createCustomChannel()
        // delete notification from status bar when user click on it
        fun deleteParticularNotification()
```

-- `CallActivity.kt`
The strength of the application on this page will also be explained by the following..
```Kotlin
        // after timer is ended the call is rejected
        fun setTimerFor60Sec()
        // work with buttons and caller text and image
        fun getReferenceOfView()
        // add on click to buttons
        fun listenToButtons()
        // delete notification from status bar when user click on it ( accept , reject )
        fun deleteNotification()
        // get data of user like name and photo from notification response 
        fun getUserDataFromNotificationData()
```

**Now** inside `services` folder we found..

-- `ActivityUtils.kt`
When the phone screen is off, these functions do their job
When the screen is off and a communication notification comes, the application will go to the `CallActivity.kt` and automatically turn on the phone screen via `fun Activity.turnScreenOnAndKeyguardOff()`
```Kotlin
        // for turn on screen
        fun Activity.turnScreenOnAndKeyguardOff()
        // for turn off screen
        fun Activity.turnScreenOffAndKeyguardOn()
```

-- `MyFirebaseMessagingService.kt`
Here you can get notifications and act on them
```Kotlin
        // this function listen for a new notification
        override fun handleIntent(intent: Intent)
        // this function listen for a new FCM Token 
        override fun onNewToken(token: String)
        // custom function used for show call notification
        private fun showNotification(intent: Intent)
        // check of app is in Background by check the package name is active [Background] or check app is active [Foreground]
        private fun isAppIsInBackground(context: Context): Boolean
```

-- `NotificationUtils.kt`
This file contains some important functions that show the notification or the call page and other functions that help us to make the notification.
```Kotlin
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
function `handleIntent` in file `MyFirebaseMessagingService.kt` return Intent that contain notification data .. we convert it to map by `convertBundleToMap(extras: Bundle): Map<String, String>` to send notification data as Map to flutter app by PlatformChannel.

---
## _Integrate Your Notification Data With Native Code_
In order for this package to work with your notifications, I want you to make very simple changes.

- All modifications are on the `MyFirebaseMessagingService.kt` and are as follows:
  -- When sending a notification by firebase, the data json must contain `caller_name` and `caller_image` in case the notification is a call, as here..
```json
        {
           "notification": {
             "body": "call from ali",
             "title": "click to answer"
           },
           "priority": "high",
           "data": {
             "click_action": "FLUTTER_NOTIFICATION_CLICK",
             "type": "2",
             "caller_name": "Mohamed Nasr",
             "caller_image": "https://www.placehold..."
           },
           "to": "dpsksQl.."         
        }
```
As you can see, the data json contains `caller_name` as well as `caller_image` and also the `type` and it is very important to specify the type of notification, whether it is a normal notification or a call notification, and at the end `click_action` so that the user can click on the notification in case the application is terminated.

- Looking at the `MyFirebaseMessagingService.kt`, inside the `handleIntent` function there is the following:
```Kotlin
        // get notification data
        val mBundle = intent.extras ?: return
        // convert notification data to map
        Utils.data = Utils.convertBundleToMap(mBundle)
        Log.w("Notification data: ",mBundle.toString())

        // way to get the type of notification
        val type = mBundle.getString("type")
        if(type == "2"){
            // Show call notification
            showCallNotification(intent)
            return
        }
        //otherwise, show normal notification came from firebase
        super.handleIntent(intent)
```

- Looking at the `MyFirebaseMessagingService.kt`, inside the `showCallNotification` function there is the following:
```Kotlin
        if(!isAppIsInBackground(applicationContext)) return

        val mBundle = intent.extras ?: return

        // data from firebase notification data
        val title = mBundle.getString("gcm.notification.title")
        val body = mBundle.getString("gcm.notification.body")
        val callerImage = mBundle.getString("caller_image")
        val callerName = mBundle.getString("caller_name")

        intent.putExtra(CALLER_NAME,callerName)
        intent.putExtra(CALLER_IMAGE,callerImage)

        if (body != null && title != null) {
            showNotificationWithFullScreen(title,body,callerName,callerImage)
        }
```

- Want to change notification channel details (Id, name, description) ?
  You can do this with the `string.xml` and `Utils.kt`.

### Code - [Flutter Side]
- When the user presses Agree or Reject, whether from the call notification or the call page, he will be automatically directed to `MainActivity.kt`, from which the application will relaunch again.

- All you have to do here is call this function, which allows you to know if the user has agreed to the call or not, and also to know the details of the latest notification in full.

```dart
    Map<String, dynamic> result = {};
    if (Platform.isAndroid) {
      try {
        final data = await platform.invokeMethod('getUserAction');
        // result is map contain the answer of user and latest notification data
        result = Map<String, dynamic>.from(data);
        // answer of user
        log(result['user_action']);
        final lastNotificationData =
            Map<String, dynamic>.from(result['last_notification_data']);
        // latest notification data, to see if a notification is a call notification or not
        log(lastNotificationData.toString());
      } on PlatformException catch (error) {
        log(error.message.toString());
      }
    }
```

---
**And here you have reached the end and you can try the notifications as described .. I hope you have benefited and if you want to inquire about something, you can contact me.**

<h3 align="left">Connect with me:</h3>
<p align="left">
<a href="https://linkedin.com/in/mnhaloka" target="blank"><img align="center" src="https://raw.githubusercontent.com/rahuldkjain/github-profile-readme-generator/master/src/images/icons/Social/linked-in-alt.svg" alt="mnhaloka" height="30" width="40" /></a>
<a href="https://fb.com/mnhaloka" target="blank"><img align="center" src="https://raw.githubusercontent.com/rahuldkjain/github-profile-readme-generator/master/src/images/icons/Social/facebook.svg" alt="mnhaloka" height="30" width="40" /></a>
</p>
