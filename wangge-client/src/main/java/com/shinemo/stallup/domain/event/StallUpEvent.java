package com.shinemo.stallup.domain.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 摆摊事件
 *
 * @author Chenzhe Mao
 * @date 2020-04-01
 */
@Data
@AllArgsConstructor
public class StallUpEvent {

	private StallUpEventEnum stallUpEvent;

	public enum StallUpEventEnum {
		//新建摆摊
		CREATE,
		//取消摆摊
		CANCEL,
		//摆摊打卡
		SIGN,
		//结束摆摊
		END,
		//自动结束
		AUTO_END;
	}
}
