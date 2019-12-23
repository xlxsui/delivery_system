package com.stu.delivery_system.controller;


import com.stu.delivery_system.domain.User;
import com.stu.delivery_system.domain.WXSessionModel;
import com.stu.delivery_system.service.MissionServiceImpl;
import com.stu.delivery_system.service.UserService;
import com.stu.delivery_system.util.JsonUtils;
import com.stu.delivery_system.vo.Response;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MissionServiceImpl missionService;
    @Autowired
    private UserService userService;

    /**
     * 用户信息
     *
     * @param uuid
     * @return
     */
    @GetMapping(value = "/get_user_info")
    public ResponseEntity<Response> get_user_info(String uuid) {
        System.out.println(uuid);
        User user = (User) userService.loadUserByUuid(uuid);

        return ResponseEntity.ok().body(new Response("success", "miao~", user));
    }


    @GetMapping(value = "/wxLogin")
    public String wxLogin(String code) {
        System.out.println("wxlogin - code: " + code);
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        Map<String, String> param = new HashMap<>();
        param.put("appid", "wxda53c29f7eac23e5");
        param.put("secret", "25ff71a16d0191636e765f6e5a76122a");
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");

//        String wxResult = HttpClientUtil.doGet(url, param);
//
//        System.out.println(wxResult);
//        // 将字符串转换成json格式数据
//        WXSessionModel model = JsonUtils.jsonToPojo(wxResult, WXSessionModel.class);
//        System.out.println(model.getOpenid() + '\n' + model.getSession_key());
//        return model.getOpenid();

        url += "?";
        url += "appid" + "=" + "wx7abf4847ab3b20a4" + "&";
        url += "secret" + "=" + "52c9e3a61c19c2c089ecc01e78893297" + "&";
        url += "js_code" + "=" + code + "&";
        url += "grant_type" + "=" + "authorization_code";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        String bodyString = "";
        try {
            //同步请求
            okhttp3.Response response = client.newCall(request).execute();
            bodyString = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WXSessionModel model = JsonUtils.jsonToPojo(bodyString, WXSessionModel.class);
        String str = "";
        str = model.getOpenid();
        return str;
    }

    @GetMapping(value = "/wxUserSyncDb")
    public ResponseEntity<Response> wxUserInfo(String id, String nickName, String avatar, String gender, String province) {
        User user = new User(id, nickName, 0);
        user.setProvince(province);
        user.setGender(gender);
        user.setAvatar(avatar);

        userService.saveUser(user);

        return ResponseEntity.ok().body(new Response("success", "miao~", user));
    }
}
