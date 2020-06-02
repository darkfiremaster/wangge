package com.shinemo.smartgrid.utils;

import com.shinemo.conf.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * @author Chenzhe Mao
 * @date 2020-05-20
 */
public class HttpUtil {

	private static final Logger log = LoggerFactory.getLogger("http");

	public static String get(String url, boolean useHttpProxy) {
		return get(url, useHttpProxy, 7000);
	}

	public static String get(String url, boolean useHttpProxy, Integer readTimeOut) {

		HttpURLConnection conn = null;
		BufferedReader rd = null;
		StringBuilder sb = new StringBuilder();
		String line = null;
		String response = null;
		try {
			if (useHttpProxy) {
				String proxyIp = Config.get("http.proxy.ip");
				String proxyPort = Config.get("http.proxy.port");
				Integer port = Integer.parseInt(proxyPort);
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyIp, port));
				conn = (HttpURLConnection) new URL(url).openConnection(proxy);
				log.info("[HttpUtil]  useHttpProxy:" + useHttpProxy + ",proxyIpAndPort:" + proxyIp + ":" + port);
			} else {
				conn = (HttpURLConnection) new URL(url).openConnection();
			}
			long start = System.currentTimeMillis();
			conn.setRequestMethod("GET");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(readTimeOut);
			conn.setConnectTimeout(14000);
			conn.setUseCaches(false);
			conn.connect();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			int code = conn.getResponseCode();
			long end = System.currentTimeMillis();
			long costTime = end - start;
			if (code == 200) {
				while ((line = rd.readLine()) != null) {
					sb.append(line);
				}
				response = sb.toString();
			}
			log.info("[get] url:{}, code:{}, costTime:{}, response:{}", url, code, costTime, response);
		} catch (IOException e) {
			log.error("[get] request:{}", url, e);
		} finally {
			try {
				if (rd != null) {
					rd.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return response;
	}

	public static void main(String[] args) {
		String url = "http://www.gx.10086.cn/css_manager/appQueryData.action?paramData=lCl_jyXx--t5GLdEYtSRuje1ItrGxytyMXl1lh62FU4VOi5cS_fpJxuU5qmDdTCVmtwHwR_hRpTar8bp_5wV_jpuz8XojcZON70ddZOfGmCw1F2uPYCZQg6dzzwTE-Io&timestamp=1589781901524&sign=2a143fd209e0aaf4ceb0a97f7fb31570";
		for (int i = 0; i < 30; i++) {
			String response = HttpUtil.get(url, true);
			System.out.println(response);
		}
	}

}