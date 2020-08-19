package com.shinemo.wangge.core.config;

import com.google.gson.reflect.TypeToken;
import com.shinemo.my.redis.service.RedisService;
import com.shinemo.smartgrid.utils.GsonUtils;
import com.shinemo.stallup.common.enums.BizGroupEnum;
import com.shinemo.stallup.domain.enums.BusinessConfigEnum;
import com.shinemo.stallup.domain.enums.ThirdHandlerTypeEnum;
import com.shinemo.stallup.domain.model.SmartGridBiz;
import com.shinemo.stallup.domain.model.StallUpBizType;
import com.shinemo.stallup.domain.params.BizParams;
import com.shinemo.stallup.domain.query.SmartGridBizQuery;
import com.shinemo.sweepfloor.domain.model.BusinessConfigDO;
import com.shinemo.sweepfloor.domain.query.BusinessConfigQuery;
import com.shinemo.wangge.core.handler.*;
import com.shinemo.wangge.dal.mapper.BusinessConfigMapper;
import com.shinemo.wangge.dal.mapper.SmartGridBizMapper;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 摆摊配置类
 *
 * @author Chenzhe Mao
 * @date 2020-04-08
 */
@Data
@Component
public class StallUpConfig {

    private ConfigDetail config;

    @Resource
    private BusinessConfigMapper businessConfigMapper;
    @Resource
    private SmartGridBizMapper smartGridBizMapper;
    @Resource
    private RedisService redisService;
    @Resource
    private SmsHotHandler smsHotHandler;
    private static final Integer BASE_CONFIG_TYPE = 1;
    private static final Integer BIZ_TYPE = 2;

