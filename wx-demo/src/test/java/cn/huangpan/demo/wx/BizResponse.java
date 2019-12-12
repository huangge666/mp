/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: BizResponse
 * Author:   pand
 * Date:     2019/11/28 19:29
 * Description: 响应类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package cn.huangpan.demo.wx;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈一句话功能简述〉<br> 
 * 〈响应类〉
 *
 * @author pand
 * @create 2019/11/28
 * @since 1.0.0
 */
@Data
public class BizResponse {

    private String code;
    private String msg;
    private Object data;

    // 错误码-描述
    private static final Map<String, String> CODE_DESCS = new HashMap<>();
    static{
        CODE_DESCS.put("0", "成功");
        CODE_DESCS.put("1002", "登录过期或未登录");
    }

    // 设置错误码 - 自动描述
    public void setCode(String code) {
        this.code = code;
        this.msg = CODE_DESCS.get(code);
    }

    /**
     * 快捷设置方式
     * @return
     */
    public static BizResponse of(String code, Object data){
        BizResponse response = new BizResponse();
        response.setCode(code);
        response.setData(data);
        return response;
    }

    public static BizResponse success(Object data){
        return of("0",data);
    }

    public static BizResponse success(){
        return of("0",null);
    }

    public static BizResponse fail(String code,Object data){

        return of(code, data);
    }
}