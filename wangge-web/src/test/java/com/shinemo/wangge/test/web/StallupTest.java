package com.shinemo.wangge.test.web;

import com.shinemo.cmmc.report.client.common.ErrorCodes;
import com.shinemo.common.tools.exception.ApiException;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.enums.StallUpStatusEnum;
import com.shinemo.stallup.domain.model.GridUserDetail;
import com.shinemo.stallup.domain.request.StallUpCreateRequest;
import com.shinemo.stallup.domain.request.StallUpSignRequest;
import com.shinemo.wangge.core.service.common.ExcelService;
import com.shinemo.wangge.core.service.stallup.StallUpService;
import com.shinemo.wangge.web.MainApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @Author shangkaihui
 * @Date 2020/6/16 10:36
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
public class StallupTest {

    @Resource
    private StallUpService stallUpService;

    @Resource
    private ExcelService excelService;

    @Test
    public void testSmsHot() {
        ApiResult<String> result = stallUpService.getRedirctSmsHotUrl(332L);
        System.out.println("result = " + result);
    }

    @Test
    public void testCreate() {
        StallUpCreateRequest request = new StallUpCreateRequest();

        request.setUid(1L);
        request.setMobile("13588039023");
        request.setOrgId(123123L);
        request.setTitle("测试代办摆摊");
        request.setStartTime(System.currentTimeMillis()-60*60*24);
        request.setEndTime(System.currentTimeMillis() +  60 * 60 * 24);
        request.setCommunityId("1");
        request.setCommunityName("金海小区");
        request.setAddress("测试摆摊地址");
        request.setGridId("123");
        request.setPartnerList(
                Arrays.asList(
                        GridUserDetail.builder()
                                .seMobile("asd")
                                .name("XXX")
                                .role("网格长")
                                .build()
                        ,
                        GridUserDetail.builder()
                                .seMobile("asd")
                                .name("XXX")
                                .role("网格看护人")
                                .build()
                )
        );
        request.setBizTypeList(
                Arrays.asList(1L, 2L, 3L, 4L)
        );
        request.setCustList(
                Arrays.asList("1","2","3")
        );
        request.setLocation("121,31");
        request.setStatus(StallUpStatusEnum.NOT_EXIST.getId());

        stallUpService.create(request);

    }

    @Test
    public void testSign() {
        StallUpSignRequest request = new StallUpSignRequest();
        Long id = 277L;
        Long uid = 1234L;
        request.setUid(uid);
        request.setId(id);
        request.setStatus(1);
        request.setAddress("广西省南宁市青秀区五合大道108号");
        request.setLocation("121,31");
        request.setMobile("13588039023");
        try {
            stallUpService.sign(request);
        } catch (Exception e) {
            throw new ApiException(ErrorCodes.BASE_ERROR);
        }
    }

    @Test
    public void testSendMail() {
        excelService.sendLoginInfoMail("2020-08-01");
    }
}