    @PostConstruct
    public void init() {
        //初始化业务应用配置
        //基础配置
        BusinessConfigQuery query = new BusinessConfigQuery();
        query.setBizType(BIZ_TYPE);
        query.setType(BASE_CONFIG_TYPE);
        BusinessConfigDO businessConfigDO = businessConfigMapper.get(query);
        ConfigDetail config = GsonUtils.fromGson2Obj(businessConfigDO.getContent(), ConfigDetail.class);
        //业务配置
        query.setType(null);
        query.setTypeList(Arrays.stream(BusinessConfigEnum.values()).map(v -> v.getType()).collect(Collectors.toList()));
        List<BusinessConfigDO> businessConfigDOList = businessConfigMapper.find(query);
        Map<Integer, List<StallUpBizType>> bizConfigMap = new HashMap<>();
        businessConfigDOList.forEach(v -> bizConfigMap.put(v.getType(), GsonUtils.fromGson2Obj(v.getContent(), new TypeToken<List<StallUpBizType>>() {
        }.getType())));
        config.setBizList(bizConfigMap.get(BusinessConfigEnum.STALL_UP_BIZ.getType()));
        config.setMarketList(bizConfigMap.get(BusinessConfigEnum.STALL_UP_MARKET.getType()));
        config.setIndexList(bizConfigMap.get(BusinessConfigEnum.INDEX_BIZ.getType()));
        config.setSweepFloorList(bizConfigMap.get(BusinessConfigEnum.SWEEP_FLOOR.getType()));
        config.setSweepFloorBizList(bizConfigMap.get(BusinessConfigEnum.SWEEP_FLOOR_BIZ.getType()));

        //扫村配置
        config.setSweepVillageList(bizConfigMap.get(BusinessConfigEnum.SWEEP_VILLAGE.getType()));
        config.setSweepVillageBizList(bizConfigMap.get(BusinessConfigEnum.SWEEP_VILLAGE_BIZ.getType()));

        //集团服务日配置
        config.setGroupServiceDayToolList(bizConfigMap.get(BusinessConfigEnum.GROUP_SERVICE_DAY_TOOL.getType()));
        config.setPublicGroupServiceDayBizList(bizConfigMap.get(BusinessConfigEnum.PUBLIC_GROUP_SERVICE_DAY_BIZ.getType()));
        config.setPublicGroupServiceDayBizDataList(bizConfigMap.get(BusinessConfigEnum.PUBLIC_GROUP_SERVICE_DAY_BIZ_DATA.getType()));

        //扫街配置
        config.setSweepStreetToolList(bizConfigMap.get(BusinessConfigEnum.SWEEP_STREET_TOOL.getType()));
        config.setSweepStreetBizList(bizConfigMap.get(BusinessConfigEnum.SWEEP_STREET_BIZ.getType()));
        config.setSweepStreetBizDataList(bizConfigMap.get(BusinessConfigEnum.SWEEP_STREET_BIZ_DATA.getType()));

        //查询全量业务列表
        SmartGridBizQuery bizQuery = new SmartGridBizQuery();
        bizQuery.setStatus(1);
        List<SmartGridBiz> smartGridBizList = smartGridBizMapper.find(bizQuery);
        List<StallUpBizType> tmpList = smartGridBizList.stream()
                .map(v -> convert2BizType(v))
                .collect(Collectors.toList());
        config.setList(tmpList);

        Map<Long, StallUpBizType> tmpMap = new HashMap<>();
        config.getList().forEach(v -> tmpMap.put(v.getId(), v));
        config.setMap(tmpMap);

        //摆摊业务办理配置
        Map<Long, StallUpBizType> tmpBizMap = new HashMap<>();
        List<StallUpBizType> tmpBizList = initListAndMap(tmpMap, config.getBizList(), tmpBizMap);
        config.setBizList(tmpBizList);
        config.setBizMap(tmpBizMap);

        //摆摊营销功能配置
        Map<Long, StallUpBizType> tmpMarketMap = new HashMap<>();
        List<StallUpBizType> tmpMarketList = initListAndMap(tmpMap, config.getMarketList(), tmpMarketMap);
        config.setMarketList(tmpMarketList);
        config.setMarketMap(tmpMarketMap);

        //首页业务办理和快捷入口配置
        List<StallUpBizType> tmpIndexList = new ArrayList<>();
        List<StallUpBizType> tmpQuickAccessList = new ArrayList<>();
        List<StallUpBizType> tmpDaosanjiaoSupportList = new ArrayList<>();
        List<StallUpBizType> tmpOAOServiceList = new ArrayList<>();
        initIndex(tmpMap, config.getIndexList(), tmpIndexList, tmpQuickAccessList, tmpDaosanjiaoSupportList,tmpOAOServiceList);
        config.setIndexList(tmpIndexList);
        config.setQuickAccessList(tmpQuickAccessList);
        config.setDaosanjiaoSupportBizList(tmpDaosanjiaoSupportList);
        config.setOaoServiceList(tmpOAOServiceList);

        //扫楼配置
        Map<Long, StallUpBizType> tmpSweepFloorMap = new HashMap<>();
        List<StallUpBizType> tmpSweepFloorList = initListAndMap(tmpMap, config.getSweepFloorList(), tmpSweepFloorMap);
        config.setSweepFloorList(tmpSweepFloorList);
        config.setSweepFloorMap(tmpSweepFloorMap);

        //扫楼业务配置
        Map<Long, StallUpBizType> tmpSweepFloorBizMap = new HashMap<>();
        List<StallUpBizType> tmpSweepFloorBizList = initListAndMap(tmpMap, config.getSweepFloorBizList(), tmpSweepFloorBizMap);
        config.setSweepFloorBizList(tmpSweepFloorBizList);
        config.setSweepFloorBizMap(tmpSweepFloorBizMap);

        //扫村配置
        Map<Long, StallUpBizType> tmpSweepVillageMap = new HashMap<>();
        List<StallUpBizType> tmpSweepVillageList = initListAndMap(tmpMap, config.getSweepVillageList(), tmpSweepVillageMap);
        config.setSweepVillageList(tmpSweepVillageList);
        config.setSweepVillageMap(tmpSweepVillageMap);

        //扫村业务配置
        Map<Long, StallUpBizType> tmpSweepVillageBizMap = new HashMap<>();
        List<StallUpBizType> tmpSweepVillageBizList = initListAndMap(tmpMap, config.getSweepVillageBizList(), tmpSweepVillageBizMap);
        config.setSweepVillageBizList(tmpSweepVillageBizList);
        config.setSweepVillageBizMap(tmpSweepVillageBizMap);

        /**
         * 集团服务日配置
         */
        Map<Long, StallUpBizType> tmpGroupServiceDayToolMap = new HashMap<>();
        List<StallUpBizType> tmpGroupServiceDayToolList = initListAndMap(tmpMap, config.getGroupServiceDayToolList(), tmpGroupServiceDayToolMap);
        config.setGroupServiceDayToolList(tmpGroupServiceDayToolList);

        Map<Long, StallUpBizType> tmpPublicGroupServiceDayBizMap = new HashMap<>();
        List<StallUpBizType> tmpPublicGroupServiceDayBizList = initListAndMap(tmpMap, config.getPublicGroupServiceDayBizList(), tmpPublicGroupServiceDayBizMap);
        config.setPublicGroupServiceDayBizList(tmpPublicGroupServiceDayBizList);

        Map<Long, StallUpBizType> tmpPublicGroupServiceDayBizDataMap = new HashMap<>();
        List<StallUpBizType> tmpPublicGroupServiceDayBizDataList = initListAndMap(tmpMap, config.getPublicGroupServiceDayBizDataList(), tmpPublicGroupServiceDayBizDataMap);
        config.setPublicGroupServiceDayBizDataList(tmpPublicGroupServiceDayBizDataList);




        /**
         * 扫街配置
         */
        Map<Long, StallUpBizType> tmpSweepStreetToolMap = new HashMap<>();
        List<StallUpBizType> tmpSweepStreetToolList = initListAndMap(tmpMap, config.getSweepStreetToolList(), tmpSweepStreetToolMap);
        config.setSweepStreetToolList(tmpSweepStreetToolList);

        Map<Long, StallUpBizType> tmpSweepStreetBizMap = new HashMap<>();
        List<StallUpBizType> tmpSweepStreetBizList = initListAndMap(tmpMap, config.getSweepStreetBizList(), tmpSweepStreetBizMap);
        config.setPublicGroupServiceDayBizList(tmpSweepStreetBizList);

        Map<Long, StallUpBizType> tmpSweepStreetBizDataMap = new HashMap<>();
        List<StallUpBizType> tmpSweepStreetBizDataList = initListAndMap(tmpMap, config.getSweepStreetBizDataList(), tmpSweepStreetBizDataMap);
        config.setPublicGroupServiceDayBizDataList(tmpSweepStreetBizDataList);



        //初始化url参数拼接handler
        MaDianConfig MaDianConfig = config.getMaDianConfig();
        MaDianCommonHandler.setAppId(MaDianConfig.getAppId());
        MaDianCommonHandler.setDomain(MaDianConfig.getDomain());
        MaDianCommonHandler.setKey(MaDianConfig.getKey());

        //默认
        Map<Integer, UrlRedirectHandler> tmpUrlMap = new HashMap<>();
        tmpUrlMap.put(ThirdHandlerTypeEnum.DEFAULT.getType(), new DefaultHandler());

        //码店业务办理
        MaDianAdHandler maDianAdHandler = new MaDianAdHandler();
        maDianAdHandler.setPath(MaDianConfig.getAdPath());
        tmpUrlMap.put(ThirdHandlerTypeEnum.MA_DIAN_AD.getType(), maDianAdHandler);

        //码店大数据营销工具
        MaDianBigDataHandler maDianBigDataHandler = new MaDianBigDataHandler();
        maDianBigDataHandler.setPath(MaDianConfig.getBigDataPath());
        tmpUrlMap.put(ThirdHandlerTypeEnum.MA_DIAN_BIG_DATA.getType(), maDianBigDataHandler);
        tmpUrlMap.put(ThirdHandlerTypeEnum.MA_DIAN_BIG_DATA_WITH_MOBILE.getType(), maDianBigDataHandler);

        //短信预热配置
        smsHotHandler.setRedisService(redisService);
        tmpUrlMap.put(ThirdHandlerTypeEnum.SMS_HOT.getType(), smsHotHandler);

        //码店智慧查询
        MaDianIntelligentHandler maDianIntelligentHandler = new MaDianIntelligentHandler();
        maDianIntelligentHandler.setPath(MaDianConfig.getIntelligentPath());
        tmpUrlMap.put(ThirdHandlerTypeEnum.MA_DIAN_INTELLIGENT.getType(), maDianIntelligentHandler);
        tmpUrlMap.put(ThirdHandlerTypeEnum.MA_DIAN_INTELLIGENT_WITH_MOBILE.getType(), maDianIntelligentHandler);

        //倒三角配置
        DaoSanJiaoConfig daoSanJiaoConfig = config.getDaoSanJiaoConfig();
        DaoSanJiaoHandler daoSanJiaoHandler = new DaoSanJiaoHandler();
        daoSanJiaoHandler.setDomain(daoSanJiaoConfig.getDomain());
        daoSanJiaoHandler.setPath(daoSanJiaoConfig.getPath());
        daoSanJiaoHandler.setSid(daoSanJiaoConfig.getSid());
        daoSanJiaoHandler.setSeed(daoSanJiaoConfig.getSeed());
        tmpUrlMap.put(ThirdHandlerTypeEnum.DAO_SAN_JIAO.getType(), daoSanJiaoHandler);

        //倒三角支撑配置
        DaoSanJiaoSupportHandler daoSanJiaoSupportHandler = new DaoSanJiaoSupportHandler();
        daoSanJiaoSupportHandler.setDomain(daoSanJiaoConfig.getDomain());
        daoSanJiaoSupportHandler.setPath(daoSanJiaoConfig.getPath());
        daoSanJiaoSupportHandler.setSeed(daoSanJiaoConfig.getSeed());
        tmpUrlMap.put(ThirdHandlerTypeEnum.DANSANJIAO_SUPPORT.getType(), daoSanJiaoSupportHandler);


        //OAO上门服务配置
        OaoServiceConfig oaoServiceConfig = config.getOaoServiceConfig();
        OAOServiceHandler oaoServiceHandler = new OAOServiceHandler();
        oaoServiceHandler.setDomain(oaoServiceConfig.getDomain());
        oaoServiceHandler.setPath(oaoServiceConfig.getPath());
        oaoServiceHandler.setSeed(oaoServiceConfig.getSeed());
        tmpUrlMap.put(ThirdHandlerTypeEnum.OAO_SERVICE.getType(), oaoServiceHandler);


        //稽核工作配置
        JiHeHandler jiHeHandler = new JiHeHandler();
        jiHeHandler.setDomain(daoSanJiaoConfig.getDomain());
        jiHeHandler.setPath(daoSanJiaoConfig.getPath());
        jiHeHandler.setSeed(daoSanJiaoConfig.getSeed());
        tmpUrlMap.put(ThirdHandlerTypeEnum.JI_HE.getType(), jiHeHandler);

        //督导配置
        DuDaoConfig duDaoConfig = config.getDuDaoConfig();
        DuDaoCommonHandler.setDomain(duDaoConfig.getDomain());
        DuDaoCommonHandler.setSeed(duDaoConfig.getSeed());

        //督导h5
        DuDaoHandler duDaoHandler = new DuDaoHandler();
        duDaoHandler.setPath(duDaoConfig.getH5Path());
        tmpUrlMap.put(ThirdHandlerTypeEnum.DU_DAO.getType(), duDaoHandler);

        //督导接口
        DuDaoQueryHandler duDaoQueryHandler = new DuDaoQueryHandler();
        duDaoQueryHandler.setPath(duDaoConfig.getQueryPath());
        tmpUrlMap.put(ThirdHandlerTypeEnum.DU_DAO_QUERY.getType(), duDaoQueryHandler);

        //全景配置
        QuanJingConfig quanJingConfig = config.getQuanJingConfig();
        QuanJingHandler quanJingHandler = new QuanJingHandler();
        quanJingHandler.setDomain(quanJingConfig.getDomain());
        quanJingHandler.setPath(quanJingConfig.getPath());
        quanJingHandler.setSeed(quanJingConfig.getSeed());
        tmpUrlMap.put(ThirdHandlerTypeEnum.QUAN_JING.getType(), quanJingHandler);

        //随身行
        tmpUrlMap.put(ThirdHandlerTypeEnum.SUI_SHEN_XING.getType(), new SuiShenXingHandler());

        //装维
        tmpUrlMap.put(ThirdHandlerTypeEnum.ZHUANG_WEI.getType(), new ZhuangWeiHandler());

        config.setUrlMap(tmpUrlMap);

        this.config = config;
    }

