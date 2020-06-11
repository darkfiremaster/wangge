package com.shinemo.smartgrid.utils;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 11:18
 * @Desc
 */
public class RedisKeyUtil {

    private static final String USER_GRID_INFO_PREFIX = "smartGrid-info-%s";

    private static final String USER_LOGIN_FLAG_PREFIX = "smartGrid-user-login-flag-%s";


    /**
     * 获取用户网格信息key
     * @param mobile
     * @return
     */
    public static String getUserGridInfoPrefixKey(String mobile) {
        return String.format(USER_GRID_INFO_PREFIX, mobile);
    }


    public static String getUserLoginFlagPrefixKey(String mobile) {
        return String.format(USER_LOGIN_FLAG_PREFIX, mobile);
    }

}
