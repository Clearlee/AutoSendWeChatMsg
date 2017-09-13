package com.clearlee.autosendwechatmsg;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clearlee
 * 2016/9/13.
 */
public class AutoSendMsgService extends AccessibilityService {

    /**
     * 注意：不同的微信版本id不一定相同，我使用的版本是是6.5.13
     */
    public static String contactUI_listview_id = "com.tencent.mm:id/i2";
    public static String contactUI_item_id = "com.tencent.mm:id/ih";
    public static String contactUI_name_id = "com.tencent.mm:id/ik";
    public static String chatUI_EditText_id = "com.tencent.mm:id/a6g";

    private static final String TAG = "AutoSendMsgService";
    private List<String> allNameList = new ArrayList<>();
    private int mRepeatCount;

    public static boolean hasSend;

    /**
     * 必须重写的方法，响应各种事件。
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                if(hasSend){
                    return;
                }
                String currentActivity = event.getClassName().toString();
                Log.d(TAG,"currentActivity = "+currentActivity);
                /**
                 * 第一次打开微信的时候有问题，不会执行任何操作
                 * log显示的是getRootInActiveWindow()没有返回任何东西，暂时无法解决
                 * 如果微信已经打开，再次操作就没有问题
                 */
                if(currentActivity.equals("com.tencent.mm.ui.LauncherUI")) {
                    PerformClickUtils.findTextAndClick(this,"通讯录");
                    AccessibilityNodeInfo itemInfo = TraversalAndFindContacts();
                    if (itemInfo != null) {
                        PerformClickUtils.performClick(itemInfo);
                    }else {
                        hasSend=true;
                    }
                }else if(currentActivity.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")){
                    PerformClickUtils.findTextAndClick(this,"发消息");
                }else if(currentActivity.equals("com.tencent.mm.ui.chatting.En_5b8fbb1e")){
                    if(fill()){
                        send();
                    }
                }
                break;
        }
    }

    /**
     * 向下滚动遍历寻找联系人
     * 如果滚动到底没有找到，向上再滚动一遍（防止当前通讯录已经位于中间位置）
     * @return
     */
    private AccessibilityNodeInfo TraversalAndFindContacts() {
        allNameList.clear();
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        List<AccessibilityNodeInfo> listview = rootNode.findAccessibilityNodeInfosByViewId(contactUI_listview_id);
        Log.d(TAG, "listview " + listview.isEmpty());
        boolean toEnd = false;
        //第一次执行后退操作需要清空名字列表
        boolean firstExcScrollBackward = true;
        if(!listview.isEmpty()){
            while (true) {
                List<AccessibilityNodeInfo> nameList = rootNode.findAccessibilityNodeInfosByViewId(contactUI_name_id);
                List<AccessibilityNodeInfo> itemList = rootNode.findAccessibilityNodeInfosByViewId(contactUI_item_id);
                Log.d(TAG, "nameList " + nameList.isEmpty());
                if (nameList != null && !nameList.isEmpty()) {
                    for (int i = 0; i < nameList.size(); i++) {
                        if(i==0) {
                            //必须在一个循环内
                            mRepeatCount = 0;
                        }
                        AccessibilityNodeInfo itemInfo = itemList.get(i);
                        AccessibilityNodeInfo nodeInfo = nameList.get(i);
                        String nickname = nodeInfo.getText().toString();
                        Log.d(TAG, "nickname " + nickname);
                        Log.d(TAG, "name " + PerformClickUtils.NAME);
                        if (nickname.equals(PerformClickUtils.NAME)) {
                            return itemInfo;
                        }
                            if (!allNameList.contains(nickname)) {
                                allNameList.add(nickname);
                            }else if(allNameList.contains(nickname)){
                                Log.d(TAG,"contains(nickname) = "+nickname);
                                Log.d(TAG,"mRepeatCount = "+mRepeatCount);
                                /*
                                * 表示已经到底了,通过判断当前页是否已经包含了三个联系人
                                * 如果有三个，表示已经到底部了，否则会一直循环
                                * */
                                if(mRepeatCount==3){
                                    //表示已经滑动到顶部了
                                    if(toEnd){
                                        Log.d(TAG,"没有找到联系人");
                                        //此次发消息操作已经完成
                                        hasSend = true;
                                        return null;
                                    }
                                    toEnd = true;
                                }
                                mRepeatCount++;
                            }
                    }
                }

                if(!toEnd) {
                    //向下滚动
                   Log.d(TAG, "向下滚动");
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                }
                else {
                    Log.d(TAG, "向上滚动");
                    if(firstExcScrollBackward) {
                        allNameList.clear();
                        firstExcScrollBackward = false;
                    }
                    //到底了还没找到向上滚动一遍
                    listview.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
                }

                //必须等待一秒钟，因为需要等待滚动操作完成
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void send() {
        Log.d(TAG, "send");
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("发送");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo n : list) {
                    if (n.getClassName().equals("android.widget.Button") && n.isEnabled()) {
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        hasSend = true;
                    }
                }
            } else {
                List<AccessibilityNodeInfo> liste = nodeInfo
                        .findAccessibilityNodeInfosByText("Send");
                if (liste != null && liste.size() > 0) {
                    for (AccessibilityNodeInfo n : liste) {
                        if (n.getClassName().equals("android.widget.Button") && n.isEnabled()) {
                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            hasSend = true;
                        }
                    }
                }
            }
            pressBackButton();
        }
    }

    private void pressBackButton() {
        Log.d(TAG,"pressBackButton");
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean fill() {
        Log.d(TAG, "fill content = "+ PerformClickUtils.CONTENT);
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findEditText(rootNode, PerformClickUtils.CONTENT);
        }
        return false;
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        List<AccessibilityNodeInfo> editInfo = rootNode.findAccessibilityNodeInfosByViewId(chatUI_EditText_id);
        if(editInfo!=null&&!editInfo.isEmpty()){
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, content);
            editInfo.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            return true;
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }


}