    private void initIndex(Map<Long, StallUpBizType> tmpMap,
                           List<StallUpBizType> list,
                           List<StallUpBizType> tmpIndexList,
                           List<StallUpBizType> tmpQuickAccessList,
                           List<StallUpBizType> tmpDaosanjiaoSupportList,
                           List<StallUpBizType> tmpOAOServiceList
                           ) {
        list.stream().map(v -> {
            StallUpBizType stallUpBizType = mergeBizType(tmpMap, v);
            //业务办理
            if (stallUpBizType.getGroupId().equals(BizGroupEnum.BIZ_HANDLE.getGroup())) {
                tmpIndexList.add(v);
            } else if (stallUpBizType.getGroupId().equals(BizGroupEnum.QUICK_APPLICATION.getGroup())) {
                //其他类型
                tmpQuickAccessList.add(v);
            } else if (stallUpBizType.getGroupId().equals(BizGroupEnum.DAOSANJIAO_SUPPORT.getGroup())) {
                tmpDaosanjiaoSupportList.add(v);
            } else if (stallUpBizType.getGroupId().equals(BizGroupEnum.OAO_SERVICE.getGroup())) {
                tmpOAOServiceList.add(v);
            }
            return stallUpBizType;
        }).collect(Collectors.toList());
        Collections.sort(list);
    }

