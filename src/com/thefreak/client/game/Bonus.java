package com.thefreak.client.game;

import com.thefreak.client.graphics.TextureAtlas;

import java.awt.image.BufferedImage;

public enum Bonus {

    PROTECTION(16 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE),
    FREEZE(17 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE),
    SHIELD(18 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE),
    STAR(19 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE),
    DETONATION(20 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE),
    LIFE(21 * Entity.SPRITE_SCALE, 7 * Entity.SPRITE_SCALE, Entity.SPRITE_SCALE, Entity.SPRITE_SCALE);


    private int x;
    private int y;
    private int height;
    private int width;

    Bonus(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public BufferedImage texture(TextureAtlas atlas) {
        return atlas.cut(x, y, width, height);
    }

    public static Bonus fromNumeric(int n) {
        switch (n) {
            case 0:
                return PROTECTION;
            case 1:
                return FREEZE;
            case 2:
                return SHIELD;
            case 3:
                return STAR;
            case 4:
                return DETONATION;
            case 5:
                return LIFE;
            default:
                return LIFE;
        }
    }
}
