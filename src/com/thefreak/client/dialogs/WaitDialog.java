package com.thefreak.client.dialogs;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WaitDialog extends JDialog {

    private JLabel textLabel;

    public WaitDialog(Frame owner) {
        super(owner);
    }

    public void showDialog() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

        this.add(content);
        String text = "Подключение установлено, ожидание второго игрока...";
        textLabel = new JLabel(text);
        textLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
        textLabel.setAlignmentX(0);
        textLabel.setSize(textLabel.getPreferredSize());
        content.add(textLabel);

        this.setUndecorated(true);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }

    public void setText(String text) {
        textLabel.setText(text);
    }

}