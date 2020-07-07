package com.shinemo.wangge.test.web;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.domain.utils.EncryptUtil;
import com.shinemo.stallup.domain.utils.Md5Util;
import com.shinemo.todo.domain.TodoDO;
import com.shinemo.todo.domain.TodoLogDO;
import com.shinemo.todo.enums.ThirdTodoTypeEnum;
import com.shinemo.todo.query.TodoLogQuery;
import com.shinemo.todo.query.TodoQuery;
import com.shinemo.todo.query.TodoUrlQuery;
import com.shinemo.wangge.core.service.todo.TodoLogService;
import com.shinemo.wangge.core.service.todo.TodoService;
import com.shinemo.wangge.core.service.todo.impl.TodoRedirectUrlServiceImpl;
import com.shinemo.wangge.dal.mapper.ThirdTodoMapper;
import com.shinemo.wangge.web.MainApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Author shangkaihui
 * @Date 2020/6/17 13:58
 * @Desc
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApplication.class)
@Slf4j
public class TodoTest {

    @Resource
    private TodoLogService todoLogService;

    @Resource
    private TodoRedirectUrlServiceImpl todoRedirectUrlService;

    @Resource
    private TodoService todoService;



    @Test
    public void testGetRedirectUrl() {
        String seed = "ffd40e661eb946f48fd3c759e6b8ef0b";
        String mobile = "13588039023";
        String token = "xxxx";
        String thirdId = "6c5127a3-9f1c-11ea-a34d-5254001a0735";
        Integer thirdType=1;
        Integer redirectPage=1;
        long timestamp = System.currentTimeMillis();
        Map<String, Object> formData = new HashMap<>();
        formData.put("mobile", mobile);
        formData.put("thirdId", thirdId);
        formData.put("timestamp", timestamp);
        formData.put("token", token);
        formData.put("thirdType", thirdType);
        formData.put("redirectPage", redirectPage);
        String paramStr = EncryptUtil.buildParameterString(formData);
        //1、加密
        String encryptData = EncryptUtil.encrypt(paramStr, seed);
        System.out.println("encrypt:"+encryptData);
        //2、生成签名
        String sign = Md5Util.getMD5Str(encryptData+","+seed+","+timestamp);
        System.out.println("sign:"+sign);

        String url = "https://developer.e.uban360.com/cmgr-gx-smartgrid/todo/redirectPage?";
        StringBuilder sb = new StringBuilder(url);
        url = sb.append("paramData=").append(encryptData)
                .append("&timestamp=").append(timestamp)
                .append("&sign=").append(sign)
                .append("&thirdType=").append(thirdType)
                .toString();

        System.out.println("url = " + url);
    }

