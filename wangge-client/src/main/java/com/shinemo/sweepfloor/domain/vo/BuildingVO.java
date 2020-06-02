package com.shinemo.sweepfloor.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 楼栋vo
 */
@Data
public class BuildingVO implements Comparable<BuildingVO>{
    /** 小区id */
    private String communityId;
    /** 小区名 */
    private String communityName;
    /** 楼栋id */
    private String buildingId;
    /** 楼栋名 */
    private String buildingName;
    /** 楼层数 */
    private Integer floorNumber;
    /** 单元数 */
    private Integer unitCount;
    /** 单层户数 */
    private Integer householderCountEveryFloor;
    /** 住户数 */
    private Integer householderCount;
    /** 宽带运营商类型 */
    private List<String> broadbandType;
    /** 宽带用户数 */
    private Integer broadbandUserCount;
    /** 端口余量 */
    private Integer remainingPortCount;
    /** 渗透率 */
    private String penetrationRate;
    /** 标签 */
    private List<String> labels;
    /** 排序字段 */
    private String stringSort;
    private Integer numSort;

    @Override
    public int compareTo(BuildingVO o) {
        char[] chars = o.getStringSort().toCharArray();
        char[] chars1 = this.getStringSort().toCharArray();

        if (chars.length > chars1.length) {
            return -1;
        }else if (chars.length == chars1.length) {
            for (int i = 0;i < chars.length;i++) {
                char a = chars[i];
                char b = chars1[i];
                if (a != b) {
                    return a>b?-1:1;
                }
            }
        }else {
            return 1;
        }
        return 0;
    }
}
