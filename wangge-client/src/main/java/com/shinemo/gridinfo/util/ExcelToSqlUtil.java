package com.shinemo.gridinfo.util;

import com.shinemo.client.util.GsonUtil;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * 类说明:
 *
 * @author 曾鹏
 */
public class ExcelToSqlUtil {

    public static void main(String[] args) {
        String baseSql = "insert into t_smart_grid_info(city_code,city_name,county_code,county_name,grid_id,grid_name) values";

        //1.读取csv文件
        try {
            BufferedReader reader = new BufferedReader(new FileReader("D:\\20200609.csv"));
            //第一行信息，为标题信息，不用，如果需要，注释掉
            reader.readLine();
            String line = null;
            while((line=reader.readLine())!=null){
                //CSV格式文件为逗号分隔符文件，这里根据逗号切分
                String[] item = line.split(",");

                String base = "(\"" + item[0] + "\",\""
                        + item[1] + "\",\""
                        + item[2] + "\",\""
                        + item[3] + "\",\""
                        + item[4] + "\",\""
                        + item[5] + "\"),";

                baseSql += base;

            }
            System.out.println(baseSql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
