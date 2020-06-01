package com.shinemo.stallup.domain.utils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;

/**
 * @author Chenzhe Mao
 * @date 2020-04-07
 */
public class LocalDateTimeUtils {

	public static LocalDateTime getFirstDayOfWeek(LocalDateTime localDateTime) {
		return localDateTime.with(DayOfWeek.MONDAY);
	}

	public static LocalDateTime getFirstDayOfMonth(LocalDateTime localDateTime) {
		return localDateTime.with(TemporalAdjusters.firstDayOfMonth());
	}

	public static Long getStartOfDay(LocalDateTime localDateTime) {
		return localDateTime.toLocalDate().atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
	}

	public static Long toEpochMilli(LocalDateTime localDateTime) {
		return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
	}
}
