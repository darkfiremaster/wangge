package com.shinemo.groupserviceday.domain.huawei;

import lombok.Data;

import java.util.List;

/**
 * @Author shangkaihui
 * @Date 2020/8/11 11:16
 * @Desc
 */
@Data
public class HuaWeiCreateGroupServiceDayRequest {

    private String parentActivityId;

    private String title;

    private String startTime;

    private String endTime;

    private String status;

    private String groupId;

    private List<ChildGroupServiceDay> childrenList;


    @Data
    public static class ChildGroupServiceDay{

        private String activityId;

        private List<Participant> participantList;

        @Data
        public static class Participant {

            private String userSource;

            private String userId;

            private String userName;

            private String userPhone;

            private String userType;

        }
    }

}
