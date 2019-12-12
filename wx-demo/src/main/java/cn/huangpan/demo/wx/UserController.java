/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: UserController
 * Author:   pand
 * Date:     2019/11/28 19:33
 * Description: controller
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.huangpan.demo.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 〈一句话功能简述〉<br> 
 * 〈controller〉
 *
 * @author pand
 * @create 2019/11/28
 * @since 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    public final static Map<String, JSONObject> tokenOpenIds = new HashMap<>();

    @GetMapping("/auth")
    public BizResponse auth(String code) throws IOException {

        // 通过code 获得 openid 和 unionid 和 session_key
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session?appid=wx74eb8c9316b66f91&secret=fdba93de4b77d6b615b4b7178d28d79b&js_code=%s&grant_type=authorization_code", code);

        OkHttpClient client = new OkHttpClient();
        Request build = new Request.Builder().url(url).get().build();
        Response response = client.newCall(build).execute();

        String str = response.body().string();
        JSONObject json = JSON.parseObject(str);
        String openid = json.getString("openid");
        String unionid = json.getString("unionid");
        String token = UUID.randomUUID().toString();
//        String sessionKey = json.getString("session_key");

        tokenOpenIds.put(token,json);

        return BizResponse.success(token);
    }
}