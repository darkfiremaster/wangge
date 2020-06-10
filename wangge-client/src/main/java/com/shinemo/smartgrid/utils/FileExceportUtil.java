package com.shinemo.smartgrid.utils;

import com.shinemo.sweepfloor.domain.vo.SweepFloorActivityVO;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FileExceportUtil {
    private static String fileName = "test";
    private static final String defualtCharset = "GBK";
    private static final String newLine = "\r\n";

    /**
     * 传入类 、该类的结果集 、编码字符集 产生对应的流
     * @param clazz
     * @param list
     * @param charset
     * @return
     */



    public static InputStream exportInputStreamFromClass(Class clazz, List<?> list,
                                                         String charset,String delimiter) {
        String content = getExportStringFromClass(clazz, list,delimiter);
        return getInputStream(content, charset);
    }

    /**
     * 传入类、类的集合、编码字符集、导出路径 自动生成文件到指定路径
     * @param clazz
     * @param list
     * @param charset
     * @param exportUrl
     */
    public static void exportFileFromClass(Class clazz, List<?> list, String charset,
                                           String exportUrl,String delimiter) {
        String content = getExportStringFromClass(clazz, list,delimiter);
        export(content, charset, exportUrl);
    }

    /**
     * 传入linkedhashmap，编码字符集 产生对应的流文件
     * @param list
     * @param charset
     * @return
     */
    public static InputStream exportInputStreamFromMap(List<LinkedHashMap<Object, Object>> list,
                                                       String charset) {
        String content = getExportStringFromMap(list);
        return getInputStream(content, charset);
    }

    /**
     * 传入linkedhashmap、编码字符集、导出路径 自动生成文件到指定路径
     * @param list
     * @param charset
     * @param exportUrl
     */
    public static void exportFileFromMap(List<LinkedHashMap<Object, Object>> list, String charset,
                                         String exportUrl) {
        String content = getExportStringFromMap(list);
        export(content, charset, exportUrl);
    }

    public static void export(String content, String charset, String exportUrl) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(exportUrl);
            InputStream io = getInputStream(content, charset);
            int length = 1024;
            byte[] buffer = new byte[1024];
            while ((length = io.read(buffer)) > -1) {
                out.write(buffer, 0, length);
            }
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
		/*	List<TxtDemo> txtDemos = TxtDemo.generator();
//		getExportString(TxtDemo.class,txtDemos);
		TxtFileExceport.fileName = "billDownload"+new Date().getTime();
		String urlString = "D:/tmp/billDownload/" + fileName + ".txt";

		exportFile(TxtDemo.class, txtDemos, null, urlString);
		 List<LinkedHashMap<Object, Object>> lists=TxtDemo.generatorMap();
		 export(getExportStringFromMap(lists), null, urlString);*/
        String path = "D:/tmp/billDownload/";
        String urlString = "D:/tmp/billDownload/" + fileName + ".avl";
        File file = new File(path);
        file.mkdirs();
        File file02 = new File(urlString);
        file02.createNewFile();
        SweepFloorActivityVO vo = new SweepFloorActivityVO();
        vo.setStatus(1);
        vo.setCommunityName("小区名1");
        vo.setCommunityId("小区id1");
        vo.setAddress("小区1地址");

        SweepFloorActivityVO vo02 = new SweepFloorActivityVO();
        vo02.setStatus(1);
        vo02.setCommunityName("小区名2");
        vo02.setCommunityId("小区id2");
        vo02.setAddress("小区2地址");

        List<SweepFloorActivityVO> vos = new ArrayList<>();
        vos.add(vo);
        vos.add(vo02);

        exportFileFromClass(SweepFloorActivityVO.class,vos,null,urlString,",");

    }

    /**
     * 反射拼接类的属性值
     * @param clazz
     * @param list
     * @return
     */
    public static String getExportStringFromClass(Class clazz, List<?> list,String delimiter) {
        StringBuffer sb = new StringBuffer();
        for (Object object : list) {
            Field[] fs = clazz.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field field = fs[i];
                field.setAccessible(true);
                String valString;
                try {

                    Object o = field.get(object);
                    if (o == null) {
                        valString = "";
                    }else {
                        valString = o.toString();
                    }
                    if (i == (fs.length - 1)) {
                        sb.append(valString + newLine);
                    } else {
                        sb.append(valString + delimiter);
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    /**遍历 拼接value值
     * @param list
     * @return
     */
    public static String getExportStringFromMap(List<LinkedHashMap<Object,Object>> list) {
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<list.size();i++){
            LinkedHashMap<Object,Object> linkMap=list.get(i);
            int index=0;
            for(Object obj:linkMap.values()){
                if(obj==null){
                    if(index==linkMap.values().size()-1){
                        sb.append(""+newLine);
                    }else{
                        sb.append(",");
                    }
                }else{
                    if(index==linkMap.values().size()-1){
                        sb.append(obj.toString()+newLine);
                    }else{
                        sb.append(obj.toString()+",");
                    }
                }
                index++;
            }
        }
        return sb.toString();
    }


    public static InputStream getInputStream(String string, String charset) {
        byte[] bytes = null;
        try {
            if (StringUtils.isEmpty(charset)) {
                bytes = string.getBytes(defualtCharset);
            } else {
                bytes = string.getBytes(charset);
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new ByteArrayInputStream(bytes);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
