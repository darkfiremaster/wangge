package com.shinemo.stallup.domain.enums;

import com.shinemo.stallup.common.statemachine.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 摆摊状态枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-27
 */
@AllArgsConstructor
public enum StallUpStatusEnum implements BaseEnum<StallUpStatusEnum> {

	NOT_EXIST(-1, "不存在"),
	CANCELED(0, "已取消"),
	PREPARE(1, "待开始"),
	STARTED(2, "已开始"),
	END(3, "已结束"),
	AUTO_END(4, "超时自动结束"),
	//签退打卡超过距离限制
	ABNORMAL_END(5, "异常结束");

	private @Getter
	final int id;
	private @Getter
	final String name;
	private static volatile Map<Integer, StallUpStatusEnum> map;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 根据状态获取枚举类型
	 */
	public static StallUpStatusEnum getById(Integer id) {
		if (id == null) {
			return null;
		}
		if (map == null) {
			lock.lock();
			if (map == null) {
				initMap();
			}
			lock.unlock();
		}
		return map.get(id);
	}

	/**
	 * 初始化
	 */
	private static void initMap() {
		map = new HashMap<>();
		for (StallUpStatusEnum stallUpStatusEnum : StallUpStatusEnum.values()) {
			map.put(stallUpStatusEnum.getId(), stallUpStatusEnum);
		}
	}
}
