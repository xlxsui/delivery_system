package com.stu.delivery_system.controller;

import com.stu.delivery_system.domain.WXSessionModel;
import com.stu.delivery_system.service.IMoocJSONResult;
import com.stu.delivery_system.util.JsonUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/wxLogin")
    public IMoocJSONResult wxLogin(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> param = new HashMap<>();
        param.put("appid", "wxda53c29f7eac23e5");
        param.put("secret", "25ff71a16d0191636e765f6e5a76122a");
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");
        String wxResult = this.doGet(url, param);
        System.out.println(wxResult);
        //wxResult  已经拿到openid  和 session_key  下面转为json格式
        WXSessionModel model = JsonUtils.jsonToPojo(wxResult, WXSessionModel.class);
        return IMoocJSONResult.ok(model);
    }


    public static String doGet(String url, Map<String, String> param) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();

        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (param != null) {
                for (String key : param.keySet()) {
                    builder.addParameter(key, param.get(key));
                }
            }
            URI uri = builder.build();

            // 创建http GET请求
            HttpGet httpGet = new HttpGet(uri);

            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }
}