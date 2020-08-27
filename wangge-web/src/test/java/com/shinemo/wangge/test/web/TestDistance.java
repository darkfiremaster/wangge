package com.shinemo.wangge.test.web;

import com.shinemo.cmmc.report.client.wrapper.ApiResultWrapper;
import com.shinemo.common.tools.result.ApiResult;
import com.shinemo.stallup.domain.utils.DistanceUtils;
import com.shinemo.sweepfloor.common.error.SweepFloorErrorCodes;

public class TestDistance {
    public static void main(String[] args) {
        double v = checkDistanceWhenSign("120.06358868042383,30.284240201565034", "120.06380009625356,30.28409015395937");
        System.out.println(v);
    }

    private static double checkDistanceWhenSign(String dbLocation, String reqLocation) {
        String[] dbSplit = dbLocation.split(",");
        String[] reqSplit = reqLocation.split(",");
        double Lat1 = Double.parseDouble(dbSplit[1]);
        double Lon1 = Double.parseDouble(dbSplit[0]);
        double Lat2 = Double.parseDouble(reqSplit[1]);
        double Lon2 = Double.parseDouble(reqSplit[0]);
        double distance = DistanceUtils.getDistanceFromCoordinates(Lat1, Lon1, Lat2, Lon2);

        return distance;
    }

}
