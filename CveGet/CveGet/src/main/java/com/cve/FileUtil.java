package com.cve;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.cve.CVE.writeFile;

public class FileUtil {
    /**
     *
     * @param filepath
     * @throws IOException
     */
    public static boolean del(String filepath) throws IOException {
        File f = new File(filepath);// 定义文件路径
        boolean r=false;
        if (f.isFile() ) {// 判断是文件还是目录
            r=f.delete();
        }else if(f.isDirectory()){
            if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
               r=true;
            } else {// 若有则把文件放进数组，并判断是否有下级目录
                File delFile[] = f.listFiles();
                int i = f.listFiles().length;

                for (int j = 0; j < i; j++) {
                    if (delFile[j].isDirectory()) {
                        del(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                    }
                    r=delFile[j].delete();// 删除文件
                }
                r=true;
            }
        }
        return r;
    }
    public static String ReadJson(File file) {

        BufferedReader reader = null;
        String laststr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr = laststr + tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }

    public static void  writeLog(LogInfo info,String path){
        JSONObject log = new JSONObject();
        log.put("currentIndex",info.getCurrentIndex());
        log.put("curFileName",info.getCurFileName());
        log.put("keyword",info.getKeyword());
        log.put("resultsPerPage",info.getResultsPerPage());
        log.put("startIndex",info.getStartIndex());
        log.put("totalResults",info.getTotalResults());
        log.put("pageCount",info.getPageCount());
        JSONObject result = new JSONObject();
        result.put("log",log);
        System.out.println(result.toJSONString());
        String format_json = JSON.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        try {
            File file = new File(path);
            System.out.println(file.getName());
             file.delete();
            writeFile(path,format_json,true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static LogInfo readLog(String path){
        File file =new File(path);
        String str= FileUtil.ReadJson(file);
        JSONObject object = JSONObject.parseObject(str);
        JSONObject log = object.getJSONObject("log");
        System.out.println(log.toJSONString());
        LogInfo info= new LogInfo();
        info.setCurrentIndex(log.getInteger("currentIndex"));
        info.setCurFileName(log.getString("curFileName"));
        info.setKeyword(log.getString("keyword"));
        info.setResultsPerPage(log.getInteger("resultsPerPage"));
        info.setStartIndex(log.getInteger("startIndex"));
        info.setTotalResults(log.getInteger("totalResults"));
        info.setPageCount(log.getInteger("pageCount"));
        return info;
    }

}
