package dialog;

import javax.swing.*;
import java.awt.*;

/**
 * <pre>
 *     author : 谢雯琦
 *     desc   : help对话框框
 * </pre>
 */
public class HelpDialog extends JDialog {
    public HelpDialog(JFrame frame) {
        super(frame, "Help", true);

        JLabel titleLabel = new JLabel("参数相关说明", JLabel.CENTER);
        JPanel northPanel = new JPanel();
        northPanel.add(titleLabel);
        add(northPanel,BorderLayout.NORTH);
        JTextArea textArea = new JTextArea(4, 40);
        textArea.setLineWrap(true);
        textArea.setEnabled(false); //文本区不能修改
        textArea.setDisabledTextColor(Color.BLACK);
        textArea.append("   1.主机数：默认为2，只能输入数字。可设置为2~26个主机，主机名称根据设置的主机数从A" +
                "依次对应（如果是2，则主机名称为A,B,依次类推）。\n"+
                "   2.时延范围：单位为ms,默认为0~1。只能设置时延的最大值tMax，设置后每台主机的时延"+
                "将为整数集合[0,tMax]随机的一个整数。" +
                "时延为主机发送数据的时间，即传播时延，时延越大，则冲突的几率就越大");
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