package dialog;

import javax.swing.*;
import java.awt.*;

/**
 * <pre>
 *     author : 谢雯琦
 *     desc   : about对话框
 * </pre>
 */
public class AboutDialog extends JDialog {
    public AboutDialog(JFrame frame) {
        super(frame, "About", true);

        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new GridLayout(2, 0));
        JLabel authorLabel = new JLabel("姓名：谢雯琦     学号：3217004554    班级：计算机科学与技术3班", JLabel.LEFT);
        JLabel titleLabel = new JLabel("Ethernet帧的发送过程模拟", JLabel.CENTER);
        hintPanel.add(authorLabel);
        hintPanel.add(titleLabel);
        add(hintPanel, BorderLayout.NORTH);
        JTextArea textArea = new JTextArea(4, 40);
        textArea.setLineWrap(true);
        textArea.append("  1.用N个线程Ti  (0<=i<=N)模拟Ethernet上的N台主机。(N 的取值自行决定，尽可能大点)\n" +
                "  2.用一个变量Bus来模拟总线（将其初始化为”0”，表示总线空闲)\n" +
                "  3.多个子线程向总线发送自己的数据。" +
                "数据用该线程的线程号进行模拟，" +
                "发送数据用线程号和Bus的“或”操作进行模拟（即Bus=Bus|ID，ID为该线程的线程号）。\n" +
                "  4.每台主机必须在总线上发送成功10次数据，如果其中某次数据发送失败，则该线程结束。\n" +
                "  5.发送流程必须遵循CSMA/CD。随机延迟算法中的冲突窗口取\n0.005。" +
                "在数据发送成功（即Bus= =ID）后，报告“ID send success”，" +
                "产生冲突（即Bus！=ID）后报告“ID send collision”，" +
                "发送失败（即冲突计数器值为0）后报告“ID send failure”。" +
                "随着主机发送次数增加，报告其已经发送成功的次数，如“主机A发送成功数=3。\n" +
                "  6.若程序中不能模拟出冲突，可以在某些地方人为地加入延时。\n" +
                "  7.用可视化界面进行展示。");
        textArea.setEnabled(false); //文本区不能修改
        textArea.setDisabledTextColor(Color.BLACK);
        JScrollPane centerPanel = new JScrollPane(textArea);
        add(centerPanel, BorderLayout.CENTER);
        JButton ok = new JButton("OK");
        ok.addActionListener(e -> setVisible(false));


        JPanel panel = new JPanel();
        panel.add(ok);
        add(panel, BorderLayout.SOUTH);
        pack();
    }

}
