package com.example.tcpclient_android;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import java.net.Socket;
import java.io.*;

public class FileSendThread implements Runnable {

    private static final String TAG = "FileSendThread";
    
    Socket socket;
    File file;
    Handler handler;
    FileInputStream fis;
    DataOutputStream dos;
    ProgressBar progressBar;
    
    public FileSendThread(File file,Handler handler,ProgressBar progressBar){
        try {
            this.socket = new Socket("47.95.39.148",6788);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.file = file;
        this.handler = handler;
        this.progressBar = progressBar;
    }

    @Override
    public void run() {
        try{
//            Socket socket = new Socket("47.95.39.148",6788);
//        sockets.add(socket);

            dos = new DataOutputStream(
                    socket.getOutputStream()
            );

            //文件
            fis = new FileInputStream(file);

            dos.writeUTF(file.getName());
            dos.flush();
            dos.writeLong(file.length());
            dos.flush();

            int len = 0;
            byte[] buffer = new byte[1024*50];

            long recvSum = 0;
            int processSend = 0;

            while ((len = fis.read(buffer)) != -1){
                dos.write(buffer,0,len);
                dos.flush();
                recvSum += len;
                processSend = (int) (((float)recvSum/file.length())*100);
                progressBar.setProgress(processSend);
                Log.d(TAG, "len:" + len + " recvSum:" + recvSum + "---" + processSend + "%");
            }
            Message msg = handler.obtainMessage();
            msg.what = 2;
            handler.sendMessage(msg);
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (fis != null) {
                    fis.close();
                }
                if (dos != null) {
                    dos.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
