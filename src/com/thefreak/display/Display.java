package com.thefreak.display;

import com.thefreak.IO.Input;
import com.thefreak.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Display {
    private static boolean created = false;
    private static JFrame window;

    private static BufferedImage buffer;
    private static int[] bufferData;
    private static Graphics bufferGraphics;
    private static Integer clearColor;

    private static BufferStrategy bufferStrategy;

    public static void create(Integer width, Integer height, String title, Integer clearColor, Integer numBuffers) {
        if (created) return;

        window = new JFrame(title);
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem newGameMenu = new MenuItem("New");
        newGameMenu.addActionListener((event) -> Game.reset());
        window.setMenuBar(menuBar);
        menuBar.add(gameMenu);
        gameMenu.add(newGameMenu);

        Canvas content = new Canvas();

        Dimension size = new Dimension(width, height);
        content.setPreferredSize(size);

        window.setResizable(false);
        window.getContentPane().add(content);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        bufferData = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
        bufferGraphics = buffer.getGraphics();
        ((Graphics2D) bufferGraphics).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Display.clearColor = clearColor;

        content.createBufferStrategy(numBuffers);

        bufferStrategy = content.getBufferStrategy();

        created = true;

    }

    public static void clear() {
        Arrays.fill(bufferData, clearColor);
    }

    public static void swapBuffers() {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.drawImage(buffer, 0, 0, null);
        bufferStrategy.show();
    }

    public static Graphics2D getGraphics() {
        return (Graphics2D) bufferGraphics;
    }

    public static void addInputListener(Input inputListener) {
        window.add(inputListener);
    }
}
