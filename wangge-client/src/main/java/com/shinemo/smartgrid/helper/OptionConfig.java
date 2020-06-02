package com.shinemo.smartgrid.helper;

/**
 * @author: luchao
 * @email: luc@shinemo.com
 * @date: 2019/7/17
 */
public class OptionConfig {

    public final static int SERVER_ERROR = 500;
    public final static String SEPARATOR_COMMA = ",";

    public final static int CITY_SIZE = 12;
    public final static int AREA_SIZE = 20;
    public final static int ALL_REGION_SIZE = CITY_SIZE * AREA_SIZE;

    public final static int CITY_LEVEL = 3;
    public final static int AREA_LEVEL = 4;


    // 佣金类型
    public final static int COMMISSION_TYPE_MONEY = 1;       //固定金额，设多少给多少
    public final static int COMMISSION_TYPE_RATE = 2;      //订单金额的比例，办理成功订单的百分之几
    public final static int COMMISSION_TYPE_MONEY_MAPPING = 3;      //订单金额的映射，不同的办理成功订单对应不同的金额
    public final static int COMMISSION_TYPE_ID_MAPPING = 4;      //预缴ID的映射，不同的预缴ID对应不同的金额

    public final static int ENABLE_MATERIAL_STATUS = 2 + 8;

    public final static int REGION_CODE_LENGTH = 6;
    public final static String REGION_CODE_PREFIX = "33";


}
