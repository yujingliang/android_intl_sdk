package com.intl.api;

import com.intl.IntlGame;
import com.intl.entity.Session;
import com.intl.httphelper.HttpThreadHelper;
import com.intl.usercenter.IntlGameCenter;
import com.intl.utils.IntlGameLoading;
import com.intl.utils.IntlGameUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Author: yujingliang
 * @Date: 2019/11/28
 */
public class AuthorizeGUAPI {
    private IGuestLoginCallback iGuestLoginCallback;
    private HttpThreadHelper httpThreadHelper;
    public AuthorizeGUAPI(final Session session)
    {
        JSONObject jsonObject = new JSONObject();
        final String url = IntlGame.urlHost +"/api/auth/authorize/?client_id=" + IntlGame.GPclientid;
        try{
            jsonObject.put("request_type", session.getRequestType());
            jsonObject.put("unique_id", session.getAuthCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        IntlGameLoading.getInstance().show(IntlGameCenter.getInstance().activity.get());
        httpThreadHelper = new HttpThreadHelper(
                jsonObject, url, new HttpThreadHelper.HttpCallback() {
            @Override
            public void onPostExecute(HttpThreadHelper.HttpResult result) {
                IntlGameLoading.getInstance().hide();
                if (result.ex == null && result.httpCode == 200) {
                    if (result.responseData.optInt("ErrorCode") == 0 && result.responseData.optString("ErrorMessage").equals("Successed")) {
                        JSONObject datajson = result.responseData.optJSONObject("Data");
                        iGuestLoginCallback.AfterGuestLogin(session.getChannel(), datajson,result.responseData.optString("ErrorMessage"));
                    }else {
                        IntlGameUtil.logd("IntlEX","GuestLoginAPI error:"+result.responseData.toString());
                        iGuestLoginCallback.AfterGuestLogin(session.getChannel(),null,result.responseData.optString("ErrorMessage"));
                    }
                } else {
                    IntlGameUtil.logd("IntlEX","GuestLoginAPI time out:"+ (result.ex != null ? result.ex.getMessage() : null));
                    iGuestLoginCallback.AfterGuestLogin(session.getChannel(), null,result.ex.getMessage());
                }
            }
        }
        );
    }

    public void Excute() {
        httpThreadHelper.start();
    }
    public void setListener(IGuestLoginCallback listener)
    {
        iGuestLoginCallback = listener;
    }
    public interface IGuestLoginCallback{
        void AfterGuestLogin(String channel, JSONObject jsonObject, String errorMsg);
    }
}
