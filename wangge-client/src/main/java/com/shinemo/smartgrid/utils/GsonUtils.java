package com.shinemo.smartgrid.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shinemo.stallup.domain.model.StallUpBizType;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.*;

public class GsonUtils {

	private static final Logger log = LoggerFactory.getLogger(GsonUtils.class);

	private final static Gson gson;

	static {
		GsonBuilder gsonBuilder = new GsonBuilder();
		com.shinemo.client.gson.DateTypeAdapter dateTypeAdapter = new com.shinemo.client.gson.DateTypeAdapter("yyyy-MM-dd HH:mm:ss.SSS");
		gsonBuilder.registerTypeAdapter(Date.class, dateTypeAdapter);
		gsonBuilder.registerTypeAdapter(Timestamp.class, dateTypeAdapter);
		gsonBuilder.registerTypeAdapter(java.sql.Date.class, dateTypeAdapter);
		gsonBuilder.disableHtmlEscaping();
		gson = gsonBuilder.create();
	}


	public static <T> String fromObj2Gson(T obj, Class<T> clazz) {
		if (null == obj)
			return null;
		return gson.toJson(obj, clazz);
	}

	public static <T> T fromGson2Obj(String json, Class<T> clazz) {
		if (StringUtils.isBlank(json))
			return null;

		if (clazz.getSimpleName().equals(String.class.getSimpleName())) {
			return (T) String.valueOf(json);
		}
		return gson.fromJson(json, clazz);
	}

	public static <T> String fromObj2Gson(T obj, Type t) {
		if (null == obj)
			return null;
		return gson.toJson(obj, t);
	}

	public static <T> List<T> fromJsonToList(String json, Class<T[]> type) {
		try {
			T[] list = gson.fromJson(json, type);
			if (list == null) return null;
			return Arrays.asList(list);
		} catch (Exception e) {
			log.error("Jsons.fromJsonToList ex, json=" + json + ", type=" + type, e);
		}
		return null;
	}

	public static <T> boolean isJsonList(String json, Class<T[]> type) {
		try {
			gson.fromJson(json, type);
			return true;
		} catch (Exception e) {
			log.error("not json list, json=" + json);
		}
		return false;
	}


	public static <T> T fromGson2Obj(String json, Type type) {
		if (StringUtils.isBlank(json))
			return null;
		return gson.<T>fromJson(json, type);
	}


	public static <T> T fromGson2Obj(String json, com.google.gson.reflect.TypeToken<T> typeToken) {
		if (StringUtils.isBlank(json))
			return null;
		if (typeToken == null)
			throw new NullArgumentException("typeToken");
		return gson.fromJson(json, typeToken.getType());
	}

	public static Map<String, Object> getJsonMap(String jsonStr) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(jsonStr)) {
			Type typeOfT = new TypeToken<Map<String, Object>>() {
			}.getType();
			paramMap = gson.fromJson(jsonStr, typeOfT);
		}
		return paramMap;
	}

	public static Map<String, String> getStringMap(String jsonStr) {
		Map<String, String> paramMap = new HashMap<String, String>();
		if (StringUtils.isNotBlank(jsonStr)) {
			Type typeOfT = new TypeToken<Map<String, String>>() {
			}.getType();
			paramMap = gson.fromJson(jsonStr, typeOfT);
		}
		return paramMap;
	}


	public static String toJson(Object object) {
		if (object instanceof String) {
			return (String) object;
		}
		return gson.toJson(object);
	}


	public static void main(String[] args) {

//		List<Map<String, String>> list = new ArrayList<>();
//		Map<String, String> map = new HashMap<>();
//		map.put("a", "1");
//		list.add(map);
//		list.add(map);
//		System.out.println(toJson(list));
		List list = new ArrayList();
		boolean jsonList = isJsonList(GsonUtils.toJson(list), StallUpBizType[].class);

	}
}