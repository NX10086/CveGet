package com.cve;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.DocFlavor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Hello world!
 *
 */
public class CVE
{


    private MyDialog dialog ;

    public void start(){
        dialog = new MyDialog();
        final  String  PATH =  System.getProperty("user.dir");

        final JFrame main = new JFrame("根据关键字搜索CVE");
        main.setSize(400,300);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        int x = (int)(toolkit.getScreenSize().getWidth()-main.getWidth())/2;

        int y = (int)(toolkit.getScreenSize().getHeight()-main.getHeight())/2;


        main.setLocation(x,y);
        main.setDefaultCloseOperation(main.EXIT_ON_CLOSE);


        JPanel panel = new JPanel();
        panel.setSize(400,300);
        panel.setLayout(null);

        JLabel label =new JLabel();
        label.setSize(80,40);
        label.setText("Keyword :");
        label.setFont(new Font("宋体",1,13));
        label.setLocation(30,80);

        final JTextField field = new JTextField();
        field.setSize(230,40);
        field.setFont(new Font("宋体",1,13));
        field.setLocation(120,80);

        JButton search = new JButton("Search");
        search.setSize(100,40);
        search.setFont(new Font("宋体",1,13));
        search.setLocation(80,150);

        final JButton mark = new JButton("Mark");
        mark.setSize(100,40);
        mark.setFont(new Font("宋体",1,13));
        mark.setLocation(210,150);
        JButton conMark =new JButton("Continue");
        conMark.setFont(new Font("宋体",1,13));
        conMark.setBounds(80,200,100,40);
        panel.add(label);
        panel.add(field);
        panel.add(search);
        panel.add(mark);
        panel.add(conMark);

        main.setLayout(null);
        main.add(panel);
        main.setResizable(false);
        main.setVisible( true);

        search.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        String NVD_API="https://services.nvd.nist.gov/rest/json/cves/1.0?";
                        String keyword=field.getText();                // 关键字查询
                        int startIndex =0;
                        int totalResults=0;
                        int step=50;
                        int endIndex;
                        int  resultsPerPage=step;
                        String cur_request_url= NVD_API+"keyword="+keyword
                                +"&resultsPerPage="+resultsPerPage
                                +"&startIndex="+startIndex;

                        try {
                            boolean f= FileUtil.del(PATH+"/lastSearchResult");
                            System.out.println(f);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        boolean append=false;
                        JOptionPane.showMessageDialog(null, "请等待搜索结果", "提示", JOptionPane.INFORMATION_MESSAGE);
                        mark.setEnabled(false);
                        do {
                            String str=get_json(cur_request_url);
                            System.out.println(str);
                            JSONObject object = JSONObject.parseObject(str);
                            totalResults = object.getInteger("totalResults");
                            try {
                                String format_json = JSON.toJSONString(object, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                                        SerializerFeature.WriteDateUseDateFormat);
                                if(startIndex+step<totalResults){
                                    endIndex=startIndex+step;
                                }else {
                                    endIndex=totalResults;
                                }
                                writeFile(PATH+"/lastSearchResult"+"/lastSearchResult-"+startIndex+"-"+(endIndex-1)+".json",format_json,append);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                            startIndex=startIndex+step;
                            cur_request_url=NVD_API+"keyword="+keyword
                                    +"&resultsPerPage="+resultsPerPage
                                    +"&startIndex="+startIndex;

                        }while (startIndex<totalResults);
                        JOptionPane.showMessageDialog(null, "搜索完成，请开始标记", "提示", JOptionPane.INFORMATION_MESSAGE);
                        mark.setEnabled(true);
                    }
                };
                thread.start();

            }
        });

        mark.addActionListener(new ActionListener() {

            JSONObject object=null;
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.firstMarkInitial(main,PATH+"/lastSearchResult",field.getText());
            }
        });
        conMark.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                LogInfo info = FileUtil.readLog(PATH+"/log.json");
                int allCurrentIndex=info.currentIndex+info.getPageCount()*info.getResultsPerPage();
                if(allCurrentIndex==info.getTotalResults()){//上次已经全部标记完，
                    JOptionPane.showMessageDialog(null, "上次以全部标记完成，点击search搜索新的关键字，或点击mark开始新的标记", "提示", JOptionPane.INFORMATION_MESSAGE);
                }else if(allCurrentIndex<info.getTotalResults()){//传入info
                    dialog.continueMarkInitial(main,info);
                }
            }
        });
    }
    public static void main( String[] args ) throws IOException {

       CVE cve = new CVE();
       cve.start();

    }

    public static void get_cve_json_list(List<Cvelink> cvelinks,String NVD_API,List<JSONObject> jsonObjects){
        for (Cvelink cve:cvelinks){
            String cve_detial_link=NVD_API+cve.getCve_id();
            System.out.println(cve_detial_link);
            JSONObject object = JSONObject.parseObject(get_json(cve_detial_link));
            JSONObject result = object.getJSONObject("result");
            JSONArray cve_items=result.getJSONArray("CVE_Items");
            JSONObject cve_json = JSONObject.parseObject(cve_items.get(0).toString());
            jsonObjects.add(cve_json);

            //System.out.println(cve_json.toJSONString());
        }
    }
    public static void output(List<JSONObject> jsonObjects,String keyword,String time,String path) throws IOException {
        JSONArray array = JSONObject.parseArray(jsonObjects.toString());
        JSONObject result =new JSONObject();
        result.put(keyword,array);
        String format_json = JSON.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);


        writeFile(path+"/"+keyword+time+".json",format_json,true);
    }
    public static String get_next_page_url(String cur_page_url,String absurl){
        String next_page_url="";
        Document document = null;
        try {
            document = Jsoup.connect(cur_page_url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = document.getElementsByClass("pagination");

        Elements links=elements.select("a[data-testid=pagination-link-page->]");
        Set<String> link =new HashSet<>();
        for (Element e:links){
            link.add(e.select("a").attr("href"));
        }
        Iterator<String> it = link.iterator();
        if (it.hasNext()){
            next_page_url=absurl+it.next();
        }else {
            next_page_url="";
        }

        return next_page_url;
    }

    public static void get_all_cve_link(List<Cvelink> cvelinks,String absurl,String cur_page_url){
        while (!cur_page_url.isEmpty()) {
            Document document = null;
            try {
                document = Jsoup.connect(cur_page_url)
                        .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0")
                        .get();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Elements elements = document.getElementsByClass("table table-striped table-hover");
            Elements tbody = elements.select("tbody");
            Elements tr = tbody.select("tr");
            for (Element e : tr) {

                Cvelink cvelink = new Cvelink();
                String cve_id=e.select("th").select("a").text();
                String cve_link=absurl+e.select("th").select("a").attr("href");
                String cve_summary=e.select("p").text();
                cvelink.setCve_id(cve_id);
                cvelink.setLink(cve_link);
                cvelink.setSummary(cve_summary);

                MyDialog dialog = new MyDialog();




            }
            cur_page_url=get_next_page_url(cur_page_url,absurl);
        }
    }
    public static String get_json(String url){
        String json_str="";
        Document document = null;
        try {
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; rv:30.0) Gecko/20100101 Firefox/30.0")
                    .ignoreContentType(true)
                    .get();

            String str=document.body().toString();

            str=str.replaceAll("<!--","");
            str=str.replaceAll("-->","");
            Document doc =Jsoup.parse(str);
            json_str=doc.body().text();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return json_str;
    }
    /**
     * 写文件到.Json
     * @param filePath
     * @param sets
     * @throws IOException
     */
    public static void writeFile(String filePath, String sets,boolean append)
            throws IOException {
        FileWriter fw = new FileWriter(filePath,append);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    /**
     * 读Json文件
     * @param path
     * @return
     */
    public static String ReadFile(String path) {
        File file = new File(path);
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

}

