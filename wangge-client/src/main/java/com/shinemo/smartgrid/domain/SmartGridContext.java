package com.shinemo.smartgrid.domain;

import com.shinemo.client.util.UrlUtils;
import com.shinemo.common.tools.lang.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chenzhe Mao
 * @date 2020-04-30
 */
public class SmartGridContext {
	public static String KEY_START_TIME = "startTime";
	public static String KEY_UID = "uid";
	public static String KEY_LONG_UID = "longUid";
	public static String KEY_USER_NAME = "username";
	public static String KEY_ORG_ID = "orgId";
	public static String KEY_LONG_ORG_ID = "longOrgId";
	public static String KEY_ORG_NAME = "orgName";
	public static String KEY_NAME = "name";
	public static String KEY_MOBILE = "mobile";
	public static String KEY_GRID_ID = "gridInfo";

	private static final ThreadLocal<Map<String, Object>> local = new ThreadLocal<Map<String, Object>>() {
		@Override
		protected Map<String, Object> initialValue() {
			Map<String, Object> map = new HashMap<>(4);
			map.put(KEY_START_TIME, System.nanoTime());
			return map;
		}
	};

	public static Map<String, Object> get() {
		return local.get();
	}

	public static void remove() {
		local.remove();
	}

	public static void put(String key, Object value) {
		get().put(key, value);
	}

	public static Object get(String key) {
		return get().get(key);
	}

	public static long getStartTime() {
		return (Long) get(KEY_START_TIME);
	}

	public static void setUid(final String uid) {
		put(KEY_UID, uid);
		put(KEY_LONG_UID, NumberUtils.toLong(uid));
	}

	public static String getUid() {
		return (String) get(KEY_UID);
	}

	public static long getLongUid() {
		return (Long) get(KEY_LONG_UID);
	}

	public static void setOrgId(String orgId) {
		put(KEY_ORG_ID, orgId);
		put(KEY_LONG_ORG_ID, NumberUtils.toLong(orgId));
	}

	public static String getOrgId() {
		return (String) get(KEY_ORG_ID);
	}

	public static long getLongOrgId() {
		return (Long) get(KEY_LONG_ORG_ID);
	}

	public static void setOrgName(String orgName) {
		put(KEY_ORG_NAME, orgName);
	}

	public static String getOrgName() {
		return (String) get(KEY_ORG_NAME);
	}

	public static void setUserName(String userName) {
		put(KEY_USER_NAME, userName);
		put(KEY_NAME, userName);
	}

	public static String getUserName() {
		try {
			return UrlUtils.decodeValue((String) get(KEY_USER_NAME));
		} catch (Exception e) {
			return null;
		}
	}

	public static void setName(String userName) {
		put(KEY_NAME, userName);
		put(KEY_USER_NAME, userName);
	}

	public static String getName() {
		return (String) get(KEY_NAME);
	}

	public static void setMobile(String mobile) {
		put(KEY_MOBILE, mobile);
	}

	public static void setGridInfo(String gridInfo) {
		put(KEY_GRID_ID, gridInfo);
	}

	public static String getMobile() {
		return (String) get(KEY_MOBILE);
	}

}
