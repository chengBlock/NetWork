package com.example.tcpclient_android;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


public class ClientThread implements Runnable {

    private static final String TAG = "ClientThread";

    private Socket socket;
    private BufferedReader reader;
    private Handler handler;

    public ClientThread(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String content = null;
        while (true) {
            try {
                if (((content = reader.readLine()) != null)) {
                    Message message = handler.obtainMessage();
                    message.obj = content;
                    message.what = 1;
                    handler.sendMessage(message);
                    Log.d(TAG, "接收到的消息： " + content);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
