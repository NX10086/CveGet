package com.cve;







        import com.alibaba.fastjson.JSONArray;
        import com.alibaba.fastjson.JSONObject;

        import java.awt.*;

        import java.awt.event.ActionEvent;

        import java.awt.event.ActionListener;

        import java.awt.event.WindowEvent;
        import java.awt.event.WindowListener;
        import java.io.File;
        import java.io.IOException;
        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;
        import java.util.concurrent.Executors;

        import java.util.concurrent.ScheduledExecutorService;

        import java.util.concurrent.TimeUnit;


        import javax.swing.*;

        import static com.cve.FileUtil.ReadJson;


public class MyDialog extends JFrame implements WindowListener {
    final  String  PATH =  System.getProperty("user.dir");
    private JSONObject firstJsonObject = null;


    private JTextArea textArea;

    private JRadioButton confirm,cancel;

    private JButton next,stop;

    private JFrame dialog;

    private boolean state = false;
    private int startIndex;
    private int totalResults;
    private int resultsPerPage;
    private List<JSONObject> saveSet;
    private int currentIndex;
    private int pageCount;
    private String keyword;
    private JSONArray CVE_Items;
    private String curFileName;

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        int option = JOptionPane.showConfirmDialog(this, "确定退出标记?", "提示",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
        {
            try {
                System.out.println("---------here-------");
                saveCurrent();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            this.dispose();
        }
        else if(option == JOptionPane.NO_OPTION){

                System.out.println("---------cancel-------");


        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    private  class Info{
        String value;
        String id;
        public Info (String value,String id){
            this.id=id;
            this.value=value;
        }
    }

    public  Info   getInfoByIndexFromJsonArray(JSONArray array ,int index){
        JSONArray CVE_Items = array;
        JSONObject CVE_Item = JSONObject.parseObject(CVE_Items.get(index).toString());
        JSONObject cve = CVE_Item.getJSONObject("cve");
        JSONObject description = cve.getJSONObject("description");
        JSONObject description_data=JSONObject.parseObject(description.getJSONArray("description_data").get(0).toString());
        JSONObject CVE_data_meta = cve.getJSONObject("CVE_data_meta");
        String value=description_data.getString("value");
        String ID = CVE_data_meta.getString("ID");
        Info info = new Info(value,ID);
        return info;
    }
    public  void firstMarkInitial(JFrame father,String path,String keyword){//这里需要该

        File file = new File(path);
        File f1 = file.listFiles()[0];
        this.curFileName=f1.getName();
        String Jsonstr=ReadJson(f1);
        JSONObject jsonObject= JSONObject.parseObject(Jsonstr);

        this.firstJsonObject = jsonObject;

        this.startIndex = firstJsonObject.getInteger("startIndex");
        this.currentIndex =startIndex;
        this.pageCount=0;
        this.totalResults = firstJsonObject.getInteger("totalResults");
        this.resultsPerPage = firstJsonObject.getInteger("resultsPerPage");
        this.keyword=keyword;
        this.saveSet=new ArrayList<JSONObject>();
        JSONObject result = firstJsonObject.getJSONObject("result");
        this.CVE_Items = result.getJSONArray("CVE_Items");
        Info info = getInfoByIndexFromJsonArray(CVE_Items,startIndex);
        showDialog(father,info);
    }
    public void continueMarkInitial(JFrame father,LogInfo log){
        this.startIndex=log.startIndex;
        this.pageCount=log.pageCount;
        this.currentIndex=log.currentIndex;
        this.resultsPerPage=log.resultsPerPage;
        this.totalResults=log.totalResults;
        this.keyword=log.keyword;
        String filename=log.curFileName;
        saveSet=new ArrayList<JSONObject>();
        File file =new File(PATH+"/lastSearchResult/"+filename);
        this.curFileName=file.getName();
        String str= ReadJson(file);
        JSONObject result = JSONObject.parseObject(str).getJSONObject("result");
        CVE_Items = result.getJSONArray("CVE_Items");
        Info info = getInfoByIndexFromJsonArray(CVE_Items,currentIndex);
        showDialog(father,info);
    }
    public  void   showDialog(JFrame father, final Info info) {
        dialog = new JFrame();
        textArea=new JTextArea();
        textArea.setText(info.value);
        textArea.setFont(new Font("宋体",Font.BOLD,16));
        textArea.setBounds(0,0,400,300);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(0,0,400,300);
        scrollPane.setViewportView(textArea);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);


        confirm = new JRadioButton("save");
        confirm.setBounds(50,300,100,40);
        confirm.setFont(new Font("宋体",Font.BOLD,18));
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state=true;
            }
        });

