package com.shinemo.stallup.domain.enums;

import com.shinemo.stallup.domain.params.DuDaoParams;
import com.shinemo.stallup.domain.params.MaDianParams;
import com.shinemo.stallup.domain.params.SuiShenXingParams;
import com.shinemo.stallup.domain.params.ZhuangWeiParams;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 第三方类型枚举
 *
 * @author Chenzhe Mao
 * @date 2020-04-26
 */
@AllArgsConstructor
public enum ThirdHandlerTypeEnum {
	DEFAULT(1, "直接跳转",null),
	MA_DIAN_AD(2, "码店业务办理", MaDianParams.class),
	MA_DIAN_BIG_DATA(3, "码店大数据营销工具不带查询手机号",null),
	//todo
	SMS_HOT(4, "短信预热",null),
	MA_DIAN_INTELLIGENT(5, "码店智慧查询不带查询手机号",null),
	SUI_SHEN_XING(6, "随身行", SuiShenXingParams.class),
	DAO_SAN_JIAO(7, "倒三角",null),
	MA_DIAN_BIG_DATA_WITH_MOBILE(8, "码店大数据营销工具带查询手机号",null),
	MA_DIAN_INTELLIGENT_WITH_MOBILE(9, "码店智慧查询带查询手机号",null),
	DU_DAO(10, "督导H5", DuDaoParams.class),
	DU_DAO_QUERY(11, "督导接口", DuDaoParams.class),
	ZHUANG_WEI(12, "装维系统", ZhuangWeiParams.class),
	QUAN_JING(13, "全景系统", null),
	JI_HE(14, "稽核工作", null),
	;
	@Getter
	private Integer type;
	@Getter
	private String desc;
	@Getter
	private Class clazz;
	private static volatile Map<Integer, ThirdHandlerTypeEnum> map;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 根据状态获取枚举类型
	 */
	public static ThirdHandlerTypeEnum getByType(Integer type) {
		if (type == null) {
			return null;
		}
		if (map == null) {
			lock.lock();
			if (map == null) {
				initMap();
			}
			lock.unlock();
		}
		return map.get(type);
	}

	/**
	 * 初始化
	 */
	private static void initMap() {
		map = new HashMap<>();
		for (ThirdHandlerTypeEnum thirdHandlerTypeEnum :ThirdHandlerTypeEnum.values()) {
			map.put(thirdHandlerTypeEnum.getType(), thirdHandlerTypeEnum);
		}
	}
}

