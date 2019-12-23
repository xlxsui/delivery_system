package com.stu.delivery_system.util;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class OkHttpUtil {

    /**
     * 发送数据到服务器，获取登录数据, get方法基本不用
     *
     * @param url      服务器的url
     * @param callback 处理请求成功或者失败的回调
     */
    public static void sendGetRequest(String url, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 发送数据到服务器，获取登录数据, get方法基本不用
     *
     * @param url      服务器的url
     * @param body     表单主体
     * @param callback 处理请求成功或者失败的回调
     */
    public static void sendPostRequest(String url, RequestBody body, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
}













