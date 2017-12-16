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
    private static JFrame mainFrame; //Рамка

    private BufferedImage buffer; //Изображение, на которое будет все помещаться, а после с него на экран
    private int[] bufferData; //информация о изображении BufferedImage (массив цветов в ARGB)
    private Graphics bufferGraphics;
    private Color clearColor; // Цвет для очистки

    private BufferStrategy bufferStrategy;

    public Display(Integer width, Integer height, String title, Color clearColor, Integer numBuffers, Game game) {
        mainFrame = new JFrame(title);
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Программа закрывается, при нажатии на крестик

        MenuBar menuBar = new MenuBar();
        Menu gameMenu = new Menu("Game");
        MenuItem newGameMenu = new MenuItem("New");
        newGameMenu.addActionListener((event) -> game.reset());
        mainFrame.setMenuBar(menuBar);
        menuBar.add(gameMenu);
        gameMenu.add(newGameMenu);

        Canvas content = new Canvas(); // "Лист" внутри рамки

        Dimension contentSize = new Dimension(width, height); //Обрабатывает разрешение окна
        content.setPreferredSize(contentSize);

        mainFrame.setResizable(false); //Исключает возможность изменить размер окна
        mainFrame.getContentPane().add(content); //Исключает из области добавления занятые участи, т.е. тулбары
        mainFrame.pack(); //Изменяет размер окна под контент
        mainFrame.setLocationRelativeTo(null); //Окно появляется по середине экрана
        mainFrame.setVisible(true);

        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // ARGB - Прозрачность + RGB
        bufferData = ((DataBufferInt) buffer.getRaster().getDataBuffer())
                .getData();
        bufferGraphics = buffer.getGraphics();
        ((Graphics2D) bufferGraphics)
                .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //Сглаживание
        this.clearColor = clearColor;

        content.createBufferStrategy(numBuffers); //Создание стратегии буферизации для большей плавности

        bufferStrategy = content.getBufferStrategy();
    }

    public void clear() {
        Arrays.fill(bufferData, clearColor.getRGB());
    }

    //Меняет то, что мы видим внутри канваса на новую сцену
    public void swapBuffers() {
        Graphics g = bufferStrategy.getDrawGraphics();
        g.drawImage(buffer, 0, 0, null);
        bufferStrategy.show();
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) bufferGraphics;
    }

    public void addInputListener(Input inputListener) {
        mainFrame.add(inputListener);
    }
}
