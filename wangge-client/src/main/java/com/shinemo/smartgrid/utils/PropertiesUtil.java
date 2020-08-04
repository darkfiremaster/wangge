package com.shinemo.smartgrid.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class PropertiesUtil {

    /**
     * 日志对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties env = new Properties();

    private static String propertiesPath = "wsf.vcc.properties";

    static {
        InputStream is = null;
        try {
            is = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesPath);
            env.load(is);
        } catch (FileNotFoundException e) {
            LOGGER.error("File  Not Found ！");
        } catch (IOException e) {
            LOGGER.error("Occur IOException");
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    LOGGER.error("Occur IOException");
                }

            }
        }
    }

    /**
     * 取属性值
     *
     * @param key key
     * @return String
     */
    public static String getString(String key) {
        return env.getProperty(key);
    }

    /**
     * 设置属性值
     *
     * @param key   key
     * @param value value
     */
    public static void setProperty(String key, String value) {
        FileOutputStream outStream = null;
        try {
            File file = new File(
                    PropertiesUtil.class.getClassLoader().getResource(".").getPath() + File.separator + propertiesPath);
            outStream = new FileOutputStream(file);
            env.setProperty(key, value);
            // 写入properties文件
            env.store(outStream, null);
        } catch (FileNotFoundException e) {
            LOGGER.error("File  Not Found ！");
        } catch (IOException e) {
            LOGGER.error("Occur IOException:", e.getMessage());
        } finally {
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    LOGGER.error("Occur IOException");
                }

            }
        }
    }

    /**
     * 生成id
     *
     * @return String
     */
    public static String generateKey() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuffer buffer = new StringBuffer();
        int length = 6;
        char[] allChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            buffer.append(allChar[random.nextInt(allChar.length)]);
        }
        int randomNum = Integer.parseInt(buffer.toString());
        return sdf.format(date).toString() + String.valueOf(randomNum);
    }

    /**
     * 生成id
     *
     * @param format format
     * @param length length
     * @return String
     */
    public static String generateKeyByLen(String format, int length) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        StringBuffer buffer = new StringBuffer();
        char[] allChar = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            buffer.append(allChar[random.nextInt(allChar.length)]);
        }
        int randomNum = Integer.parseInt(buffer.toString());
        return sdf.format(date).toString() + String.valueOf(randomNum);
    }

}

