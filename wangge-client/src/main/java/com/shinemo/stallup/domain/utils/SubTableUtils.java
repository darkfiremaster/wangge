package com.shinemo.stallup.domain.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 分表工具类
 *
 * @author Chenzhe Mao
 * @date 2020-05-06
 */
public class SubTableUtils {
	private static final DateTimeFormatter yyyyMM = DateTimeFormatter.ofPattern("yyyyMM");
	private static final String INDEX_FORMAT = "_month_%s";

	public static String getTableIndexByMonth() {
		return getTableIndexByMonth(LocalDate.now());
	}

	public static String getTableIndexByMonth(LocalDate localDate) {
		return String.format(INDEX_FORMAT, localDate.format(yyyyMM));
	}
}
