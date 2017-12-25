package com.clearlee.autosendwechatmsg;

/**
 * Created by Clearlee on 2017/12/22 0023.
 * 微信版本6.6.0
 */

public class WeChatTextWrapper {

    public static final String WECAHT_PACKAGENAME = "com.tencent.mm";


    public static class WechatClass{
        //微信首页
        public static final String WECHAT_CLASS_LAUNCHUI = "com.tencent.mm.ui.LauncherUI";
        //微信联系人页面
        public static final String WECHAT_CLASS_CONTACTINFOUI = "com.tencent.mm.plugin.profile.ui.ContactInfoUI";
        //微信聊天页面
        public static final String WECHAT_CLASS_CHATUI = "com.tencent.mm.ui.chatting.ChattingUI";
    }


    public static class WechatId{
        /**
         * 通讯录界面
         */
        public static final String WECHATID_CONTACTUI_LISTVIEW_ID = "com.tencent.mm:id/ih";
        public static final String WECHATID_CONTACTUI_ITEM_ID = "com.tencent.mm:id/iy";
        public static final String WECHATID_CONTACTUI_NAME_ID = "com.tencent.mm:id/j1";

        /**
         * 聊天界面
         */
        public static final String WECHATID_CHATUI_EDITTEXT_ID = "com.tencent.mm:id/a_z";
        public static final String WECHATID_CHATUI_USERNAME_ID = "com.tencent.mm:id/ha";
        public static final String WECHATID_CHATUI_BACK_ID = "com.tencent.mm:id/h9";
        public static final String WECHATID_CHATUI_SWITCH_ID = "com.tencent.mm:id/a_x";
    }

}
