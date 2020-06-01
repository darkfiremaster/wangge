package com.shinemo.stallup.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Chenzhe Mao
 * @date 2020-05-26
 */
@AllArgsConstructor
@Getter
public enum  StallUpExceptionEnum {
	CANCELED(0, "参与人取消计划"),
	AUTO_END(4,"超时自动签退"),
	ABNORMAL_END(5,"超出打卡范围")
	;
	@Getter
	private Integer status;
	@Getter
	private String exceptionMsg;

	private static volatile Map<Integer, StallUpExceptionEnum> map;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 根据状态获取枚举类型
	 */
	public static StallUpExceptionEnum getByStatus(Integer status) {
		if (status == null) {
			return null;
		}
		if (map == null) {
			lock.lock();
			if (map == null) {
				initMap();
			}
			lock.unlock();
		}
		return map.get(status);
	}

	/**
	 * 初始化
	 */
	private static void initMap() {
		map = new HashMap<>();
		for (StallUpExceptionEnum stallUpExceptionEnum : StallUpExceptionEnum.values()) {
			map.put(stallUpExceptionEnum.getStatus(), stallUpExceptionEnum);
		}
	}

	public static boolean isExceptionEnd(Integer status) {
		if (status == null) {
			return false;
		}
		if (map == null) {
			lock.lock();
			if (map == null) {
				initMap();
			}
			lock.unlock();
		}
		return map.keySet().contains(status);
	}

}