    @Test
    public void testGetZhuangYiAppUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.ZHUANG_YI_ORDER.getId());
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("1-15890-13617712720");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetChannelVisitUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.CHANNEL_VISIT.getId());
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("1-15890-13617712720");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetYujingUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(5);
        todoUrlQuery.setOperatorMobile("13617712720");
        todoUrlQuery.setThirdId("DK202006240707281");
        todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
    }

    @Test
    public void testGetDaosanjiaoUrl() {
        TodoUrlQuery todoUrlQuery = new TodoUrlQuery();
        todoUrlQuery.setThirdType(ThirdTodoTypeEnum.DAO_SAN_JIAO_ORDER.getId());
        todoUrlQuery.setThirdId("6c5127a3-9f1c-11ea-a34d-5254001a0735");
        todoUrlQuery.setOperatorMobile("17380571067");
        ApiResult<String> result = todoRedirectUrlService.getRedirectUrl(todoUrlQuery);
        System.out.println("result = " + result);
    }

    @Test
    public void testOperateTodoThing() {
        TreeMap<String, Object> treeMap = new TreeMap<>();
        treeMap.put("key","34b18faa-0424-41ad-b73b-80fc02d4be55");
        treeMap.put("method","operateTodoThing");
        treeMap.put("postBody","34b18faa-0424-41ad-b73b-80fc02d4be55");
        treeMap.put("sign","34b18faa-0424-41ad-b73b-80fc02d4be55");

    }

    @Test
    public void testGetTodoLogList() {
        TodoLogQuery todoLogQuery = new TodoLogQuery();

        //todoLogQuery.setId();
        todoLogQuery.setCompany("讯盟");
        todoLogQuery.setThirdType(8);
        todoLogQuery.setThirdId(String.valueOf(294));
        todoLogQuery.setOperatorMobile("13107701611");
        todoLogQuery.setStatus(1);
        todoLogQuery.setStartTime("2020-06-01 00:00:00");
        todoLogQuery.setEndTime("2020-06-30 23:59:59");
        todoLogQuery.setCurrentPage(1);
        todoLogQuery.setPageSize(100);
        todoLogQuery.setOrderBy("id desc");
        String request = GsonUtils.toJson(todoLogQuery);
        System.out.println("request = " + request);
        ApiResult<List<TodoLogDO>> result = todoLogService.getTodoLogList(todoLogQuery);
        System.out.println("result = " + result);
    }

    @Test
    public void testGetTodoList() {
        TodoQuery todoQuery = new TodoQuery();
        todoQuery.setThirdId("294");
        todoQuery.setThirdType(8);
        todoQuery.setTitle("哈哈");
        todoQuery.setMobile("13107701611");
        todoQuery.setStatus(1);
        todoQuery.setStartTime(DateUtil.parseDateTime("2020-06-01 00:00:00"));
        todoQuery.setEndTime(DateUtil.parseDateTime("2020-06-30 23:59:59"));
        todoQuery.setCurrentPage(1);
        todoQuery.setPageSize(100);
        String request = GsonUtils.toJson(todoQuery);
        System.out.println("request = " + request);
        ApiResult<List<TodoDO>> result = todoLogService.getTodoList(todoQuery);
        System.out.println("result = " + result);
    }



    @Resource
    private ThirdTodoMapper thirdTodoMapper;
    @Test
    public void insertTodo() {
        String json = " {\n" +
                "            \"id\": 38,\n" +
                "            \"gmtCreate\": 1593412724000,\n" +
                "            \"gmtModified\": 1593412724000,\n" +
                "            \"thirdId\": \"13588039023\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"测试装维\",\n" +
                "            \"remark\": \"测试装维\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"未返单\",\n" +
                "            \"operatorTime\": 1592928000000,\n" +
                "            \"operatorMobile\": \"13588039023\",\n" +
                "            \"startTime\": 1593792000000\n" +
                "        }";
        TodoDO todoDO = GsonUtils.fromGson2Obj(json, TodoDO.class);
        System.out.println("todoDO = " + todoDO);
        thirdTodoMapper.insert(todoDO);
    }

    @Test
    public void insertTodoList() {
        String json = "[\n" +
                "        {\n" +
                "            \"id\": 55,\n" +
                "            \"gmtCreate\": 1594051747000,\n" +
                "            \"gmtModified\": 1594052830000,\n" +
                "            \"thirdId\": \"C-771-20200707-370001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"魔百盒-null-null-新装\",\n" +
                "            \"remark\": \"工单时限：2020-07-09 12:00:00.0\\n预约时间：2020-07-09 08:00:00 - 2020-07-09 12:00:00\\n装机地址：广西南宁青秀区青秀区城区长湖路13号长湖景苑1栋12楼1201\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1594052830000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 54,\n" +
                "            \"gmtCreate\": 1594051400000,\n" +
                "            \"gmtModified\": 1594051400000,\n" +
                "            \"thirdId\": \"C-771-20200707-360001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"魔百盒-null-null-新装\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间： - \\n装机地址：广西南宁青秀区青秀区城区长湖路13号长湖景苑1栋12楼1201\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1594051400000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 53,\n" +
                "            \"gmtCreate\": 1593924331000,\n" +
                "            \"gmtModified\": 1593924924000,\n" +
                "            \"thirdId\": \"C-771-20200616-200001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"千里眼-null-null-新装\",\n" +
                "            \"remark\": \"工单时限：2020-06-18 20:00:00.0\\n预约时间： - \\n装机地址：广西南宁马山县白山镇金伦大道中段巴更路片区金轮大道632号\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593924924000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 52,\n" +
                "            \"gmtCreate\": 1593924331000,\n" +
                "            \"gmtModified\": 1593924924000,\n" +
                "            \"thirdId\": \"C-771-20200602-620001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-1000M-FTTH-移机-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-06-02 23:59:59.0\\n预约时间： - \\n装机地址：广西南宁西乡塘区西乡塘区城区安武大道北湖村山东坡38号3楼\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593924924000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 51,\n" +
                "            \"gmtCreate\": 1593845848000,\n" +
                "            \"gmtModified\": 1593923965000,\n" +
                "            \"thirdId\": \"C-772-20200610-040001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-1000M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-06-12 12:00:00.0\\n预约时间： - \\n装机地址：广西柳州柳南区柳南区城区龙屯路（H可200M）新云村龙屯旁50号商店后面5楼\",\n" +
                "            \"status\": 1,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593923965000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 50,\n" +
                "            \"gmtCreate\": 1593845345000,\n" +
                "            \"gmtModified\": 1593845552000,\n" +
                "            \"thirdId\": \"C-771-20200602-660001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-1000M-FTTH-终端更换\",\n" +
                "            \"remark\": \"工单时限：2020-06-05 12:00:00.0\\n预约时间：2020-06-05 08:00:00 - 2020-06-05 12:00:00\\n客户名称：苏泳琮\\n装机地址：广西南宁武鸣县城厢镇灵源路灵水三区3号民房\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593845552000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 49,\n" +
                "            \"gmtCreate\": 1593765259000,\n" +
                "            \"gmtModified\": 1593765312000,\n" +
                "            \"thirdId\": \"C-771-20200703-300001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-1000M-FTTH-移机-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-07-05 20:00:00.0\\n预约时间： - \\n客户名称：李胜权\\n装机地址：广西南宁西乡塘区西乡塘区城区安武大道北湖村山东坡38号3楼\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593765312000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 48,\n" +
                "            \"gmtCreate\": 1593752614000,\n" +
                "            \"gmtModified\": 1593752614000,\n" +
                "            \"thirdId\": \"C-771-20170306-000868\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"IMS-12M-FTTB-新装\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2017-03-19 12:00:00 - 2017-03-19 20:00:00\\n客户名称：凤姐\\n装机地址：广西|南宁|青秀区|长塘镇|长塘街|长塘街片区|政府\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593752614000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 47,\n" +
                "            \"gmtCreate\": 1593750126000,\n" +
                "            \"gmtModified\": 1593750442000,\n" +
                "            \"thirdId\": \"C-771-20170306-000870\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-20M-FTTB-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2017-12-25 12:00:00 - 2017-12-25 20:00:00\\n客户名称：卢宪刚\\n装机地址：广西|南宁|邕宁区|百济镇|百济街|中学教师宿舍|2单元\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593750442000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 46,\n" +
                "            \"gmtCreate\": 1593748316000,\n" +
                "            \"gmtModified\": 1593749925000,\n" +
                "            \"thirdId\": \"C-771-20200509-020001\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-1000M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：2020-05-10 12:00:00.0\\n预约时间： - \\n客户名称：广西2019429952\\n装机地址：广西南宁青秀区青秀区城区富兴路81-1号世贸东方小区宿舍楼2栋3楼303\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待预约\",\n" +
                "            \"operatorTime\": 1593749925000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 45,\n" +
                "            \"gmtCreate\": 1593744635000,\n" +
                "            \"gmtModified\": 1593762180000,\n" +
                "            \"thirdId\": \"C-771-20170217-000540\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-12M-FTTB-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2017-12-07 12:00:00 - 2017-12-07 20:00:00\\n客户名称：陆江日\\n装机地址：广西|南宁|横县|横州镇|金龙街|富丽花园小区|A栋|2单元\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593762180000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 44,\n" +
                "            \"gmtCreate\": 1593743923000,\n" +
                "            \"gmtModified\": 1593743923000,\n" +
                "            \"thirdId\": \"C-771-20170209-000405\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-12M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2019-07-16 08:00:00 - 2019-07-16 12:00:00\\n客户名称：广西2122215802\\n装机地址：广西|南宁|青秀区|伶俐镇|伶俐街|伶俐街片区|伶俐镇政府大院2栋外墙\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593743922000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 43,\n" +
                "            \"gmtCreate\": 1593743527000,\n" +
                "            \"gmtModified\": 1593743527000,\n" +
                "            \"thirdId\": \"C-771-20170116-000171\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-20M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2019-07-27 08:00:00 - 2019-07-27 12:00:00\\n客户名称：广西1111722142\\n装机地址：广西|南宁|兴宁区|兴宁区城区|望州南路|180号党校小区|9栋|1层\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593743527000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 42,\n" +
                "            \"gmtCreate\": 1593677688000,\n" +
                "            \"gmtModified\": 1594091524000,\n" +
                "            \"thirdId\": \"C-771-20170118-000290\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-12M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2018-12-29 08:00:00 - 2018-12-29 12:00:00\\n装机地址：广西|南宁|江南区|江南区城区|淡村路北一里|12号市二医院宿舍|2栋|2单元1楼\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1594091524000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 41,\n" +
                "            \"gmtCreate\": 1593674539000,\n" +
                "            \"gmtModified\": 1593677307000,\n" +
                "            \"thirdId\": \"C-771-20170116-000101\",\n" +
                "            \"thirdType\": 4,\n" +
                "            \"title\": \"家庭宽带-12M-FTTH-新装-正装机\",\n" +
                "            \"remark\": \"工单时限：\\n预约时间：2018-07-25 08:00:00 - 2018-07-25 12:00:00\\n客户名称：广西2027717340\\n装机地址：广西|南宁|江南区|江南区城区|石柱岭一路|13号广西轻工高级技工学校|教师宿舍A栋|2单元|分光器\",\n" +
                "            \"status\": 0,\n" +
                "            \"label\": \"待施工\",\n" +
                "            \"operatorTime\": 1593677307000,\n" +
                "            \"operatorMobile\": \"18776892034\"\n" +
                "        }\n" +
                "    ]";
        List<TodoDO> todoDOList = GsonUtils.fromJsonToList(json, TodoDO[].class);
        for (TodoDO todoDO : todoDOList) {
            TodoQuery todoQuery = new TodoQuery();
            todoQuery.setThirdId(todoDO.getThirdId());
            todoQuery.setThirdType(todoDO.getThirdType());
            todoQuery.setMobile(todoDO.getOperatorMobile());
            TodoDO todoDB = thirdTodoMapper.get(todoQuery);
            if (todoDB == null) {
                //新增
                todoDO.setId(null);
                thirdTodoMapper.insert(todoDO);
                log.info("新增成功");
            } else {
                //更新
                TodoDO todoDO1 = new TodoDO();
                BeanUtil.copyProperties(todoDO, todoDO1);
                todoDO1.setId(todoDB.getId());
                thirdTodoMapper.update(todoDO);
                log.info("修改成功,id:{}", todoDO1.getId());
            }
        }
    }
}
