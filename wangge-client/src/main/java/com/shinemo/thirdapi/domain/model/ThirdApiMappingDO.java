package com.shinemo.thirdapi.domain.model;

import lombok.Data;

import java.util.Date;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 09:58
 * @Desc
 */
@Data
public class ThirdApiMappingDO {

    private Long id;

    private Date gmtCreate;

    private Date gmtModified;

    /**
     * 接口唯一标识
     */
    private String apiName;

    /**
     * 第三方接口url
     */
    private String url;

    /**
     * 接口方法名
     */
    private String method;

    /**
     * 接口描述
     */
    private String description;

    /**
     * 状态
     *
     * @see com.shinemo.thirdapi.common.enums.ThirdApiStatusEnum
     */
    private Integer status;

    /**
     * 请求类型
     */
    private String contentType;

    /**
     * 请求方式
     */
    private String httpMethod;

    /**
     * 标位
     */
    private Integer flag;

    /**
     * 第三方类型
     *
     * @see com.shinemo.thirdapi.common.enums.ThirdApiTypeEnum
     */
    private Integer type;

    /**
     * 超时时间
     */
    private Integer timeout;

    /**
     * mock数据
     */
    private String mockData;

    /**
     * 备注
     */
    private String remark;


    public Boolean isIgnoreMobile() {
        if (flag != null) {
            return (flag.intValue() & 1) == 1;
        }
        return false;
    }

    /**
     * 是否分页
     * @return
     */
    public Boolean isPage() {
        if (flag != null) {
            return (flag.intValue() & 2) == 2;
        }
        return false;
    }

    /**
     * data是否是数组
     * @return
     */
    public Boolean dataArrarFlag() {
        if (flag != null) {
            return (flag.intValue() & 4) == 4;
        }
        return false;
    }

}
