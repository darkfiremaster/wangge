package com.shinemo.sweepvillage.domain.vo;

import lombok.Data;

import java.util.List;

@Data
public class HuaWeiAreaVO {
    private String provinceName;
    private String provinceCode;
    private List<City> cityList;
    @Data
    public static class City {
        private String cityName;
        private String cityCode;
        private List<Area> areaList;
    }

    @Data
    public static class Area {
        private String areaName;
        private String areaCode;
    }

}
