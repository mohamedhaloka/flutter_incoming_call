import 'package:flutter/material.dart';

class RejectCallScreen extends StatelessWidget {
  const RejectCallScreen({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Reject'),
      ),
      body: const Center(
        child: Text(
          'Reject Call',
          style: TextStyle(fontSize: 40),
        ),
      ),
    );
  }
}
