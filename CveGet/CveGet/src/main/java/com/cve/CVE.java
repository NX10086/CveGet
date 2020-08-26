package com.cve;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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





    public static void main( String[] args ) throws IOException {


        JFrame main = new JFrame("根据关键字搜索CVE");
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
        label.setText("key word :");
        label.setFont(new Font("宋体",1,13));
        label.setLocation(30,80);

        final JTextField field = new JTextField();
        field.setSize(230,40);
        field.setFont(new Font("宋体",1,13));
        field.setLocation(120,80);

        JButton button = new JButton("Search");
        button.setSize(100,40);
        button.setFont(new Font("宋体",1,13));
        button.setLocation(140,150);

        panel.add(label);
        panel.add(field);
        panel.add(button);
        main.setLayout(null);
        main.add(panel);
        main.setResizable(false);
        main.setVisible( true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String NVD_API="https://services.nvd.nist.gov/rest/json/cve/1.0/";
                String absurl="https://nvd.nist.gov";
                String form_type="Basic";       // 1.Basic     2.Advanced
                String results_type="overview"; // 1.overview  2. Statistics
                String query=field.getText();                // 关键字查询
                String queryType="";            //如需完全匹配，令为&queryType=phrase
                String search_type="all";       //

                String cur_page_url= "https://nvd.nist.gov/vuln/search/results?form_type="+form_type+

                        "&results_type="+results_type+
                        "&query="+query+queryType+
                        "&search_type="+search_type;
                System.out.println(cur_page_url);
                List<Cvelink> cvelinks=new ArrayList<>();
                get_all_cve_link(cvelinks,absurl,cur_page_url);//取得全部搜索结果CVE_ID

                List<JSONObject>  jsonObjects=new ArrayList<>();
                get_cve_json_list(cvelinks,NVD_API,jsonObjects);//根据CVE_ID取得全部CVE_JSON列表
                System.out.println(jsonObjects.toString());
                try {
                    String  path =  System.getProperty("user.dir");
                    output(jsonObjects,query,path);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });



    }
    public static void get_cve_json_list(List<Cvelink> cvelinks,String NVD_API,List<JSONObject> jsonObjects){
        for (Cvelink cve:cvelinks){
            String cve_detial_link=NVD_API+cve.getCve_id();
            JSONObject object = JSONObject.parseObject(get_json(cve_detial_link));
            JSONObject result = object.getJSONObject("result");
            JSONArray cve_items=result.getJSONArray("CVE_Items");
            JSONObject cve_json = JSONObject.parseObject(cve_items.get(0).toString());
            jsonObjects.add(cve_json);

            //System.out.println(cve_json.toJSONString());
        }
    }
    public static void output(List<JSONObject> jsonObjects,String project_name,String path) throws IOException {
        JSONArray array = JSONObject.parseArray(jsonObjects.toString());
        JSONObject result =new JSONObject();
        result.put(project_name,array);
        String format_json = JSON.toJSONString(result, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);


        writeFile(path+"/"+project_name+".json",format_json);
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
                int res=dialog.showDialog(null,cve_summary,"是否接受"+cve_id+"?");
                if(res==1){

                    cvelinks.add(cvelink); //点击“是”后留下该cve
                    System.out.println("接受");
                }else{
                       //点击“否”后过滤该cve
                    System.out.println("拒绝");
                }



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
            json_str=document.body().text();
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
    public static void writeFile(String filePath, String sets)
            throws IOException {
        FileWriter fw = new FileWriter(filePath);
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
    public static Object ReadFile(String path) {
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
        return JSON.parse(laststr);
    }

}

