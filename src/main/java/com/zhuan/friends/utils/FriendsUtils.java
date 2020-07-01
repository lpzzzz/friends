package com.zhuan.friends.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 工具类 使用类名直接调用静态方法
 */
public class FriendsUtils {

    /**
     * 生成随机字符串
     *
     * @return
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String md5(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /**
     * 将字符串转换为 JSON 字符串
     *
     * @param code 存储状态码
     * @param msg  存储响应消息
     * @param map  存储业务数据
     * @return
     */
    public static String getJSONString(int code, String msg, Map<String, Object> map) {
        JSONObject json = new JSONObject();
        json.put("code", code);
        json.put("msg", msg);

        if (map != null) {
            for (String key : map.keySet()) {
                json.put(key, map.get(key));
            }
        }
        return json.toJSONString();
    }

    /**
     * 重载方法 传递两个参数
     *
     * @param code
     * @param msg
     * @return
     */
    public static String getJSONString(int code, String msg) {
        return getJSONString(code, msg, null);
    }

    /**
     * 重载方法传递一个参数
     *
     * @param code
     * @return
     */
    public static String getJSONString(int code) {
        return getJSONString(code, null, null);
    }

    public static void main(String[] args) {
        Map<String ,Object> map = new HashMap<>();
        map.put("name","张三");
        map.put("age",23);
        System.out.println(getJSONString(0, "ok", map));
    }

}
