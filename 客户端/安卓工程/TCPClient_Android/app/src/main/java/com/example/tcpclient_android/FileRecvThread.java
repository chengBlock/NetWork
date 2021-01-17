package com.example.tcpclient_android;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class FileRecvThread implements Runnable {

    private static final String TAG = "FileRecvThread";

    Socket socket;
    Handler handler;
    DataInputStream dis;
    ProgressBar progressBar;

    public FileRecvThread(Handler handler, ProgressBar progressBar) {
        try {
            this.socket = new Socket("47.95.39.148",6788);
            this.dis = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.handler = handler;
        this.progressBar = progressBar;
    }


    @Override
    public void run() {
        try {
            String fileName = dis.readUTF();
            long fileLength = dis.readLong();

            int processRecv = 0;

            FileOutputStream fos = new FileOutputStream(createFile(fileName));

            long recvSum = 0;

            int len = 0;
            byte[] buffer = new byte[1024*50];
            while ((len = dis.read(buffer)) != -1) {
                fos.write(buffer,0,len);
                fos.flush();
                recvSum +=len;
                processRecv = (int) (((float)recvSum/fileLength)*100);
                progressBar.setProgress(processRecv);
                Log.d(TAG, "len:" + len + " recvSum:" + recvSum + "---" + processRecv + "%");
            }

            Message msg = handler.obtainMessage();
            msg.what = 3;
            handler.sendMessage(msg);


        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                dis.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createFile(String fileName) {
        String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "CCC" + File.separator;
        File file = new File(mPath + fileName);
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
