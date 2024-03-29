package frame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import dialog.*;

/**
 * <pre>
 *     author : 谢雯琦
 *     time   : 2019/06/28
 *     desc   : 主要界面显示类
 * </pre>
 */
public class ShowFrame extends JFrame {
    private int mWidth, mHeight;  //窗口大小
    private JMenuItem mAboutItem; //关于菜单项
    private JMenuItem mHelpItem; //帮助菜单项
    private AboutDialog mAboutDialog; //about的弹窗
    private HelpDialog mHelpDialog; //help的弹窗
    private JButton mSendBtn; // 发送按钮
    private JButton mClearBtn; // 清空按钮
    private JButton mUpdateBtn;//修改主机数按钮
    private JButton mTimeLaterBtn; //时延按钮
    private JTextArea textArea;    //主机发送结果显示文本框
    private JTextField mPcNumText; //主机文本框
    private JTextField mPcTimeLaterText; //时延框
    /**
     * volatile声明该变量为易变对象,这样的话每个线程就不会创建bus副本到自己的栈中,
     * 而是直接操作主存中的bus。
     */
    private  volatile long bus = 0;//总线
    private int pcNum = 2;//主机数,默认为2个
    private long maxTime = 0;//随机时延的最大值，默认最大为0ms
    private static final double COLLISION_WINDOW = 0.005;  //冲突窗口

    private ArrayList<Thread> threads;  //线程数
    private ArrayList<Character> names; //主机集合


    public ShowFrame() {
        setFrameSize();//设置适合电脑屏幕的大小
        initView(); // 初始化组件
        onClick(); //点击事件
    }

    private void initView() {
        //Help标题栏
        JMenu helpMenu = new JMenu("Help");
        mHelpItem = new JMenuItem("Help");
        mAboutItem = new JMenuItem("About");
        mAboutItem.setMnemonic('A');
        mHelpItem.setMnemonic('H');
        mAboutDialog = new AboutDialog(this);
        mHelpDialog = new HelpDialog(this);
        helpMenu.add(mHelpItem);
        helpMenu.add(mAboutItem);
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        menuBar.add(helpMenu);

        //初始化主机所需要的集合
        names = new ArrayList<>();
        threads = new ArrayList<>();

        //顶部区域
        JPanel northPanel = new JPanel();
        JPanel pcPanel = new JPanel();
        JPanel timePanel = new JPanel();
        mPcNumText = new JTextField("2", 6);
        mPcTimeLaterText = new JTextField("0", 10);
        mUpdateBtn = new JButton("修改");
        mTimeLaterBtn = new JButton("修改");
        mPcNumText.setEditable(false); //不可点击
        mPcTimeLaterText.setEditable(false);//不可点击
        northPanel.setLayout(new GridLayout(1, 2));
        //添加到主机区域
        pcPanel.add(new JLabel("设置主机数：", SwingConstants.RIGHT));
        pcPanel.add(mPcNumText);
        pcPanel.add(mUpdateBtn);
        //添加到时延区域
        timePanel.add(new JLabel("设置时延范围(ms)：", SwingConstants.RIGHT));
        timePanel.add(mPcTimeLaterText);
        timePanel.add(mTimeLaterBtn);
        //将主机区域和时延区域添加到顶部区域
        northPanel.add(pcPanel);
        northPanel.add(timePanel);
        add(northPanel, BorderLayout.NORTH);

        //中部区域
        textArea = new JTextArea(5, 10);
        textArea.setEnabled(false);  //不能点击
        textArea.setDisabledTextColor(Color.BLACK);//颜色设置
        //滚动区域
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        //底部区域
        JPanel southPanel = new JPanel();
        mSendBtn = new JButton("发送");
        mClearBtn = new JButton("清空");
        southPanel.add(mSendBtn);  //将按钮添加到底部面板中
        southPanel.add(mClearBtn);
        add(southPanel, BorderLayout.SOUTH);


    }

