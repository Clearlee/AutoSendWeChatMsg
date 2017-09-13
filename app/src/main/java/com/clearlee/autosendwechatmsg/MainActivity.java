package com.clearlee.autosendwechatmsg;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.clearlee.autosendwechatmsg.PerformClickUtils.CONTENT;
import static com.clearlee.autosendwechatmsg.PerformClickUtils.NAME;

/**
 * Created by Clearlee
 * 2016/9/13.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "AutoSendMsgService";
    public static final String LauncherUI = "com.tencent.mm.ui.LauncherUI";
    public static final String MM = "com.tencent.mm";

    private TextView start;
    private EditText sendName,sendContent;
    private AccessibilityManager accessibilityManager;

    private String name,content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        start = (TextView) findViewById(R.id.testWechat);
        sendName = (EditText) findViewById(R.id.sendName);
        sendContent = (EditText) findViewById(R.id.sendContent);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndStartService();
            }
        });
    }

    private void goWecaht(){
        setValue(name,content);
        AutoSendMsgService.hasSend = false;
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(MM, LauncherUI);
        startActivity(intent);
    }

    private void openService(){
        try {
            //打开系统设置中辅助功能
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(MainActivity.this, "找到微信自动发送消息，然后开启服务即可", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndStartService() {
        accessibilityManager = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        accessibilityManager.addAccessibilityStateChangeListener(new AccessibilityManager.AccessibilityStateChangeListener() {
            @Override
            public void onAccessibilityStateChanged(boolean b) {
                Log.d(TAG,"onAccessibilityStateChanged b = "+b);
                if(b){
                    goWecaht();
                }else{
                    openService();
                }
            }
        });

        name = sendName.getText().toString();
        content = sendContent.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(MainActivity.this,"联系人不能为空",Toast.LENGTH_SHORT);
        }
        if(TextUtils.isEmpty(content)){
            Toast.makeText(MainActivity.this,"内容不能为空",Toast.LENGTH_SHORT);
        }

        if(!accessibilityManager.isEnabled()){
            openService();
        }else{
            goWecaht();
        }
    }

    public void setValue(String name,String content){
        NAME = name;
        CONTENT = content;
        AutoSendMsgService.hasSend = false;
    }

}
