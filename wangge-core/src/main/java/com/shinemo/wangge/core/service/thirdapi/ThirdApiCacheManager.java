package com.shinemo.wangge.core.service.thirdapi;

import com.shinemo.common.annotation.SmIgnore;
import com.shinemo.thirdapi.domain.model.ThirdApiMappingDO;
import com.shinemo.thirdapi.domain.query.ThirdApiMappingQuery;
import com.shinemo.wangge.dal.mapper.ThirdApiMappingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author shangkaihui
 * @Date 2020/5/8 17:28
 * @Desc
 */
@Component
@Slf4j
public class ThirdApiCacheManager {

    public static final Map<String, ThirdApiMappingDO> THIRD_API_CACHE = new ConcurrentHashMap<>();

    @Resource
    private ThirdApiMappingMapper thirdApiMappingMapper;

    @PostConstruct
    public void init() {
        ThirdApiMappingQuery thirdApiMappingQuery = new ThirdApiMappingQuery();
        thirdApiMappingQuery.setPageEnable(false);
        List<ThirdApiMappingDO> thirdApiMappingDOS = thirdApiMappingMapper.find(thirdApiMappingQuery);

        Map<String, ThirdApiMappingDO> map = thirdApiMappingDOS.stream().collect(Collectors.toMap(ThirdApiMappingDO::getApiName, i -> i));
        THIRD_API_CACHE.putAll(map);

        log.info("初始化thirdapi完成");
    }

    @SmIgnore
    public void reload() {
        THIRD_API_CACHE.clear();

        ThirdApiMappingQuery thirdApiMappingQuery = new ThirdApiMappingQuery();
        thirdApiMappingQuery.setPageEnable(false);
        List<ThirdApiMappingDO> thirdApiMappingDOS = thirdApiMappingMapper.find(thirdApiMappingQuery);
        Map<String, ThirdApiMappingDO> map = thirdApiMappingDOS.stream().collect(Collectors.toMap(ThirdApiMappingDO::getApiName, i -> i));
        THIRD_API_CACHE.putAll(map);
        log.info("[reload] 刷新thirdapi缓存完成");
    }
}
