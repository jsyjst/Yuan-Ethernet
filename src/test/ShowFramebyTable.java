package test;


import dialog.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/28
 *     desc   :
 * </pre>
 */
public class ShowFramebyTable extends JFrame {
    private int mWidth, mHeight;  //窗口大小
    private JMenu mHelpMenu; //一级菜单
    private JMenuItem mAboutItem; //Help菜单项
    private AboutDialog mAboutDialog; //about的弹窗
    private JButton sendBtn; // 发送按钮
    private JButton clearBtn; // 清空按钮
    private JButton updateBtn;//修改主机数按钮
    private JButton timeLaterBtn; //时延按钮
    private JPanel northPanel, southPanel;
    private JPanel pcPanel, timePanel; //顶部的区域
    private JScrollPane scrollPane;//滚动区域
    private JTextArea textArea;
    private JTextField pcNumText; //主机文本框
    private JTextField pcTimeLaterText; //时延框
    private volatile long bus = 0;//总线
    private int pcNum = 2;//主机数
    private long maxTime = 2;//随机时延的最大值
    private static final double COLLISION_WINDOW = 0.005;

    private ArrayList<Thread> threads;  //线程数
    private ArrayList<Runnable> runnables;
    private ArrayList<Character> names; //主机集合

    private Vector<String> vName;
    private Vector vData;
    private Vector vRow;
    private DefaultTableModel tableModel;
    private JTable pcTable;

    public ShowFramebyTable() {
        setFrameSize();//设置适合电脑屏幕的大小
        initView(); // 初始化组件
        onClick(); //点击事件
    }

    private void initTable() {
        vData = new Vector<>();
        vName = new Vector<>();
        vRow = new Vector();
        vName.add("主机名");
        vName.add("主机号");
        vName.add("发送结果");
        vName.add("发送成功数");
        vName.add("重发次数");
        tableModel = new DefaultTableModel(vData, vName);
        pcTable = new JTable();
        pcTable.setModel(tableModel);
    }

    private void initView() {
        //关于我们标题栏
        mHelpMenu = new JMenu("Help");
        mAboutItem = new JMenuItem("About");
        mAboutDialog = new AboutDialog(this);
        mHelpMenu.add(mAboutItem);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(mHelpMenu);

        //初始化主机所需要的集合
        names = new ArrayList<>();
        runnables = new ArrayList<>();
        threads = new ArrayList<>();


        //顶部区域
        northPanel = new JPanel();
        pcPanel = new JPanel();
        timePanel = new JPanel();
        pcNumText = new JTextField("2", 6);
        pcTimeLaterText = new JTextField("0~1", 15);
        updateBtn = new JButton("修改");
        timeLaterBtn = new JButton("修改");
        pcNumText.setEditable(false); //不可点击
        pcTimeLaterText.setEditable(false);
        northPanel.setLayout(new GridLayout(1, 2));
        //添加到主机区域
        pcPanel.add(new JLabel("设置主机数：", SwingConstants.RIGHT));
        pcPanel.add(pcNumText);
        pcPanel.add(updateBtn);
        //添加到时延区域
        timePanel.add(new JLabel("设置时延范围(ms)：", SwingConstants.RIGHT));
        timePanel.add(pcTimeLaterText);
        timePanel.add(timeLaterBtn);
        //将主机区域和时延区域添加到顶部区域
        northPanel.add(pcPanel);
        northPanel.add(timePanel);
        add(northPanel, BorderLayout.NORTH);


        //中部区域
//        textArea = new JTextArea(5, 10);
//        textArea.setEnabled(false);  //不能点击
//        textArea.setDisabledTextColor(Color.BLACK);//颜色设置
        initTable();
        scrollPane = new JScrollPane(pcTable);//将文本框添加到滑动面板
        add(scrollPane, BorderLayout.CENTER);

        //底部区域
        southPanel = new JPanel();
        sendBtn = new JButton("发送");
        clearBtn = new JButton("清空");
        southPanel.add(sendBtn);  //将按钮添加到底部面板中
        southPanel.add(clearBtn);
        add(southPanel, BorderLayout.SOUTH);


    }

