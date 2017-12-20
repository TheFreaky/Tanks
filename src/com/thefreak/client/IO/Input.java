package com.thefreak.client.IO;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class Input extends JComponent {
    private boolean[] map;

    public Input() {
        map = new boolean[256];

        for (int i = 0; i < map.length; i++) {
            final int KEY_CODE = i;

            //Клавиша нажата
            KeyStroke keyPressed = KeyStroke.getKeyStroke(i, 0, false);

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyPressed, i * 2);
            getActionMap().put(i * 2, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    map[KEY_CODE] = true;
                }
            });

            //Клавиша отпущена
            KeyStroke keyReleased = KeyStroke.getKeyStroke(i, 0, true);

            getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyReleased, i * 2 + 1);
            getActionMap().put(i * 2 + 1, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent arg0) {
                    map[KEY_CODE] = false;
                }
            });
        }
    }

    public boolean getKey(int keyCode) {
        return map[keyCode];
    }
}