    private List<StallUpBizType> initListAndMap(Map<Long, StallUpBizType> tmpMap,
                                                List<StallUpBizType> list,
                                                Map<Long, StallUpBizType> map) {
        if(list == null){
            return new ArrayList<>();
        }
        list.stream().map(v -> {
            StallUpBizType stallUpBizType = mergeBizType(tmpMap, v);
            map.put(stallUpBizType.getId(), stallUpBizType);
            return stallUpBizType;
        }).collect(Collectors.toList());
        Collections.sort(list);
        return list;
    }

    private StallUpBizType convert2BizType(SmartGridBiz v) {
        StallUpBizType stallUpBizType = new StallUpBizType();
        stallUpBizType.setId(v.getId());
        stallUpBizType.setName(v.getName());
        stallUpBizType.setIcon(v.getIcon());
        stallUpBizType.setType(v.getType());
        stallUpBizType.setCategory(v.getCategory());
        stallUpBizType.setGroupId(v.getGroupId());
        String config = v.getConfig();

        if (StringUtils.hasText(config)) {
            ThirdHandlerTypeEnum thirdHandlerTypeEnum = ThirdHandlerTypeEnum.getByType(v.getType());
            Class clazz = thirdHandlerTypeEnum.getClazz();
            BizParams params = (BizParams) GsonUtils.fromGson2Obj(config, clazz);
            stallUpBizType.setBizParams(params);
        }
        stallUpBizType.setUrl(v.getUrl());
        return stallUpBizType;
    }

