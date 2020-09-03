package com.shinemo.wangge.web.controller.businessentry;


import com.shinemo.businessentry.domain.request.BusinessAddRequest;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.domain.SmartGridContext;
import com.shinemo.wangge.core.service.thirdapi.ThirdApiMappingV2Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("businessEntry")
@Slf4j
public class BusinessEntryController {

    @Resource
    private ThirdApiMappingV2Service thirdApiMappingV2Service;


    @PostMapping(value = "/add")
    public ApiResult<Void> add(@RequestBody BusinessAddRequest request){
        Assert.notNull(request, "request is null");
        Assert.notNull(request.getGroupName(), "groupName is null");
        Assert.notNull(request.getGroupAddress(), "groupAddress is null");
        Assert.notNull(request.getGroupAddressDetails(), "groupAddressDetails is null");
        Assert.notNull(request.getContactPerson(), "ContactPerson is null");
        Assert.notNull(request.getContactMobile(), "ContactMobile is null");
        Assert.notNull(request.getIntentionBusiness(), "IntentionBusiness is null");
        Assert.notNull(request.getBusinessContent(), "BusinessContent is null");
        Assert.notNull(request.getCompanyMessage(), "companyMessage is null");
        Assert.notNull(request.getCompetitionCompany(), "CompetitionCompany is null");

        /*拼接友商参数*/
        StringBuilder compayBuilder=new StringBuilder();
        for (String business:request.getCompetitionCompany()) {
            compayBuilder.append(business).append(",");
        }

        /*拼接意向业务*/
        StringBuilder builder=new StringBuilder();
        for (String business:request.getIntentionBusiness()) {
            builder.append(business).append(",");
        }

        /*格式化时间*/
//        List<BusinessInfoVO> infoVOS=request.getCompetitionBusinessInfos();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        for (BusinessInfoVO infoVO: infoVOS) {
//            if (StringUtils.isNotBlank(infoVO.getExpireTime())){
//                long lt = new Long(infoVO.getExpireTime());
//                Date date = new Date(lt);
//                infoVO.setExpireTime(simpleDateFormat.format(date));
//            }
//        }

        Map<String,Object> map = new HashMap<>();
        map.put("groupName",request.getGroupName());
        map.put("groupAddress",request.getGroupAddress());
        map.put("groupAddressDetails",request.getGroupAddressDetails());
        map.put("contactPerson",request.getContactPerson());
        map.put("contactMobile",request.getContactMobile());
        map.put("businessContent",request.getBusinessContent());
        map.put("inputUserName",SmartGridContext.getUserName());
        map.put("inputUserMobile", SmartGridContext.getMobile());
        map.put("competitionCompany",compayBuilder.toString());
        map.put("intentionBusiness",builder.toString());

//        map.put("competitionBusinessInfos",infoVOS);

        if (StringUtils.isNotBlank(request.getGroupCode())){
            map.put("groupCode",request.getGroupCode());
        }
        if (StringUtils.isNotBlank(request.getLocation())){
            map.put("location",request.getLocation());
        }

//        map.put("remark","");
//        thirdApiMappingV2Service.asyncDispatch(map,"",SmartGridContext.getMobile());
        return ApiResult.of(0);

    }


}
