package com.corcow.hw.flagproject.manager;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by multimedia on 2016-04-28.
 */
public class NetworkManager {
    private static NetworkManager instance;
    public static NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }

    AsyncHttpClient client;

    private NetworkManager() {
        client = new AsyncHttpClient();
    }

    public interface OnResultListener<T> {
        public void onSuccess(T result);
        public void onFail(int code);
    }

    // Server URL
    private static final String SERVER = "http://52.79.140.129:3000";

    public void testPost(Context context, String paramTestString, final OnResultListener<String> listener ) {
        RequestParams params = new RequestParams();
        params.put("test", paramTestString);

        client.post(context, SERVER + "/test", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int i, cz.msebera.android.httpclient.Header[] headers, String s, Throwable throwable) {

            }

            @Override
            public void onSuccess(int i, cz.msebera.android.httpclient.Header[] headers, String s) {

            }
        });
    }

    public void test365(Context context, String email, String password, String nickname, final OnResultListener<String> listener ) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("nick", nickname);

        client.post(context, "http://52.68.247.34:3000/users", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }
}