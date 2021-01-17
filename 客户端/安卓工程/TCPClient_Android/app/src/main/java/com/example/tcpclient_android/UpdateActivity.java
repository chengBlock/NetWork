package com.example.tcpclient_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcpclient_android.updateUtil.AppUpdateUtil;
import com.example.tcpclient_android.updateUtil.UpdateManager;


import static com.example.tcpclient_android.updateUtil.AppUpdateUtil.checkUpdate;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {

    private UpdateManager mUpdateManager;

    private static final String TAG = "UpdateActivity";

    Button btn_update;
    TextView tv_current_version;
    TextView tv_server_version;
    Handler handler;

    String currentVersion;
    String serverVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                serverVersion = ((String) msg.obj);
                tv_server_version.setText("可更新版本号:" + serverVersion);
            }
        };

        btn_update = findViewById(R.id.btn_update);
        tv_current_version = findViewById(R.id.tv_current_version);
        tv_server_version = findViewById(R.id.tv_flag_version);

        currentVersion = AppUpdateUtil.getVersion(this);

        tv_current_version.setText("当前版本:" + currentVersion);

        btn_update.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.obj = checkUpdate().replace(".apk","");
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                //这里来检测版本是否需要更新
                if (serverVersion.equals(currentVersion)) {
                    Toast.makeText(UpdateActivity.this,"当前版本已是最新",Toast.LENGTH_SHORT).show();
                }else {
                    mUpdateManager = new UpdateManager(this);
                    mUpdateManager.checkUpdateInfo();
                }
            break;
        }
    }
}
