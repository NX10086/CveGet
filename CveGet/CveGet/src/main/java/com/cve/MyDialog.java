package com.cve;







        import java.awt.*;

        import java.awt.event.ActionEvent;

        import java.awt.event.ActionListener;

        import java.util.concurrent.Executors;

        import java.util.concurrent.ScheduledExecutorService;

        import java.util.concurrent.TimeUnit;


        import javax.swing.*;


public class MyDialog {

    private String message = null;


    private JTextArea textArea;

    private JButton confirm,cancel;

    private JDialog dialog = null;

    int result = -5;

    public  int  showDialog(JFrame father, String message,String title) {



        this.message = message;

        textArea=new JTextArea();
        textArea.setText(message);
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


        confirm = new JButton("接受");

        confirm.setBounds(50,450,80,40);

        confirm.addActionListener(new ActionListener() {

            @Override

            public void actionPerformed(ActionEvent e) {

                result = 1;

                MyDialog.this.dialog.dispose();

            }

        });

        cancel = new JButton("拒绝");

        cancel.setBounds(260,450,80,40);

        cancel.addActionListener(new ActionListener() {



            @Override

            public void actionPerformed(ActionEvent e) {

                result = -1;

                MyDialog.this.dialog.dispose();

            }

        });

        dialog = new JDialog(father, true);

        dialog.setTitle(title);

        dialog.setLayout(null);

        dialog.add(scrollPane);

        dialog.add(confirm);

        dialog.add(cancel);



        dialog.pack();

        dialog.setSize(new Dimension(420,600));

        dialog.setLocationRelativeTo(father);

        dialog.setVisible(true);

        return result;

    }
    public static void main(String args[]){
        MyDialog dialog = new MyDialog();
        int result = dialog.showDialog(null,"??????","是否接受");
        System.out.println(result);
    }

}
