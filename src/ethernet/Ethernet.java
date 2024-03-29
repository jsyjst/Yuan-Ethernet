package ethernet;

import frame.ShowFrame;
import test.ShowFramebyTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <pre>
 *     author : 谢雯琦
 *     desc   : 主程序
 * </pre>
 */
public class Ethernet {
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            ShowFrame frame = new ShowFrame();
            //不执行任何操作;要求程序在已注册的 WindowListener 对象的 windowClosing 方法中处理该操作
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    int result = JOptionPane.showConfirmDialog(null, "确认退出?", "确认", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if(result == JOptionPane.OK_OPTION){
                        System.exit(0);
                    }
                }
            });
            frame.setTitle("Ethernet帧的发送过程模拟");
            frame.setVisible(true);
        });
    }
}
