package com.shinemo.businessentry.domain.model;

import lombok.Data;

import java.util.List;

/**
 * @author ChenQinHai
 * @date 2020/9/1
 */
@Data
public class CompanyMesVO {
    /*企业名称*/
    private String competitionCompany;
    /*业务列表*/
    private List<BusinessInfoVO> competitionBusinessInfos;

    @Data
    public static  class BusinessInfoVO {
        /*业务名*/
        private String businessName;
        /*过期时间*/
        private String expireTime;
        /*其他信息*/
        private String remark;
    }
}



