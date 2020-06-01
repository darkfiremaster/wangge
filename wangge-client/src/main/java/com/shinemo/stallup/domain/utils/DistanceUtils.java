package com.shinemo.stallup.domain.utils;

import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.stallup.common.error.StallUpErrorCodes;
import lombok.extern.slf4j.Slf4j;

/**
 * 距离工具类
 *
 * @author harold
 * @version 2016.02.02.V1.0
 */
@Slf4j
public class DistanceUtils {
	/**
	 * 地球半径
	 */
	private static final double EARTH_RADIUS = 6378137;
	/**
	 * 地球半径的单位, 米
	 */
	private static final DistanceUnit EARTH_RADIUS_UNIT = DistanceUnit.m;

	/**
	 * 计算弧长
	 *
	 * @param d 经纬度
	 * @return 弧长
	 */
	private static double rad(double d) {
		return d * Math.PI / 180.0; // 计算弧长
	}

	/**
	 * 根据两点经纬度, 计算两点的距离
	 *
	 * @param unit 单位
	 * @param Lat1 地点1的纬度
	 * @param Lon1 地点1的经度
	 * @param Lat2 地点2的纬度
	 * @param Lon2 地点2的经度
	 * @return 距离
	 */
	public static double getDistanceFromCoordinates(DistanceUnit unit, double Lat1, double Lon1, double Lat2,
	                                                double Lon2) {
		double radLat1 = rad(Lat1);
		double radLat2 = rad(Lat2);
		double a = radLat1 - radLat2;
		double b = rad(Lon1) - rad(Lon2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS * unit.getUnit(EARTH_RADIUS_UNIT);
		return s;
	}

	/**
	 * 根据两点经纬度, 计算两点的距离
	 *
	 * @param Lat1 地点1的纬度
	 * @param Lon1 地点1的经度
	 * @param Lat2 地点2的纬度
	 * @param Lon2 地点2的经度
	 * @return 距离, 单位米
	 */
	public static double getDistanceFromCoordinates(double Lat1, double Lon1, double Lat2, double Lon2) {
		return getDistanceFromCoordinates(DistanceUnit.m, Lat1, Lon1, Lat2, Lon2);
	}

	/**
	 * 距离单位
	 *
	 * @author harold
	 * @version 2016.02.02.V1.0
	 */
	public enum DistanceUnit {
		/**
		 * 幺米
		 */
		ym(24),
		/**
		 * 仄米
		 */
		zm(21),
		/**
		 * 阿米
		 */
		am(18),
		/**
		 * 飞米
		 */
		fm(15),
		/**
		 * 皮米
		 */
		pm(12),
		/**
		 * 埃米
		 */
		A(10),
		/**
		 * 纳米
		 */
		nm(9),
		/**
		 * 微米
		 */
		umm(6),
		/**
		 * 忽米
		 */
		cmm(5),
		/**
		 * 丝米
		 */
		dmm(4),
		/**
		 * 毫米
		 */
		mm(3),
		/**
		 * 厘米
		 */
		cm(2),
		/**
		 * 分米
		 */
		dm(1),
		/**
		 * 米
		 */
		m(0),
		/**
		 * 十米
		 */
		dam(-1),
		/**
		 * 百米
		 */
		hm(-2),
		/**
		 * 千米
		 */
		km(-3),
		/**
		 * 兆米
		 */
		Mm(-6),
		/**
		 * 京米
		 */
		Gm(-9),
		/**
		 * 太米
		 */
		Tm(-12),
		/**
		 * 拍米
		 */
		Pm(-15),
		/**
		 * 光年
		 */
		Ly(9.46, -15),
		/**
		 * 艾米
		 */
		Em(-18),
		/**
		 * 泽米
		 */
		Zm(-21),
		/**
		 * 尧米
		 */
		Ym(-24);

		/**
		 * 单位换算乘数
		 */
		private double unit;

		/**
		 * 系数默认为1
		 *
		 * @param index 指数
		 */
		private DistanceUnit(int index) {
			unit = Math.pow(10, index);
		}

		/**
		 * @param coefficient 系数
		 * @param index       指数
		 */
		private DistanceUnit(double coefficient, int index) {
			unit = coefficient * Math.pow(10, index);
		}

		/**
		 * 获取米换算到当前单位需要相乘的数
		 *
		 * @return 需要相乘的数
		 */
		public double getUnit() {
			return getUnit(m);
		}

		/**
		 * 获取from单位换算到当前单位需要相乘的数
		 *
		 * @param from 原单位
		 * @return 相乘的数
		 */
		public double getUnit(DistanceUnit from) {
			return this.unit / from.unit;
		}
	}

	/**
	 * 运算符号
	 *
	 * @author harold
	 * @version 2016.02.02.V1.0
	 */
	public enum Operators {
		/**
		 * 加
		 */
		PLUS,
		/**
		 * 减
		 */
		MINUS,
		/**
		 * 乘
		 */
		MULTIPLY,
		/**
		 * 除
		 */
		DIVIDE
	}

	/**
	 * 计算距离
	 *
	 * @param src 源 经度,纬度
	 * @param des 目的 经度,纬度
	 * @return java.lang.Integer
	 */
	public static Integer getDistance(String src, String des) {

		String[] str1 = src.split(",");
		String[] str2 = des.split(",");
		if (str1.length < 2 || str2.length < 2) {
			log.error("[getDistance] arguments error, str1:{}, str2:{}", str1, str2);
			throw new ApiException(StallUpErrorCodes.BASE_ERROR);
		}
		double distance = DistanceUtils.getDistanceFromCoordinates(
			Double.parseDouble(str1[1]),
			Double.parseDouble(str1[0]),
			Double.parseDouble(str2[1]),
			Double.parseDouble(str2[0])
		);
		return (int) distance;
	}
}
