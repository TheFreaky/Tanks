package com.thefreak.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WaitDialog extends JDialog {

    public WaitDialog(Frame owner) {
        super(owner);
    }

    public void showDialog() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        this.add(content);
        String text = "Подключение установлено, ожидание второго игрока...";
        JLabel message = new JLabel(text);
        message.setBorder(new EmptyBorder(10, 10, 10, 10));
        message.setAlignmentX(0);
        message.setSize(message.getPreferredSize());
        content.add(message);

        this.setUndecorated(true);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

}