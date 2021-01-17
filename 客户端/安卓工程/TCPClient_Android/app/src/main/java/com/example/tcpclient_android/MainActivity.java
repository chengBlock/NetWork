package com.example.tcpclient_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcpclient_android.updateUtil.AppUpdateUtil;
import com.example.tcpclient_android.updateUtil.UpdateManager;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.tcpclient_android.updateUtil.AppUpdateUtil.checkUpdate;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    Button btn_connect;
    Button btn_send;

    Button btn_file_send;
    Button btn_file_recv;

    Button btn_update_apk;

    EditText edit_ip;
    EditText edit_port;
    EditText edit_input;
    EditText edit_file_name;

    TextView tv_message;
    TextView tv_process;

    ProgressBar progressBar;

    ExecutorService threadPool;
    Socket socket;
    PrintStream printStream;

    public Handler handler;
    String fileDir;
    boolean fileFlag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btn_connect = findViewById(R.id.btn_connect);
        btn_send = findViewById(R.id.btn_send);
        btn_file_recv = findViewById(R.id.btn_file_recv);
        btn_file_send = findViewById(R.id.btn_file_send);
        btn_update_apk = findViewById(R.id.btn_update_apk);

        edit_input = findViewById(R.id.edit_input);
        edit_ip = findViewById(R.id.edit_ip);
        edit_port = findViewById(R.id.edit_port);
        edit_file_name = findViewById(R.id.et_file_name);

        tv_message = findViewById(R.id.tv_message);
        tv_process = findViewById(R.id.tv_process);

        btn_send.setOnClickListener(this);
        btn_connect.setOnClickListener(this);
        btn_file_send.setOnClickListener(this);
        btn_file_recv.setOnClickListener(this);
        btn_update_apk.setOnClickListener(this);

        progressBar = findViewById(R.id.pb_progress);

        threadPool = Executors.newCachedThreadPool();
        fileDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "CCC" + File.separator;




        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Object msgRcv =  msg.obj;
                switch (msg.what){
                    case 0:
                        String content0 = (String) msgRcv;
                        btn_connect.setText(content0);
                        break;
                    case 1:
                        String content1 = (String) msgRcv;
                        tv_message.setText(content1);
                        break;
                    case 2:
                        tv_process.setText("文件发送完毕");
                        fileFlag = true;
                        Toast.makeText(MainActivity.this,"文件发送完毕",Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        tv_process.setText("文件接收完毕");
                        fileFlag = true;
                        Toast.makeText(MainActivity.this,"文件接收完毕",Toast.LENGTH_SHORT).show();
                        break;
                    case 404:
                        UpdateManager updateManager = new UpdateManager(MainActivity.this);
                        updateManager.checkUpdateInfo();
                        break;

                    default:
                        Toast.makeText(MainActivity.this,msgRcv+"",Toast.LENGTH_SHORT).show();

                }
            }
        };

        //动态申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.REQUEST_INSTALL_PACKAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},2);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                String currentApkVersion = AppUpdateUtil.getVersion(MainActivity.this);
                String serverApkVersion = checkUpdate().replace(".apk","");
                if (!currentApkVersion.equals(serverApkVersion)) {
                    msg.what = 404;
                    msg.obj = currentApkVersion;
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connect:
                if (socket == null) {
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            connect();
                        }
                    });
                }
                break;
            case R.id.btn_send:
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        send();
                    }
                });
                edit_input.setText("");
                break;
            case R.id.btn_file_recv:
                if (fileFlag) {
                    fileFlag = false;
                    tv_process.setText("文件接收中...");
                    edit_file_name.setText("");
                    progressBar.setProgress(0);
                    threadPool.execute(new Runnable() {
                        @Override
                        public void run() {
                            fileRecv(handler,progressBar);
                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this,"传输中...",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_file_send:
                final String fileName = edit_file_name.getText().toString();
                if (TextUtils.isEmpty(fileName)) {
                    Toast.makeText(MainActivity.this,"请输入文件名",Toast.LENGTH_SHORT).show();
                }else {
                    final File file = new File(fileDir + fileName);
                    if (file.exists()) {
                        if (fileFlag) {
                            fileFlag = false;
                            tv_process.setText("文件发送中...");
                            edit_file_name.setText("");
                            progressBar.setProgress(0);
                            threadPool.execute(new Runnable() {
                                @Override
                                public void run() {
                                    fileSend(file,progressBar);
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this,"传输中...",Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(MainActivity.this,"文件不存在",Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onClick: " + fileDir + fileName);
                    }
                }
                break;
            case R.id.btn_update_apk:
                startActivity(new Intent(MainActivity.this,UpdateActivity.class));
                break;
        }
    }

    private void connect() {
        try {
            socket = new Socket("47.95.39.148",6789);
            new Thread(new ClientThread(socket,handler)).start();
            printStream = new PrintStream(socket.getOutputStream());

            Message message = handler.obtainMessage();
            message.obj = "已连接";
            message.what = 0;
            handler.sendMessage(message);
        } catch (IOException e) {
            Log.d(TAG, "connect failed");
            e.printStackTrace();
        }
    }

    private void fileSend(File file,ProgressBar progressBar) {
        try{
            threadPool.execute(new FileSendThread(file,handler,progressBar));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fileRecv(Handler handler,ProgressBar progressBar){
        threadPool.execute(new FileRecvThread(handler,progressBar));
    }

    private void send(){
        String content = edit_input.getText().toString();
        printStream.println(content);
    }

    private void disconnect(){
        try {
            printStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //动态申请权限后调用
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            //存储权限
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //活动授权，执行操作
                    Toast.makeText(this,"You granted the permission",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this,"You denied the permission",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
