package com.thefreak.client.game;

import com.thefreak.client.IO.Input;
import com.thefreak.client.game.level.Level;
import com.thefreak.client.game.level.TileType;
import com.thefreak.client.graphics.Sprite;
import com.thefreak.client.graphics.SpriteSheet;
import com.thefreak.client.graphics.TextureAtlas;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    public static final Integer SPRITE_SCALE = 16;
    public static final Integer SPRITES_PER_HEADING = 1;

    protected static final Integer EVOLVING_TIME = 1300;
    private static final List<Sprite> evolvingList = new ArrayList<>();

    protected Float x;
    protected Float y;
    protected Float scale;
    static Level lvl;
    protected TextureAtlas atlas;
    Boolean evolving;
    Long createdTime;
    Integer animationCount;
    Boolean isAlive;

    Entity(Float x, Float y, Float scale, TextureAtlas atlas, Level lvl) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.atlas = atlas;
        Entity.lvl = lvl;
        isAlive = true;

        createdTime = System.currentTimeMillis();
        animationCount = 0;
        evolving = true;
        for (Integer i = 16; i < 20; i++) {
            evolvingList.add(
                    new Sprite(
                            new SpriteSheet(
                                    atlas.cut(i * SPRITE_SCALE, 6 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                                    SPRITES_PER_HEADING, SPRITE_SCALE), scale)
            );
        }
    }

    public abstract void update(Input input);

    public abstract void render(Graphics2D g);

    public abstract boolean isDead();

    protected Rectangle2D.Float getRectangle() {
        return new Rectangle2D.Float(x, y, SPRITE_SCALE * scale, SPRITE_SCALE * scale);
    }

    Rectangle2D.Float getRectangle(Float newX, Float newY) {
        return new Rectangle2D.Float(newX, newY, SPRITE_SCALE * scale, SPRITE_SCALE * scale);
    }

    void drawEvolving(Graphics2D g) {
        if (animationCount % 12 < 3) {
            evolvingList.get(0).render(g, x, y);
        } else if (animationCount % 12 >= 3 && animationCount % 12 < 6) {
            evolvingList.get(1).render(g, x, y);
        } else if (animationCount % 12 >= 6 && animationCount % 12 < 9) {
            evolvingList.get(2).render(g, x, y);
        } else if (animationCount % 12 >= 9) {
            evolvingList.get(3).render(g, x, y);
        }
        animationCount++;
        if (System.currentTimeMillis() > createdTime + EVOLVING_TIME) {
            evolving = false;
        }
    }

    boolean canMove(Float newX, Float newY, Float centerX, Float centerY, Float bottomX, Float bottomY) {
        Integer tileX = (int) (newX / Level.SCALED_TILE_SIZE);
        Integer tileY = (int) (newY / Level.SCALED_TILE_SIZE);
        Integer tileCenterX = (int) (centerX / Level.SCALED_TILE_SIZE);
        Integer tileCenterY = (int) (centerY / Level.SCALED_TILE_SIZE);
        Integer tileBottomX = bottomX % Level.SCALED_TILE_SIZE == 0 ? tileCenterX
                : (int) (bottomX / Level.SCALED_TILE_SIZE);
        Integer tileBottomY = bottomY % Level.SCALED_TILE_SIZE == 0 ? tileCenterY
                : (int) (bottomY / Level.SCALED_TILE_SIZE);

        Integer[][] tileMap = lvl.getTileMap();

        return !(Integer.max(tileY, tileBottomY) >= tileMap.length || Integer.max(tileX, tileBottomX) >= tileMap[0].length
                || isImpossibleTile(tileMap[tileY][tileX], tileMap[tileCenterY][tileCenterX],
                tileMap[tileBottomY][tileBottomX]));

    }

    private boolean isImpossibleTile(Integer... tileNum) {
        for (Integer aTileNum : tileNum)
            if (aTileNum == TileType.BRICK.numeric()
                    || aTileNum == TileType.METAL.numeric()
                    || aTileNum == TileType.DOWN_LEFT_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_RIGHT_EAGLE.numeric()
                    || aTileNum == TileType.UP_LEFT_EAGLE.numeric()
                    || aTileNum == TileType.UP_RIGHT_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_LEFT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_RIGHT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.UP_LEFT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.UP_RIGHT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.WATER.numeric()
                    || aTileNum == TileType.OTHER_WATER.numeric()) {
                return true;
            }
        return false;
    }

    public void drawExplosion(Graphics2D g) {

        Float adjustedX = x - SPRITE_SCALE;
        Float adjustedY = y - SPRITE_SCALE;

        SpriteSheet expSheet = new SpriteSheet(
                atlas.cut(19 * SPRITE_SCALE, 8 * SPRITE_SCALE, 2 * SPRITE_SCALE, 2 * SPRITE_SCALE), SPRITES_PER_HEADING,
                2 * SPRITE_SCALE);
        Sprite expSprite = new Sprite(expSheet, scale);
        SpriteSheet bigExpSheet = new SpriteSheet(
                atlas.cut(21 * SPRITE_SCALE, 8 * SPRITE_SCALE, 2 * SPRITE_SCALE, 2 * SPRITE_SCALE), SPRITES_PER_HEADING,
                2 * SPRITE_SCALE);
        Sprite bigExpSprite = new Sprite(bigExpSheet, scale);
        long curTime = System.currentTimeMillis();

        new Thread(() -> {
            long time = System.currentTimeMillis();
            while (time < curTime + 150) {
                expSprite.render(g, adjustedX, adjustedY);
                bigExpSprite.render(g, adjustedX, adjustedY);
                time = System.currentTimeMillis();
            }
        }).start();

    }

}
