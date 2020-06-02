package com.shinemo.smartgrid.helper;

import com.shinemo.common.tools.LoginContext;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author luchao
 */
public class UserLoginHelper extends LoginContext {

    private static final String NAME = "name";
    private static final String MOBILE = "mobile";
    private static final String MGR_ID = "mgrId";
    private static final String REGION_CODES = "regionCodes";
    private static final String REGION_CODE_LIST = "regionCodeList";
    private static final String CITY_CODE_LIST = "cityCodeList";


    public static void set(String name, String mobile, Long mgrId, String regionCodes) {
        setUserName(name);
        put(MOBILE, mobile);
        put(MGR_ID, mgrId);
        put(REGION_CODES, regionCodes);
        if (StringUtils.isBlank(regionCodes)) {
            put(REGION_CODE_LIST, Collections.EMPTY_LIST);
        } else {
            List<Integer> regionCodeList = Arrays.asList(regionCodes.split(OptionConfig.SEPARATOR_COMMA)).stream().map(NumberUtils::toInt).collect(Collectors.toList());
            put(REGION_CODE_LIST, regionCodeList);
        }
    }

    public static String getName() {
        return String.valueOf(get(NAME));
    }

    public static String getMobile() {
        return String.valueOf(get(MOBILE));
    }


    public static Long getMgrId() {
        return (Long) get(MGR_ID);
    }

    public static String getRegionCodes() {
        return String.valueOf(get(REGION_CODES));
    }

    public static List<Integer> getReionCodeList() {
        return (List<Integer>) get(REGION_CODE_LIST);
    }

}
