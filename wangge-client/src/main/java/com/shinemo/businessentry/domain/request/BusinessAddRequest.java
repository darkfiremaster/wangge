package com.shinemo.businessentry.domain.request;

import com.shinemo.businessentry.domain.model.CompanyMesVO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author ChenQinHai
 * @date 2020/9/1
 */
@Data
@AllArgsConstructor
public class BusinessAddRequest {

    /*集团名*/
    private String groupName;
    /*集团编码*/
    private String groupCode;
    /*集团地址*/
    private String groupAddress;
    /*详细地址*/
    private String groupAddressDetails;
    /*坐标*/
    private String location;
    /*集团联系人*/
    private String contactPerson;
    /*集团联系方式*/
    private String contactMobile;
    /*意向业务*/
    private List<String> intentionBusiness;
    /*商情内容*/
    private String businessContent;
    /*友商*/
    private List<String> competitionCompany;
    /*友商业务*/
    private List<CompanyMesVO> companyMessage;

}
