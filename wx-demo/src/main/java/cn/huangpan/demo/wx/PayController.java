/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: PayController
 * Author:   pand
 * Date:     2019/12/4 22:27
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.huangpan.demo.wx;

import jodd.Jodd;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import okhttp3.*;
import okhttp3.RequestBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

/**
 * 〈一句话功能简述〉<br> 
 * 〈〉
 *
 * @author pand
 * @create 2019/12/4
 * @since 1.0.0
 */
@RequestMapping("/pay")
@RestController
public class PayController {

    // 统一下单 -> 返回给前端签名
    @GetMapping
    public BizResponse pay(int amout,@RequestHeader("token") String token) throws IOException{

        if(token == null || token.length() == 0){return BizResponse.fail("1002","未登录");}

        // 统一下单
        String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
        OkHttpClient client = new OkHttpClient();

        Map<String,Object> params = new TreeMap<>();
        params.put("appid","wx74eb8c9316b66f91");
        params.put("mch_id", "1508262781");
        params.put("nonce_str", UUID.randomUUID().toString().replace("-", ""));
        params.put("body", "支付一分钱-测试");
        params.put("out_trade_no", "740890944");
        params.put("total_fee", amout);
        params.put("spbill_create_ip", "112.95.22.224");
        params.put("notify_url", "https://wx.panqingshan.cn/notice/1802/pay/notice");
        params.put("trade_type", "JSAPI");
        params.put("openid", UserController.tokenOpenIds.get(token).getString("openid"));

        // 签名
        // appid=wx74eb8c9316b66f91&mch_id=1508262781...&
        Set<String> strings = params.keySet();
        StringBuffer signParams = new StringBuffer();
        for (String string : strings) {
            signParams.append(string).append("=").append(params.get(string)).append("&");
        }
        signParams.append("key=").append("lakJYzxp5znq5Pz1JYDBGInzrUNVAeYj");
        String sign = DigestUtils.md5Hex(signParams.toString());
        params.put("sign", sign);

        // Map -> XML
        String xml = XmlHelper.toXml(params);
        RequestBody body = RequestBody.create(xml, MediaType.parse("application/xml"));

        Request request = new Request.Builder().url(url).post(body).build();
        Response response = client.newCall(request).execute();

        String str = response.body().string();
        System.out.println("str = " + str);

        Map<String, String> prePayMap = XmlHelper.of(str).toMap();
        // 返回签名

        Map<String, String> requestPayParams = new TreeMap<>();
        requestPayParams.put("appId", "wx74eb8c9316b66f91");
        requestPayParams.put("timeStamp",System.currentTimeMillis()+"");
        requestPayParams.put("nonceStr",UUID.randomUUID().toString().replace("-", ""));
        requestPayParams.put("package","prepay_id="+prePayMap.get("prepay_id"));
        requestPayParams.put("signType","MD5");

        Set<String> requestPayParamsSet = requestPayParams.keySet();
        StringBuffer requestPaySignPara = new StringBuffer();
        for (String requestPayKey : requestPayParamsSet) {
            requestPaySignPara.append(requestPayKey).append("=").append(requestPayParams.get(requestPayKey)).append("&");
        }
        requestPaySignPara.append("key=").append("lakJYzxp5znq5Pz1JYDBGInzrUNVAeYj");
        String requestPaySign = DigestUtils.md5Hex(requestPaySignPara.toString());

        requestPayParams.put("paySign",requestPaySign);

        return BizResponse.success(requestPayParams);
    }

    // https://wx.panqingshan.cn/notice/1802/pay/notice
    // -> http://192.168.8.41:8080/pay/notice
    @PostMapping("/notice")
    public String notice(@org.springframework.web.bind.annotation.RequestBody  String body){
        System.out.println("body = " + body);
        return "<xml>\n" +
                "\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";
    }
}