    private StallUpBizType mergeBizType(Map<Long, StallUpBizType> map,
                                        StallUpBizType v) {
        StallUpBizType stallUpBizType = map.get(v.getId());
        v.setName(stallUpBizType.getName());
        v.setType(stallUpBizType.getType());
        v.setIcon(stallUpBizType.getIcon());
        v.setBizParams(stallUpBizType.getBizParams());
        v.setUrl(stallUpBizType.getUrl());
        v.setCategory(stallUpBizType.getCategory());
        v.setGroupId(stallUpBizType.getGroupId());
        return v;
    }

    @Getter
    @Setter
    public static class ConfigDetail {

        /**
         * 打卡半径
         */
        private Integer radius;
        /**
         * 属于网格的角色id列表
         */
        private List<String> gridRoleIdList;
        /**
         * 码店配置
         */
        private MaDianConfig maDianConfig;
        /**
         * 短信预热配置
         */
        private SmsHotConfig smsHotConfig;
        /**
         * 倒三角配置
         */
        private DaoSanJiaoConfig daoSanJiaoConfig;

        /**
         * OAO上门服务配置
         */
        private OaoServiceConfig oaoServiceConfig;

        /**
         * 督导配置
         */
        private DuDaoConfig duDaoConfig;
        /**
         * 全景配置
         */
        private QuanJingConfig quanJingConfig;
        /**
         * 数据源
         *
         * @see StallUpBizType
         */
        private List<StallUpBizType> list;
        private Map<Long, StallUpBizType> map;
        /**
         * 业务办理
         */
        private Map<Long, StallUpBizType> bizMap;
        private List<StallUpBizType> bizList;
        /**
         * 营销工具
         */
        private Map<Long, StallUpBizType> marketMap;
        private List<StallUpBizType> marketList;
        /**
         * 首页业务办理
         */
        private Map<Long, StallUpBizType> indexMap;
        private List<StallUpBizType> indexList;
        /**
         * 首页快捷入口
         */
        private Map<Long, StallUpBizType> quickAccessMap;
        private List<StallUpBizType> quickAccessList;

