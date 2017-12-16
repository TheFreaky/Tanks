package com.thefreak.game;

import com.thefreak.game.level.Level;
import com.thefreak.game.level.TileType;
import com.thefreak.graphics.Sprite;
import com.thefreak.graphics.SpriteSheet;
import com.thefreak.graphics.TextureAtlas;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Bullet {

    public enum BulletHeading {
        B_NORTH(20 * Player.SPRITE_SCALE, 6 * Player.SPRITE_SCALE + 4, Player.SPRITE_SCALE / 2,
                Player.SPRITE_SCALE / 2),
        B_EAST(21 * Player.SPRITE_SCALE + Player.SPRITE_SCALE / 2,
                6 * Player.SPRITE_SCALE + 4, Player.SPRITE_SCALE / 2, Player.SPRITE_SCALE / 2),
        B_SOUTH(21 * Player.SPRITE_SCALE, 6 * Player.SPRITE_SCALE + 4, Player.SPRITE_SCALE / 2,
                Player.SPRITE_SCALE / 2),
        B_WEST(20 * Player.SPRITE_SCALE + Player.SPRITE_SCALE / 2,
                6 * Player.SPRITE_SCALE + 4, Player.SPRITE_SCALE / 2,
                Player.SPRITE_SCALE / 2);

        private Integer x;
        private Integer y;
        private Integer h;
        private Integer w;

        BulletHeading(Integer x, Integer y, Integer h, Integer w) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        protected BufferedImage texture(TextureAtlas atlas) {
            return atlas.cut(x, y, w, h);
        }
    }

    private Float speed;
    private Map<BulletHeading, Sprite> spriteMap;
    private BulletHeading bulletHeading;
    private Float x;
    private Float y;
    private Float scale;
    private Boolean isActive;
    private Level lvl;
    private EntityType type;
    private Boolean explosionDone;
    private List<Sprite> explosionList;
    private Integer animationCount;
    private Game game;

    Bullet(Float x, Float y, Float scale, Float speed, String direction, TextureAtlas atlas, Level lvl,
           EntityType type, Game game) {
        this.game = game;

        spriteMap = new HashMap<>();
        this.lvl = lvl;
        isActive = true;
        this.type = type;
        animationCount = 0;
        this.scale = scale;
        this.speed = speed;
        explosionDone = false;
        explosionList = new ArrayList<>();
        explosionList
                .add(new Sprite(
                        new SpriteSheet(atlas.cut(16 * Player.SPRITE_SCALE, 8 * Player.SPRITE_SCALE,
                                Player.SPRITE_SCALE, Player.SPRITE_SCALE), Player.SPRITE_SCALE, Player.SPRITE_SCALE),
                        scale));
        explosionList
                .add(new Sprite(
                        new SpriteSheet(atlas.cut(17 * Player.SPRITE_SCALE, 8 * Player.SPRITE_SCALE,
                                Player.SPRITE_SCALE, Player.SPRITE_SCALE), Player.SPRITE_SCALE, Player.SPRITE_SCALE),
                        scale));
        explosionList
                .add(new Sprite(
                        new SpriteSheet(atlas.cut(18 * Player.SPRITE_SCALE, 8 * Player.SPRITE_SCALE,
                                Player.SPRITE_SCALE, Player.SPRITE_SCALE), Player.SPRITE_SCALE, Player.SPRITE_SCALE),
                        scale));

        for (BulletHeading bh : BulletHeading.values()) {
            SpriteSheet sheet = new SpriteSheet(bh.texture(atlas), Player.SPRITES_PER_HEADING, Player.SPRITE_SCALE / 2);
            Sprite sprite = new Sprite(sheet, scale);
            spriteMap.put(bh, sprite);
        }
        switch (direction) {
            case "EAST":
                bulletHeading = BulletHeading.B_EAST;
                this.x = x + Player.SPRITE_SCALE * scale / 2;
                this.y = y + (Player.SPRITE_SCALE * scale) / 4;
                break;
            case "NORT":
                bulletHeading = BulletHeading.B_NORTH;
                this.x = x + (Player.SPRITE_SCALE * scale) / 4;
                this.y = y;
                break;
            case "WEST":
                bulletHeading = BulletHeading.B_WEST;
                this.x = x;
                this.y = y + (Player.SPRITE_SCALE * scale) / 4;
                break;
            case "SOUT":
                bulletHeading = BulletHeading.B_SOUTH;
                this.x = x + (Player.SPRITE_SCALE * scale) / 4;
                this.y = y + Player.SPRITE_SCALE * scale / 2;
                break;
        }
        game.registerBullet(type, this);

    }

    void update() {
        if (!isActive) return;

        switch (bulletHeading) {
            case B_EAST:
                x += speed;
                if (!canFly(x + Player.SPRITE_SCALE * scale / 4, y, x + Player.SPRITE_SCALE * scale / 4,
                        y + Player.SPRITE_SCALE * scale / 4))
                    isActive = false;
                break;
            case B_NORTH:
                y -= speed;
                if (!canFly(x, y, x + Player.SPRITE_SCALE * scale / 4, y))
                    isActive = false;
                break;
            case B_SOUTH:
                y += speed;
                if (!canFly(x, y + Player.SPRITE_SCALE * scale / 4, x + Player.SPRITE_SCALE * scale / 4,
                        y + Player.SPRITE_SCALE * scale / 4))
                    isActive = false;
                break;
            case B_WEST:
                x -= speed;
                if (!canFly(x, y, x, y + Player.SPRITE_SCALE * scale / 4))
                    isActive = false;
                break;
        }

        if (type == EntityType.Player) {
            List<Bullet> enemyBullets = game.getBullets(EntityType.Enemy);
            for (Bullet bullet : enemyBullets) {
                if (getRectangle().intersects(bullet.getRectangle())) {
                    isActive = false;
                    bullet.setInactive();
                    bullet.disableExplosion();
                    explosionDone = true;
                }
            }
        }

        if (x < 0 || x >= Game.WIDTH || y < 0 || y > Game.HEIGHT) {
            isActive = false;
        }

    }

    void render(Graphics2D g) {
        if (!isActive && explosionDone) {
            game.unregisterBullet(type, this);
            return;
        }
        if (!isActive) {
            drawExplosion(g);
        }

        if (isActive) {
            spriteMap.get(bulletHeading).render(g, x, y);
        }
    }

    private Boolean canFly(Float startX, Float startY, Float endX, Float endY) {
        Integer tileStartX = (int) (startX / Level.SCALED_TILE_SIZE);
        Integer tileStartY = (int) (startY / Level.SCALED_TILE_SIZE);
        Integer tileEndX = (int) (endX / Level.SCALED_TILE_SIZE);
        Integer tileEndY = (int) (endY / Level.SCALED_TILE_SIZE);

        Integer[][] tileArray = lvl.getTileMap();

        if (Integer.max(tileStartY, tileEndY) >= tileArray.length
                || Integer.max(tileStartX, tileEndX) >= tileArray[0].length || Integer.min(tileStartY, tileEndY) < 0
                || Integer.min(tileStartX, tileEndX) < 0) {
            return false;
        } else if (isImpassableTile(tileArray[tileStartY][tileStartX], tileArray[tileEndY][tileEndX])) {

            if (isDestroyableTile(tileArray[tileStartY][tileStartX])) {
                lvl.update(tileStartX, tileStartY);
            }
            if (isDestroyableTile(tileArray[tileEndY][tileEndX])) {
                lvl.update(tileEndX, tileEndY);
            }
            return false;
        }
        return true;
    }

    private Boolean isDestroyableTile(Integer tileNum) {
        return tileNum == TileType.BRICK.numeric() || tileNum == TileType.DOWN_LEFT_EAGLE.numeric()
                || tileNum == TileType.DOWN_RIGHT_EAGLE.numeric() || tileNum == TileType.UP_LEFT_EAGLE.numeric()
                || tileNum == TileType.UP_RIGHT_EAGLE.numeric()
                || tileNum == TileType.METAL.numeric() && type == EntityType.Player && Player.getPlayerStrength() == 3;

    }

    private Boolean isImpassableTile(Integer... tileNum) {
        for (Integer aTileNum : tileNum) {
            if (aTileNum == TileType.BRICK.numeric() || aTileNum == TileType.METAL.numeric()
                    || aTileNum == TileType.DOWN_LEFT_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_RIGHT_EAGLE.numeric()
                    || aTileNum == TileType.UP_LEFT_EAGLE.numeric() || aTileNum == TileType.UP_RIGHT_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_LEFT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.DOWN_RIGHT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.UP_LEFT_DEAD_EAGLE.numeric()
                    || aTileNum == TileType.UP_RIGHT_DEAD_EAGLE.numeric()) {
                return true;
            }
        }
        return false;
    }

    Boolean isActive() {
        return isActive;
    }

    Rectangle2D.Float getRectangle() {
        return new Rectangle2D.Float(x, y, Player.SPRITE_SCALE * scale / 2, Player.SPRITE_SCALE * scale / 2);
    }

    void setInactive() {
        isActive = false;
    }

    private void drawExplosion(Graphics2D g) {
        if (explosionDone)
            return;

        Float adjustedX = x - Player.SPRITE_SCALE * scale / 4;
        Float adjustedY = y - Player.SPRITE_SCALE * scale / 4;

        if (animationCount % 9 < 3) {
            explosionList.get(0).render(g, adjustedX, adjustedY);
        } else if (animationCount % 9 >= 3 && animationCount % 9 < 6) {
            explosionList.get(1).render(g, adjustedX, adjustedY);
        } else if (animationCount % 9 > 6) {
            explosionList.get(2).render(g, adjustedX, adjustedY);
        }
        animationCount++;

        if (animationCount > 12) {
            explosionDone = true;
        }
    }

    private void disableExplosion() {
        explosionDone = true;
    }
}