    private void onClick() {
        //关于我们点击效果
        mAboutItem.addActionListener(e -> {
            if (mAboutDialog == null) {
                mAboutDialog = new AboutDialog(this);
            }
            mAboutDialog.setSize(mWidth / 2, mHeight / 2); //设置dialog的大小
            mAboutDialog.setLocation(mWidth * 3 / 4, mHeight * 3 / 4); //设置dialog的位置
            mAboutDialog.setVisible(true);
        });
        //帮助点击
        mHelpItem.addActionListener(e -> {
            if (mHelpDialog == null) {
                mHelpDialog = new HelpDialog(this);
            }
            mHelpDialog.setSize(mWidth / 2, mHeight / 2);//设置dialog的大小
            mHelpDialog.setLocation(mWidth * 3 / 4, mHeight * 3 / 4);//设置dialog的位置
            mHelpDialog.setVisible(true);
        });

        //修改主机数按钮
        mUpdateBtn.addActionListener(actionEvent -> {
            String pcNumString = JOptionPane.showInputDialog("请输入主机数");  //弹出文本框
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
                        mPcNumText.setText(pcNumString);  //主机数文本框设置
                    }
                } catch (Exception e) {//输入的是不是纯数字
                    JOptionPane.showMessageDialog(this, "请输入数字",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //修改主机数按钮
        mTimeLaterBtn.addActionListener(actionEvent -> {
            String timeLaterString = JOptionPane.showInputDialog("请输入随机时延的最大值");
            if (timeLaterString != null) {
                try {
                    maxTime = Integer.valueOf(timeLaterString);
                    if (maxTime < 0) {
                        JOptionPane.showMessageDialog(this, "时延最大值不能为负数",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else if (maxTime > 100000) {
                        JOptionPane.showMessageDialog(this, "时延最大值为100s",
                                "错误", JOptionPane.ERROR_MESSAGE);
                    } else {
                        mPcTimeLaterText.setText("0~" + timeLaterString);  //主机数文本框设置
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "请输入数字",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });


        //发送按钮
        mSendBtn.addActionListener(actionEvent -> {
            if (!textArea.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "请先清空数据",
                        "错误", JOptionPane.ERROR_MESSAGE);
            } else {//数据为空
                for (int i = 0; i < pcNum; i++) {
                    names.add((char) ('A' + i)); //添加主机名
                }
                for (int i = 0; i < pcNum; i++) {
                    PcRunnable pcRun = new PcRunnable();
                    threads.add(new Thread(pcRun, String.valueOf(names.get(i))));  //添加主机
                    pcRun.setId(threads.get(i).getId()) //设置主机号
                            .setPcName(threads.get(i).getName()) //设置主机名
                            .setDelay((long) (Math.random() * maxTime)); //给每台主机设置随机的时延
                }
                for (int i = 0; i < pcNum; i++) {
                    threads.get(i).start();  //开始发送数据;
                }

            }


        });
        //清空按钮
        mClearBtn.addActionListener(actionEvent -> {
            if (textArea.getText().equals("")) {//是否为空
                JOptionPane.showMessageDialog(this, "数据已为空",
                        "提示", JOptionPane.ERROR_MESSAGE);
            } else {
                boolean isAlive = false;  //判断是否还有线程处于活动中
                for (Thread thread : threads) {
                    if (thread.isAlive()) {
                        isAlive = true;
                        break;
                    }
                }
                if(isAlive){ //还有线程处于活动中
                    JOptionPane.showMessageDialog(this,
                            "目前还有主机正在发送数据，请发送完后重试",
                            "错误", JOptionPane.ERROR_MESSAGE);
                }else {//线程都执行完毕，即主机已经停止发送数据
                    textArea.setText("");
                    threads.clear();
                    names.clear();
                }

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

    /**
     * 主机发送数据实现类
     */
    class PcRunnable implements Runnable {
        private int successCount = 0;  //成功次数
        private int repeatCount = 0;  //重传次数
        private long id;  //线程号
        private String name; //主机名
        private long delay;//时延


        PcRunnable setId(long id) {
            this.id = id;
            return this;
        }

        PcRunnable setPcName(String name) {
            this.name = name;
            return this;
        }

        PcRunnable setDelay(long delay){
            this.delay = delay;
            return this;
        }

        @Override
        public void run() {
            while (true) {
                if (bus == 0) { //总线空闲，获取发送权
                    bus = bus | id; //模拟发送数据
                    //模拟主机间的传播时延
                    try {
                        //51200ns即51.2us，确保主机的传播时延大于51.2us
                        Thread.sleep(delay,51200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (bus == id) {//数据发送成功
                        successCount++;  //成功次数加1
                        textArea.append("ID "+id + " send success，主机" + name + "：发送成功数=" + successCount + "，" +
                                "  重传次数=" + repeatCount + "\n");
                        bus = 0;  //释放总线
                        repeatCount = 0;  //重传次数重置为0
                        if (successCount >= 10) {
                            textArea.append("主机" + name + "成功发送10次数据\n");
                            break;  //成功发送10次，线程结束
                        }
                    } else {//产生冲突
                        textArea.append("ID "+id + " send collision， 主机" + name + "：发送成功数=" + successCount + "，" +
                                "  重传次数=" + repeatCount + "\n");
                        bus = 0;  //释放总线
                        repeatCount++;  //重传次数加1
                        if (repeatCount <= 16) {  //使用截至二进制指数退避算法重发数据
                            try {
                                Thread.sleep((long) backOff(repeatCount));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {//重传达16次仍然不成功
                            textArea.append("ID "+id + "(主机" + name + ")" + "：send failure\n");
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
