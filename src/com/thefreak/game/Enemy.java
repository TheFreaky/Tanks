package com.thefreak.game;

import com.thefreak.IO.Input;
import com.thefreak.game.level.Level;
import com.thefreak.graphics.Sprite;
import com.thefreak.graphics.SpriteSheet;
import com.thefreak.graphics.TextureAtlas;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public abstract class Enemy extends Entity {
    private static final Integer DELAY = 2000;

    private EnemyHeading enemyHeading;
    private Map<EnemyHeading, Sprite> spriteMap;
    private Float speed;
    private Float bulletSpeed;
    private Player player;
    private Bullet bullet;
    private Bonus bonus;
    private Integer lives;
    private Game game;

    public enum EnemyHeading {
        NORTH, EAST, SOUTH, WEST;

        private Integer x;
        private Integer y;
        private Integer height;
        private Integer width;

        protected BufferedImage texture(TextureAtlas atlas) {
            return atlas.cut(x, y, width, height);
        }

        private EnemyHeading getFromNumber(int number) {
            switch (number) {
                case 0:
                    return NORTH;
                case 1:
                    return SOUTH;
                case 2:
                    return EAST;
                default:
                    return WEST;
            }
        }

        private void setCords(int x, Integer y, Integer w, Integer h) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;

        }
    }

    public Enemy(Float x, Float y, Float scale, Float speed, TextureAtlas atlas, Level lvl, Integer headX, Integer headY,
                 Integer lives, Game game) {
        super(x, y, scale, atlas, lvl);
        this.game = game;
        enemyHeading = EnemyHeading.NORTH;
        spriteMap = new HashMap<>();
        this.speed = speed;
        bulletSpeed = 4f;
        this.lives = lives;

        Random rand = new Random();
        if (rand.nextInt(8) == 7) {
            bonus = Bonus.fromNumeric(rand.nextInt(6));
        }

        for (EnemyHeading eh : EnemyHeading.values()) {
            switch (eh) {
                case NORTH:
                    eh.setCords(headX * SPRITE_SCALE, headY * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE);
                    break;
                case EAST:
                    eh.setCords((headX + 6) * SPRITE_SCALE, headY * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE);
                    break;
                case SOUTH:
                    eh.setCords((headX + 4) * SPRITE_SCALE, headY * SPRITE_SCALE, SPRITE_SCALE, 16);
                    break;
                case WEST:
                    eh.setCords((headX + 2) * SPRITE_SCALE, headY * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE);
                    break;
            }
            SpriteSheet sheet = new SpriteSheet(eh.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale);
            spriteMap.put(eh, sprite);
        }

    }

    @Override
    public void update(Input input) {
        if (evolving || !isAlive)
            return;

        Float newX = x;
        Float newY = y;

        switch (enemyHeading) {
            case NORTH:
                newY -= speed;
                newX = (float) ((Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE);
                break;
            case EAST:
                newX += speed;
                newY = (float) ((Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE);
                break;
            case SOUTH:
                newY += speed;
                newX = (float) ((Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE);
                break;
            case WEST:
                newX -= speed;
                newY = (float) ((Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE);
                break;
        }

        if (newX < 0) {
            newX = 0f;
            enemyHeading = changeEnemyHeading();
        } else if (newX > Game.WIDTH - SPRITE_SCALE * scale) {
            newX = Game.WIDTH - SPRITE_SCALE * scale;
            enemyHeading = changeEnemyHeading();
        }

        if (newY < 0) {
            newY = 0f;
            enemyHeading = changeEnemyHeading();
        } else if (newY > Game.HEIGHT - SPRITE_SCALE * scale) {
            newY = Game.HEIGHT - SPRITE_SCALE * scale;
            enemyHeading = changeEnemyHeading();
        }

        switch (enemyHeading) {
            case NORTH:
                if (canMove(newX, newY, newX + (SPRITE_SCALE * scale / 2), newY, newX + (SPRITE_SCALE * scale), newY)
                        && notIntersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading();
                break;
            case SOUTH:
                if (canMove(newX, newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale / 2),
                        newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading();
                break;
            case EAST:
                if (canMove(newX + (SPRITE_SCALE * scale), newY, newX + (SPRITE_SCALE * scale),
                        newY + (SPRITE_SCALE * scale / 2), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading();
                break;
            case WEST:
                if (canMove(newX, newY, newX, newY + (SPRITE_SCALE * scale / 2), newX, newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)
                        && (player == null || !getRectangle(newX, newY).intersects(player.getRectangle()))) {
                    x = newX;
                    y = newY;
                } else
                    enemyHeading = changeEnemyHeading();
                break;
        }

        if (bullet == null && System.currentTimeMillis() % DELAY < 50) {
            bullet = new Bullet(x, y, scale, bulletSpeed, enemyHeading.toString().substring(0, 4), atlas, lvl,
                    EntityType.Enemy, game);
        }
        if (bullet != null && !bullet.isActive() && System.currentTimeMillis() % DELAY < 50) {
            bullet = new Bullet(x, y, scale, bulletSpeed, enemyHeading.toString().substring(0, 4), atlas, lvl,
                    EntityType.Enemy, game);
        }

    }

    private EnemyHeading changeEnemyHeading() {
        Random random = new Random();
        Integer direction = random.nextInt(4);
        EnemyHeading newEnemyHeading = enemyHeading.getFromNumber(direction);
        if (newEnemyHeading == enemyHeading) {
            changeEnemyHeading();
        }
        return newEnemyHeading;
    }

    @Override
    public void render(Graphics2D g) {
        if (!isAlive) {
            return;
        }

        if (evolving) {
            drawEvolving(g);
            return;
        }
        spriteMap.get(enemyHeading).render(g, x, y);

    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    private boolean notIntersectsEnemy(Float newX, Float newY) {
        List<Enemy> enemyList = game.getEnemies();
        Rectangle2D.Float rect = getRectangle(newX, newY);
        for (Enemy enemy : enemyList) {
            if (enemy != this && rect.intersects(enemy.getRectangle()))
                return false;
        }
        return true;
    }

    public boolean isEvolving() {
        return evolving;
    }

    @Override
    public boolean isDead() {
        if (!isAlive && bonus != null) {
            lvl.setBonus(bonus);
        }
        return !isAlive;
    }

    public void setDead() {
        isAlive = false;
    }

    public void fixHitting(int playerStrength) {
        lives -= playerStrength;
    }

    public boolean hasMoreLives() {
        return lives >= 0;
    }
}
