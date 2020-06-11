package com.shinemo.wangge.test.web;

import com.shinemo.stallup.domain.utils.SubTableUtils;
import org.junit.Test;

import java.time.LocalDate;

/**
 * @Author shangkaihui
 * @Date 2020/6/11 10:25
 * @Desc
 */
public class StaticTest {

    @Test
    public void testSubTable() {
        String tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 7, 1).minusDays(1));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);

        tableIndexByMonth = SubTableUtils.getTableIndexByOnlyMonth(LocalDate.of(2020, 6, 29).plusDays(2));
        System.out.println("tableIndexByMonth = " + tableIndexByMonth);
    }


}
