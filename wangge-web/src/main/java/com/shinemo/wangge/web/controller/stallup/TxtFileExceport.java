package com.shinemo.wangge.web.controller.stallup;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

public class TxtFileExceport {
    private static String fileName = null;
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
                                                         String charset) {
        String content = getExportStringFromClass(clazz, list);
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
                                           String exportUrl) {
        String content = getExportStringFromClass(clazz, list);
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

    public static void main(String[] args) {
		/*	List<TxtDemo> txtDemos = TxtDemo.generator();
//		getExportString(TxtDemo.class,txtDemos);
		TxtFileExceport.fileName = "billDownload"+new Date().getTime();
		String urlString = "D:/tmp/billDownload/" + fileName + ".txt";

		exportFile(TxtDemo.class, txtDemos, null, urlString);
		 List<LinkedHashMap<Object, Object>> lists=TxtDemo.generatorMap();
		 export(getExportStringFromMap(lists), null, urlString);*/
    }

    /**
     * 反射拼接类的属性值
     * @param clazz
     * @param list
     * @return
     */
    public static String getExportStringFromClass(Class clazz, List<?> list) {
        StringBuffer sb = new StringBuffer();
        for (Object object : list) {
            Field[] fs = clazz.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                Field field = fs[i];
                field.setAccessible(true);
                String valString;
                try {
                    valString = field.get(object).toString();
                    if (i == (fs.length - 1)) {
                        sb.append(valString + newLine);
                    } else {
                        sb.append(valString + ",");
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
