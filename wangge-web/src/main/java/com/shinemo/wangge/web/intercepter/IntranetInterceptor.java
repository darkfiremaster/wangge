package com.shinemo.wangge.web.intercepter;

import com.google.common.net.InetAddresses;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;

/**
 * 只允许回环 ip 或 内网 ip 访问
 *
 * @author Harold Luo
 * @program common-client
 * @date 2018-06-28
 **/
@Slf4j
public class IntranetInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("enter IntranetInterceptor preHandle:{}", request.getRequestURL());
        String ip = getRealIp(request);
        log.info("ip = " + ip);
        if (!isIntranetOrLoopback(ip)) {
            response.setStatus(404);
            return false;
        }
        return true;
    }

    private static final String[] HEADERS_ABOUT_CLIENT_IP = {"X-Forwarded-For", "Proxy-Client-IP",
            "WL-Proxy-Client-IP", "HTTP_X_FORWARDED_FOR", "HTTP_X_FORWARDED", "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP", "HTTP_FORWARDED_FOR", "HTTP_FORWARDED", "HTTP_VIA", "REMOTE_ADDR"};

    /**
     * IPv4 字节数
     */
    private static final int IPv4_LENGTH = 4;
    /**
     * IPv4 单位长度
     */
    private static final int IPv4_UNIT_LENGTH = 1;
    /**
     * IPv4 分割字符串
     */
    private static final char IPv4_SEPARATOR = '.';
    /**
     * IPv6 字节数
     */
    private static final int IPv6_LENGTH = 16;
    /**
     * IPv6 单位长度
     */
    private static final int IPv6_UNIT_LENGTH = 2;
    /**
     * IPv6 分割字符串
     */
    private static final char IPv6_SEPARATOR = ':';
    /**
     * 无符号 0
     */
    private static final byte UBYTE_0 = 0x00;
    /**
     * 无符号 1
     */
    private static final byte UBYTE_1 = 0x01;
    /**
     * 无符号 10
     */
    private static final byte UBYTE_10 = 0x0A;
    /**
     * 无符号 16
     */
    private static final byte UBYTE_16 = 0x10;
    /**
     * 无符号 127
     */
    private static final byte UBYTE_127 = 0x7F;
    /**
     * 无符号 168
     */
    private static final byte UBYTE_168 = (byte) 0xA8;
    /**
     * 无符号 172
     */
    private static final byte UBYTE_172 = (byte) 0xAC;
    /**
     * 无符号 192
     */
    private static final byte UBYTE_192 = (byte) 0xC0;
    /**
     * 无符号 240
     */
    private static final byte UBYTE_240 = (byte) 0xF0;
    /**
     * 无符号 254
     */
    private static final byte UBYTE_254 = (byte) 0xFE;

    public static String getClientIpAddr(HttpServletRequest request) {
        for (String header : HEADERS_ABOUT_CLIENT_IP) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    /**
     * 获取真实 IP
     *
     * @param request
     * @return java.lang.String
     * @author Zhihao Luo
     * @date 2019-05-16
     **/
    public static String getRealIp(HttpServletRequest request) {
        return getRealIp(getClientIpAddr(request), ",");
    }

    /**
     * 获取真实 IP
     *
     * @param ip
     * @param split
     * @return java.lang.String
     * @author Zhihao Luo
     * @date 2019-05-16
     **/
    public static String getRealIp(String ip, String split) {
        int index = -1;
        int begin = 0;
        String realIp = null;
        do {
            index = StringUtils.indexOf(ip, split, begin);
            realIp = StringUtils.trimToNull(index == -1 ? ip.substring(begin) : ip.substring(begin, index));
            begin = index + 1;
        } while ((realIp == null || !isIP(realIp)) && index >= 0);
        return realIp;
    }

    /**
     * 判断是否是 ip
     *
     * @param ip
     * @return boolean
     * @author Zhihao Luo
     * @date 2018-08-15
     **/
    public static final boolean isIP(String ip) {
        return toNumeric(ip) != null;
    }

    /**
     * 判断是否是 ip
     *
     * @param ip
     * @return boolean
     * @author Harold Luo
     * @date 2018-08-15
     **/
    public static final boolean isIP(byte[] ip) {
        return ip != null && (ip.length == IPv4_LENGTH || ip.length == IPv6_LENGTH);
    }

    /**
     * ip 转化成二进制，支持 IPv4 和 IPv6，不是合法 ip 时会返回 null
     *
     * @param ip
     * @return byte[]
     * @author Zhihao Luo
     * @date 2018-08-15
     **/
    public static final byte[] toNumeric(String ip) {
        if (ip == null) {
            return null;
        }
        InetAddress inetAddress = InetAddresses.forString(ip);
        if (inetAddress == null) {
            return null;
        }
        return inetAddress.getAddress();
    }

    /**
     * 判断是否是内网 IP 或回环 IP
     *
     * @param ip
     * @return boolean
     * @author Zhihao Luo
     * @date 2018-08-15
     **/
    public static final boolean isIntranetOrLoopback(String ip) {
        return isIntranetOrLoopback(toNumeric(ip));
    }

    /**
     * 判断是否是内网 IP 或回环 IP
     *
     * @param ip
     * @return boolean
     * @author Harold Luo
     * @date 2018-08-15
     **/
    public static final boolean isIntranetOrLoopback(byte[] ip) {
        if (!isIP(ip)) {
            return false;
        }
        return isIntranet(ip) || isLoopback(ip);
    }

    /**
     * 判断是否是内网 IP
     *
     * @param ip
     * @return boolean
     * @author Harold Luo
     * @date 2018-08-15
     **/
    public static final boolean isIntranet(byte[] ip) {
        if (ip == null) {
            return false;
        }
        if (ip.length == IPv4_LENGTH) {
            // IPv4
            // 10/8，172.16/12，192.168/16
            return ip[0] == UBYTE_10 || (ip[0] == UBYTE_172 && (ip[1] & UBYTE_240) == UBYTE_16) || (ip[0] == UBYTE_192 && ip[1] == UBYTE_168);
        }
        if (ip.length == IPv6_LENGTH) {
            // IPv6
            // FEC0::/10
            return ip[0] == UBYTE_254 && (ip[1] & UBYTE_192) == UBYTE_192;
        }
        return false;
    }

    /**
     * 判断是否是回环 IP
     *
     * @param ip
     * @return boolean
     * @author Harold Luo
     * @date 2018-08-15
     **/
    public static final boolean isLoopback(byte[] ip) {
        if (ip == null) {
            return false;
        }
        if (ip.length == IPv4_LENGTH) {
            // IPv4
            // 127/8
            return ip[0] == UBYTE_127;
        }
        if (ip.length == IPv6_LENGTH) {
            // IPv6
            // ::1
            for (int i = 0, size = IPv6_LENGTH - 1; i < size; i++) {
                if (ip[i] != UBYTE_0) {
                    return false;
                }
            }
            return ip[IPv6_LENGTH - 1] == UBYTE_1;
        }
        return false;
    }
}
