package com.shinemo.smartgrid.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@Slf4j
public class DateUtils {



    public static void main(String[] args) throws NoSuchAlgorithmException {
//        Date monday = getThisWeekMonday(new Date());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String format = df.format(monday);
//        Date thisMonthFirstDay = getThisMonthFirstDay();
//        System.out.println(df.format(thisMonthFirstDay));
//        System.out.println(format);
//        Date dayStartTime = getDayStartTime();
//        Date dayEndTime = getDayEndTime();
//        System.out.println(df.format(dayStartTime));
//        System.out.println(df.format(dayEndTime));
//        HuaweiBuildingRequest request = new HuaweiBuildingRequest();
//        request.setCommunityId("1001");
//        request.setBroadbandType("中国移动");
//        request.setBuildingId("1001001");
//        request.setBuildingName("西溪一号院");
//        Map<String,Object> map =  new HashMap<>();
//        map.put("postBody",request);
//        map.put("method","method");
//        map.put("timeStamp",1588050928993L);
//        //key为双方约定，参数不传递
//        map.put("key","test");
//        ObjectMapper objectMapper = new ObjectMapper();
//        String source = null;
//        try {
//            source = objectMapper.writeValueAsString(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        byte[] bytes = MessageDigest.getInstance("MD5").digest(source.getBytes(Charset.forName("UTF-8")));
//        char[] result = new char[bytes.length * 2];
//        int c = 0;
//        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
//        for (byte b : bytes) {
//            result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
//            result[c++] = HEX_DIGITS[b & 0xf];
//        }
//        String sign = new String(result);
//        map.put("sign",sign);
//        HuaweiBuildingRequest request = new HuaweiBuildingRequest();
//        request.setBuildingName("楼栋名");
//        TreeMap<String,Object> map = new TreeMap<>();
//        map.put("timeStamp",System.currentTimeMillis());
//        map.put("method","addBuiling");
//        map.put("postBody",request);
//        System.out.println(JSON.toJSON(map));
    }

    public static String hex(byte[] data) {
        char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] result = new char[data.length * 2];
        int c = 0;
        for (byte b : data) {
            result[c++] = HEX_DIGITS[(b >> 4) & 0xf];
            result[c++] = HEX_DIGITS[b & 0xf];
        }
        return new String(result);
    }

    public static Date getThisWeekMonday() {

        DateTime dateTime = DateUtil.beginOfWeek(new Date());
        return new Date(dateTime.getTime());

    }

    /**
     * 当月第一天
     * @return
     */
    public static Date getThisMonthFirstDay() {
        DateTime dateTime = DateUtil.beginOfMonth(new Date());
        return new Date(dateTime.getTime());
    }

    /**
     * 获取当日开始时间
     * @return
     */
    public static Date getDayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当日结束时间
     * @return
     */
    public static Date getDayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    /**
     * 把日期转换为字符串
     *
     * @param date
     * @return
     */
    public static String dateToString(Date date, String format) {
        String result = "";
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            result = formater.format(date);
        } catch (Exception e) {
            log.error("[dateToString] dateToString error,date = {},format = {}",date,format);
        }
        return result;
    }

    /**
     * 把符合日期格式的字符串转换为日期类型
     *
     * @param dateStr
     * @return
     */
    public static Date stringtoDate(String dateStr, String format) {
        Date d = null;
        SimpleDateFormat formater = new SimpleDateFormat(format);
        try {
            formater.setLenient(false);
            d = formater.parse(dateStr);
        } catch (Exception e) {
            log.error("[stringtoDate] stringtoDate error,dateStr = {},format = {}",dateStr,format);
            d = null;
        }
        return d;
    }



}
