import 'dart:async';
import 'dart:developer';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_incoming_call/accept_call.dart';
import 'package:flutter_incoming_call/reject_call.dart';

import 'firebase_options.dart';

typedef CallEventHandler = Future<dynamic> Function();

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp(options: DefaultFirebaseOptions.currentPlatform);
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Incoming call Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Incoming Call'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);
  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const platform = MethodChannel('call_action');

  String _userAction = 'Unknown user action.';

  Future<void> _getUserAction() async {
    Map<String,dynamic> result = {};
    try {
      final data = await platform.invokeMethod('getUserAction');
      result = Map<String,dynamic>.from(data);
      log(result.toString());
      final lastNotificationData = Map<String,dynamic>.from(result['last_notification_data']);
      log(lastNotificationData.toString());
    } on PlatformException catch (error) {
      log(error.message.toString());
    }

    setState(() => _userAction = result['user_action']);
  }

  @override
  void initState() {
    _getUserAction();
    Future.delayed(Duration.zero, () {
      FirebaseMessaging.onMessage.listen((message) {
        log('onMessage Listen: ${message.data}');
      });

      FirebaseMessaging.onMessageOpenedApp.listen((message) {
        log('onMessageOpenedApp Listen: ${message.data}');
        if (_userAction == 'Accept') {
          Navigator.push(context,
              MaterialPageRoute(builder: (_) => const AcceptCallScreen()));
        } else {
          Navigator.push(context,
              MaterialPageRoute(builder: (_) => const RejectCallScreen()));
        }
      });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'User action status..',
            ),
            Text(
              _userAction,
              style: Theme.of(context).textTheme.headline4,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _getUserAction,
        child: const Icon(Icons.call_to_action),
      ),
    );
  }
}