        /**
         * 倒三角支撑配置
         */
        private Map<Long, StallUpBizType> daosanjiaoSupportBizMap;
        private List<StallUpBizType> daosanjiaoSupportBizList;

        /**
         * OAO上门服务
         */
        private Map<Long, StallUpBizType> oaoServiceMap;
        private List<StallUpBizType> oaoServiceList;

        /**
         * 扫楼
         */
        private Map<Long, StallUpBizType> sweepFloorMap;
        private List<StallUpBizType> sweepFloorList;
        /**
         * 扫楼业务
         */
        private Map<Long, StallUpBizType> sweepFloorBizMap;
        private List<StallUpBizType> sweepFloorBizList;

        /**
         * 扫村
         */
        private Map<Long, StallUpBizType> sweepVillageMap;
        private List<StallUpBizType> sweepVillageList;


        /**
         * 扫村业务
         */
        private Map<Long,StallUpBizType> sweepVillageBizMap;
        private List<StallUpBizType> sweepVillageBizList;


        /**
         * 集团服务日业务
         */
        private List<StallUpBizType> groupServiceDayToolList;
        private List<StallUpBizType> publicGroupServiceDayBizList;
        private List<StallUpBizType> publicGroupServiceDayBizDataList;


        /**
         * 扫街
         */
        private List<StallUpBizType> sweepStreetToolList;
        private List<StallUpBizType> sweepStreetBizList;
        private List<StallUpBizType> sweepStreetBizDataList;


        /**
         * url参数拼接map
         *
         * @see ThirdHandlerTypeEnum
         */
        private Map<Integer, UrlRedirectHandler> urlMap;
    }

    @Getter
    @Setter
    private class MaDianConfig {
        /**
         * 码店appId
         */
        private String appId;
        /**
         * 码店key
         */
        private String key;
        /**
         * 码店域名
         */
        private String domain;
        /**
         * 码店业务办理
         */
        private String adPath;
        /**
         * 码店大数据工具
         */
        private String bigDataPath;
        /**
         * 码店智慧查询
         */
        private String intelligentPath;
    }

    @Setter
    @Getter
    private class SmsHotConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 路径
         */
        private String path;
    }

    @Setter
    @Getter
    private class DaoSanJiaoConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 路径
         */
        private String path;
        /**
         * 双方约定内容
         */
        private Integer sid;
        private String seed;
    }

    @Setter
    @Getter
    private class OaoServiceConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 路径
         */
        private String path;

        private String seed;
    }


    @Setter
    @Getter
    private class DuDaoConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 路径
         */
        private String h5Path;
        private String queryPath;
        private String seed;
    }

    @Setter
    @Getter
    private class QuanJingConfig {
        /**
         * 域名
         */
        private String domain;
        /**
         * 路径
         */
        private String path;
        private String seed;
    }
}
