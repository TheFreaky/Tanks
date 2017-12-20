package com.thefreak.client.graphics;

import com.thefreak.client.utils.ResourceLoader;

import java.awt.image.BufferedImage;

public class TextureAtlas {
    private BufferedImage image;

    public TextureAtlas(String imageName) {
        image = ResourceLoader.loadImage(imageName);
    }

    public BufferedImage cut(int x, int y, int width, int height) {
        return image.getSubimage(x, y, width, height);
    }
}
