package com.corcow.hw.flagproject.manager;

import android.content.Context;

import com.corcow.hw.flagproject.model.json.FileInfo;
import com.corcow.hw.flagproject.model.json.Login;
import com.corcow.hw.flagproject.model.json.LoginResult;
import com.corcow.hw.flagproject.model.json.UserPage;
import com.corcow.hw.flagproject.model.json.UserPageResult;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.File;

import cz.msebera.android.httpclient.Header;

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
    Gson gson;

    private NetworkManager() {
/*
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            MySSLSocketFactory socketFactory = new MySSLSocketFactory(trustStore);
            socketFactory.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            client = new AsyncHttpClient();
            client.setSSLSocketFactory(socketFactory);
            client.setCookieStore(new PersistentCookieStore(MyApplication.getContext()));
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        }
*/
        client = new AsyncHttpClient();
        gson = new Gson();
//        client.setCookieStore(new PersistentCookieStore(MyApplication.getContext()));

    }

    public interface OnResultListener<T> {
        public void onSuccess(T result);
        public void onFail(int code);
    }
    public interface OnFileResultListener<T> {
        public void onSuccess(T result);
        public void onProgress(long bytesWritten, long totalSize);
        public void onFail(int code);
    }

    // Server URL
    private static final String SERVER = "http://52.79.140.129:3000";
    private static final String REQ_TYPE="type";
    private static final String TYPE_APP="app";

    // Sign in 로그인
    public void signIn(Context context, String userID, String userPW, final OnResultListener<LoginResult> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);
        params.put("userPW", userPW);

        client.post(context, SERVER + "/signin", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                Login resultResponse = gson.fromJson(responseString, Login.class);
                listener.onSuccess(resultResponse.result);
            }
        });
    }

    public void signOut(Context context, String userID, final OnResultListener<String> listener ) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);

        client.post(context, SERVER + "/signout", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }

    // Sign up 회원가입
    public void signUp(Context context, String userID, String userPW, String email, final OnResultListener<LoginResult> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);
        params.put("userPW", userPW);
        params.put("email", email);

        client.post(context, SERVER + "/signup", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                Login resultResponse = gson.fromJson(responseString, Login.class);
                listener.onSuccess(resultResponse.result);
            }
        });
    }

    // ID 중복확인
    public void idCheck(Context context, String userID, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);

        client.post(context, SERVER + "/idcheck", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }

    public void fileUpload(Context context, String fileName, String absolutePath, String flagName, String filePrivate, String userID, final OnFileResultListener<String> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("fileName", fileName);
        params.put("flagName", flagName);
        params.put("filePrivate", filePrivate);
        params.put("userID", userID);
        File file = new File(absolutePath);
        try {
            params.put("flagFile", file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        client.post(context, SERVER + "/fileUpload", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                listener.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }


    // 파일 정보 받아오기
    public void fileInfo(Context context, String userID, String flagName, final OnResultListener<FileInfo> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);
        params.put("flagName", flagName);

        client.get(context, SERVER + "/fileInfo", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                FileInfo result = gson.fromJson(responseString, FileInfo.class);
                listener.onSuccess(result);
            }
        });
    }


    // Handler 변경
    public void fileDownload(Context context, String userID, String flagName, final OnFileResultListener<File> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("userID", userID);
        params.put("flagName", flagName);

        client.get(context, SERVER + "/" + userID + "/" + flagName, params, new FileAsyncHttpResponseHandler(context) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File responseFile) {
                listener.onFail(statusCode);
            }

            @Override
            public void onProgress(long bytesWritten, long totalSize) {
                listener.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File responseFile) {
                listener.onSuccess(file);
            }
        });
    }

    // UserPage
    public void userFileList(Context context, String userID, final OnResultListener<UserPageResult> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);

        client.post(context, SERVER + "/" + userID, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                UserPage result = gson.fromJson(responseString, UserPage.class);
                listener.onSuccess(result.result);
            }
        });
    }

    public void fileDelete(Context context, String _id, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("_id", _id);

        client.post(context, SERVER + "/fileDelete", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }

    public void filePrivate(Context context, String _id, final OnResultListener<String> listener) {
        RequestParams params = new RequestParams();
        params.put(REQ_TYPE, TYPE_APP);
        params.put("_id", _id);

        client.post(context, SERVER + "/filePrivate", params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                listener.onFail(statusCode);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                listener.onSuccess(responseString);
            }
        });
    }

}