        cancel = new JRadioButton("delete");
        cancel.setBounds(260,300,100,40);
        cancel.setFont(new Font("宋体",Font.BOLD,18));
        cancel.setSelected(true);
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                state= false;
            }
        });


        final ButtonGroup group =new ButtonGroup();
        group.add(confirm);
        group.add(cancel);

        next = new JButton("next");
        next.setFont(new Font("宋体",Font.BOLD,18));
        next.setBounds(260,450,100,40);
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (state){
                   saveSet.add(JSONObject.parseObject(CVE_Items.get(currentIndex).toString()));
               }
               currentIndex=currentIndex+1;
               if (currentIndex+resultsPerPage*pageCount<totalResults&&currentIndex<startIndex+resultsPerPage){
                   Info info1 = getInfoByIndexFromJsonArray(CVE_Items,currentIndex);
                   textArea.setText(info1.value);
                   dialog.setTitle(info1.id);
               }else {

                   int allCurrentIndex=currentIndex+pageCount*resultsPerPage;
                   int endIndex=0;
                   if (allCurrentIndex<totalResults){
                       JOptionPane.showMessageDialog(null, "请等待读取。。。", "提示", JOptionPane.INFORMATION_MESSAGE);
                       next.setEnabled(false);

                       if(allCurrentIndex+resultsPerPage<totalResults){
                           endIndex=allCurrentIndex+resultsPerPage-1;
                       }else {
                           endIndex=totalResults-1;
                       }
                       File file = new File(PATH+"/lastSearchResult"+"/lastSearchResult-"+allCurrentIndex+"-"+endIndex+".json");
                       curFileName=file.getName();
                       String str= ReadJson(file);
                       System.out.println(str);
                       JSONObject object = JSONObject.parseObject(str);
                       JSONObject result = object.getJSONObject("result");
                       CVE_Items = result.getJSONArray("CVE_Items");
                       currentIndex=0;
                       pageCount=pageCount+1;
                       JOptionPane.showMessageDialog(null, "读取完成，请继续", "提示", JOptionPane.INFORMATION_MESSAGE);
                       Info info1 = getInfoByIndexFromJsonArray(CVE_Items,currentIndex);
                       textArea.setText(info1.value);
                       dialog.setTitle(info1.id);
                       next.setEnabled(true);
                   }else {
                       try {
                           saveCurrent();
                           JOptionPane.showMessageDialog(null, "全部标记完成", "提示", JOptionPane.INFORMATION_MESSAGE);
                           dialog.setVisible(false);
                       } catch (Exception ex) {
                           ex.printStackTrace();
                       }
                   }
               }
            }
        });
        stop = new JButton("stop");
        stop.setFont(new Font("宋体",Font.BOLD,18));
        stop.setBounds(50,450,100,40);

        stop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    try {
                        saveCurrent();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(null, "标记过的已保存,可以点击continue继续，或开始新的标记", "提示", JOptionPane.INFORMATION_MESSAGE);
                    dialog.setVisible(false);
            }
        });

        dialog.setTitle(info.id);

        dialog.setLayout(null);

        dialog.add(scrollPane);

        dialog.add(confirm);

        dialog.add(cancel);

        dialog.add(next);

        dialog.add(stop);

        dialog.addWindowListener(this);


        dialog.pack();

        dialog.setSize(new Dimension(420,600));

        dialog.setLocationRelativeTo(father);

        dialog.setVisible(true);



    }
    public void saveCurrent()throws Exception{
        CVE.output(saveSet,keyword,System.currentTimeMillis()+"",PATH+"/output");
        LogInfo logInfo = new LogInfo();
        logInfo.setPageCount(pageCount);
        logInfo.setResultsPerPage(resultsPerPage);
        logInfo.setTotalResults(totalResults);
        logInfo.setStartIndex(startIndex);
        logInfo.setCurrentIndex(currentIndex);
        logInfo.setKeyword(keyword);
        logInfo.setCurFileName(curFileName);
        FileUtil.writeLog(logInfo,PATH+"/log.json");

    }
    public static void main(String args[]){

    }

}
