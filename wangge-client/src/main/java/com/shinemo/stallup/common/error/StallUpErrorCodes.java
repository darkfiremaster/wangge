package com.shinemo.stallup.common.error;

import com.shinemo.common.tools.exception.ErrorCode;

/**
 * 错误
 *
 * @author Chenzhe Mao
 * @date 2020-04-25
 */
public interface StallUpErrorCodes {
	ErrorCode BASE_ERROR = new ErrorCode(500, "系统繁忙，请稍后尝试！");
	ErrorCode PARAM_ERROR = new ErrorCode(501, "参数错误");
	ErrorCode ALREADY_CANCEL_ERROR = new ErrorCode(1000, "该摆摊计划已取消！");
	ErrorCode ALREADY_SIGN_ERROR = new ErrorCode(1002, "该摆摊计划已打卡！");
	ErrorCode ALREADY_END_ERROR = new ErrorCode(1003, "该摆摊计划已结束！");
	ErrorCode TIME_ERROR = new ErrorCode(2001, "计划结束时间不能早于计划开始时间！");
	ErrorCode NO_IMAGE_ERROR = new ErrorCode(2002, "请先上传图片！");
	ErrorCode IMAGE_NUM_ERROR = new ErrorCode(2003, "图片数量不能超过4张！");
	ErrorCode SIGN_RECORD_NOT_EXIST = new ErrorCode(2004, "签到记录不存在！");
	ErrorCode TITLE_LENGTH_ERROR = new ErrorCode(2005, "标题长度不能超过二十字！");
	ErrorCode SIGN_ERROR = new ErrorCode(2006, "请先结束已开始的摆摊计划！");
	ErrorCode SAVE_STATUS_ERROR = new ErrorCode(2007, "摆摊计划不是已开始状态！");
	ErrorCode SIGN_DISTANCE_ERROR = new ErrorCode(2008, "当前位置不在打卡范围！");
	ErrorCode FREQUENT_ERROR = new ErrorCode(2009, "操作太频繁啦，请稍后再试！");
	ErrorCode BIZ_TYPE_ERROR = new ErrorCode(2010, "不支持的业务类型！");
	ErrorCode PARTNER_EMPTY = new ErrorCode(2011, "参与人列表不能为空！");
	ErrorCode HUAWEI_API_ERROR = new ErrorCode(3001, "调用华为接口异常");
	ErrorCode GRID_ERROR = new ErrorCode(3002, "非网格系统用户");
}