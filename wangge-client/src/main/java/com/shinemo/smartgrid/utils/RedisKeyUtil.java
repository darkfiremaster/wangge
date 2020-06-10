package com.shinemo.smartgrid.utils;

/**
 * @Author shangkaihui
 * @Date 2020/6/9 11:18
 * @Desc
 */
public class RedisKeyUtil {

    private static final String USER_GRID_INFO_PREFIX = "smartGrid-info-";

    /**
     * 获取用户网格信息key
     * @param mobile
     * @return
     */
    public static String getUserGridInfoPrefixKey(String mobile) {
        return USER_GRID_INFO_PREFIX + mobile;
    }

}