    private void onClick() {
        //关于我们点击效果
        mAboutItem.addActionListener(e -> {
            if (mAboutDialog == null) {
                mAboutDialog = new AboutDialog(this);
            }
            mAboutDialog.setSize(mWidth / 2, mHeight / 2);
            mAboutDialog.setLocation(mWidth * 3 / 4, mHeight * 3 / 4);
            mAboutDialog.setVisible(true);
        });

        //修改主机数按钮
        updateBtn.addActionListener(actionEvent -> {
            String pcNumString = JOptionPane.showInputDialog("请输入主机数");
            if (pcNumString != null) {
                try {
                    pcNum = Integer.valueOf(pcNumString);
                    if (pcNum < 2) {
                        JOptionPane.showMessageDialog(this, "主机数不能小于1",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else if (pcNum > 26) {
                        JOptionPane.showMessageDialog(this, "主机数最多为26",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        pcNumText.setText(pcNumString);  //主机数文本框设置
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "请输入数字",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //修改主机数按钮
        timeLaterBtn.addActionListener(actionEvent -> {
            String timeLaterString = JOptionPane.showInputDialog("请输入随机时延的最大值");
            if (timeLaterString != null) {
                try {
                    maxTime = Integer.valueOf(timeLaterString);
                    if (maxTime == 0) {
                        JOptionPane.showMessageDialog(this, "时延最大值必须大于1",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else if (maxTime > 100000) {
                        JOptionPane.showMessageDialog(this, "时延最大值为100s",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        pcTimeLaterText.setText("0~" + timeLaterString);  //主机数文本框设置
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "请输入数字",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //发送按钮
        sendBtn.addActionListener(actionEvent -> {
//            if(!textArea.getText().equals("")){
//                JOptionPane.showMessageDialog(this, "请先清空数据",
//                        "错误", JOptionPane.ERROR_MESSAGE);
//            }else {
            for (int i = 0; i < pcNum; i++) {
                names.add((char) ('A' + i));
            }
            for (int i = 0; i < pcNum; i++) {
                PcRunnable pcRun = new PcRunnable();
                runnables.add(pcRun);
                threads.add(new Thread(pcRun, String.valueOf(names.get(i))));
                pcRun.setId(threads.get(i).getId()).setPcName(threads.get(i).getName()).setDelay((long) (Math.random() * maxTime));
            }
            for (int i = 0; i < pcNum; i++) {
                threads.get(i).start();
            }
            //}


        });
        //清空按钮
        clearBtn.addActionListener(actionEvent -> {
            if (textArea.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "数据已为空",
                        "提示", JOptionPane.ERROR_MESSAGE);
            } else {
                textArea.setText("");
                threads.clear();
                runnables.clear();
                names.clear();
            }

        });
    }

    /**
     * 适配屏幕的方法
     */
    private void setFrameSize() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
        mWidth = screenWidth / 2;
        mHeight = screenHeight / 2;
        setSize(screenWidth / 2, screenHeight / 2);
        setLocation(screenWidth / 4, screenHeight / 4);
    }

    class PcRunnable implements Runnable {
        private int successCount = 0;  //成功次数
        private int repeatCount = 0;  //重传次数
        private long mId;  //线程号
        private String name; //线程名
        private long delay;

        public PcRunnable setId(long mId) {
            this.mId = mId;
            return this;
        }

        public PcRunnable setPcName(String name) {
            this.name = name;
            return this;
        }

        public void setDelay(long delay) {
            this.delay = delay;
        }

        @Override
        public synchronized void run() {
            while (true) {

                //在释放总线前加入随机时延，模拟冲突
                if (bus == 0) {
                        bus = bus | mId; //模拟发送数据
                        try {
                            Thread.sleep((delay));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (bus == mId) {//数据发送成功
                            successCount++;  //成功次数加1
                            vRow = new Vector();
                            vRow.add(name);
                            vRow.add(mId);
                            vRow.add("发送成功");
                            vRow.add(successCount);
                            vRow.add(repeatCount);
                            vData.add(vRow);
                            tableModel.setDataVector(vData, vName);
                            bus = 0;
                            repeatCount = 0;  //重传次数重置为0
                            if (successCount >= 10) break;  //成功发送10次，线程结束
                        } else {//产生冲突
                            vRow = new Vector();
                            vRow.add(name);
                            vRow.add(mId);
                            vRow.add("产生冲突");
                            vRow.add(successCount);
                            vRow.add(repeatCount);
                            vData.add(vRow);
                            tableModel.setDataVector(vData, vName);
                            bus = 0;
                            repeatCount++;  //重传次数加1
                            if (repeatCount <= 16) {  //使用截至二进制指数退避算法重发数据
                                try {
                                    Thread.sleep((long) backOff(repeatCount));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } else {//重传达16次仍然不成功
                                vRow = new Vector();
                                vRow.add(name);
                                vRow.add(mId);
                                vRow.add("发送失败");
                                vRow.add(successCount);
                                vRow.add(repeatCount);
                                vData.add(vRow);
                                tableModel.setDataVector(vData, vName);
                                break;
                            }
                        }
                    }
                }
        }

    }


    //截断二进制指数退避算法
    private double backOff(int count) {
        int k = Math.min(count, 10);
        return Math.random() * (Math.pow(2, k) - 1) * COLLISION_WINDOW;
    }
}
