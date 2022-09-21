import 'package:flutter/material.dart';

class AcceptCallScreen extends StatelessWidget {
  const AcceptCallScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Accept'),
      ),
      body: const Center(
        child: Text(
          'Accept Call',
          style: TextStyle(fontSize: 40),
        ),
      ),
    );
  }
}
