package com.example.tcpclient_android.updateUtil;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import androidx.core.content.FileProvider;

import com.example.tcpclient_android.R;

public class UpdateManager {

    private Context mContext;

    private static final String TAG = "UpdateManager";

    //提示语
    private String updateMsg = "有最新的软件包哦，亲快下载吧~";

    private Dialog noticeDialog;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static final String savePath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "CCC" + File.separator;;

    private static final String saveFileName = savePath + "UpdateDemoRelease.apk";

    //返回的安装包url
    private String apkUrl = "http://47.95.39.148/3.apk";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    Socket socket;
    InputStream is;
    DataInputStream dis;

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }
    public UpdateManager(){

    }

    //外部接口让主Activity调用
    public void checkUpdateInfo(){
        showNoticeDialog();
    }


    private void showNoticeDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("下载", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showDownloadDialog();
            }
        });
        builder.setNegativeButton("以后再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

    private void showDownloadDialog(){
        AlertDialog.Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar)v.findViewById(R.id.progress);

        builder.setView(v);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });
        downloadDialog = builder.create();
        downloadDialog.show();

        downloadApk();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
//                URL url = new URL(apkUrl);
//
//                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
//                conn.connect();
//                int length = conn.getContentLength();
//                InputStream is = conn.getInputStream();

                //下载apk的socket
                Socket socket = new Socket("47.95.39.148",6787);
                InputStream is = socket.getInputStream();
                DataInputStream dis = new DataInputStream(is);

                String recvApkName = dis.readUTF();
                String currentVersion = AppUpdateUtil.getVersion(mContext);
                Log.d(TAG, "run: " + recvApkName);
                long length = dis.readLong();
                if (currentVersion.equals(recvApkName)) {

                }
                File file = new File(savePath);
                if(!file.exists()){
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];
                int numread = 0;

                while (!interceptFlag && (numread = dis.read(buf)) != -1) {
                    count += numread;
                    progress =(int)(((float)count / length) * 100);
                    //更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    fos.write(buf,0,numread);
                }
                if(numread <= 0){
                    //下载完成通知安装
                    mHandler.sendEmptyMessage(DOWN_OVER);
                }

//                do{
//                int numread = is.read(buf);
//                count += numread;
//                progress =(int)(((float)count / length) * 100);
//                //更新进度
//                mHandler.sendEmptyMessage(DOWN_UPDATE);
//                if(numread <= 0){
//                    //下载完成通知安装
//                    mHandler.sendEmptyMessage(DOWN_OVER);
//                    break;
//                }
//                fos.write(buf,0,numread);
//            }while(!interceptFlag);//点击取消就停止下载.
                
                fos.close();
                is.close();
                Log.d(TAG, "run: end");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk(){
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }
    /**
     * 安装apk
     */
    private void installApk(){
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Log.d(TAG, "installApk: " + apkfile.getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri apkUri = FileProvider.getUriForFile(mContext,
                "com.example.tcpclient_android.fileprovider",
                apkfile);//file即为所要共享的文件的file
        Log.d(TAG, "installApk: "+apkUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
        Log.d(TAG, "installApk: end");
    }
}