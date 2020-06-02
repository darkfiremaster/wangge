package com.shinemo.stallup.domain.huawei;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/4/26 16:15
 * @Desc
 */
@Setter
@Getter
@ToString
public class SearchMapResult {
    /**
     * code : 200
     * message : success
     * data : [{"cellId":"XQID-BC32","cellName":"广西医科大学","cellAddr":"南宁青秀区青秀区城区双拥路22号","centerpoint":"108.1,22.8"},{"cellId":"XQID-1A1","cellName":"广西经贸职业技术学院","cellAddr":"南宁青秀区青秀区城区青山路14号","centerpoint":"108.2,22.9"}]
     */
    private Integer code;
    private String message;
    private List<Cell> data;

    @Data
    public static class Cell {
        /**
         * cellId : XQID-BC32
         * cellName : 广西医科大学
         * cellAddr : 南宁青秀区青秀区城区双拥路22号
         * centerpoint : 108.1,22.8
         */
        private String cellId;
        private String cellName;
        private String cellAddr;
        private String centerPoint;

    }
}
