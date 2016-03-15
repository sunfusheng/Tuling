package com.robot.tuling.control;

/**
 * Created by sunfusheng on 2015/1/14.
 */
public class HttpControl {

    public static final String TULING_URL = "http://www.tuling123.com/openapi/api";
    public static  final String TULING_KEY = "577173e62a2ff6627b62e94d663b449c";

    /*
     * 获得图灵API接口URL
     */
    public static String getTulingUrl(String input) {
        return TULING_URL + "?key=" + TULING_KEY + "&info=" + input;
    }

}
