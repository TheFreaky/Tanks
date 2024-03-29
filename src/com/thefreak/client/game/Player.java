package com.thefreak.client.game;

import com.thefreak.client.IO.Input;
import com.thefreak.client.game.level.Level;
import com.thefreak.client.graphics.Sprite;
import com.thefreak.client.graphics.SpriteSheet;
import com.thefreak.client.graphics.TextureAtlas;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Entity {
    private static final int PROTECTION_TIME = 4000;
    private static final float APPEARANCE_X = Entity.SPRITE_SCALE * Game.SCALE * 4;
    private static final float APPEARANCE_Y = Entity.SPRITE_SCALE * Game.SCALE * 12;

    public enum Heading {
        NORTH_SIMPLE(0, 0, SPRITE_SCALE, SPRITE_SCALE),
        EAST_SIMPLE(6 * SPRITE_SCALE, 0, SPRITE_SCALE, SPRITE_SCALE),
        SOUTH_SIMPLE(4 * SPRITE_SCALE, 0, SPRITE_SCALE, SPRITE_SCALE),
        WEST_SIMPLE(2 * SPRITE_SCALE, 0, SPRITE_SCALE, SPRITE_SCALE),

        NORTH_MEDIUM(0, 2 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        EAST_MEDIUM(6 * SPRITE_SCALE, 2 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        SOUTH_MEDIUM(4 * SPRITE_SCALE, 2 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        WEST_MEDIUM(2 * SPRITE_SCALE, 2 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),

        NORTH_STRONG(0, 7 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        EAST_STRONG(6 * SPRITE_SCALE, 7 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        SOUTH_STRONG(4 * SPRITE_SCALE, 7 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
        WEST_STRONG(2 * SPRITE_SCALE, 7 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),;

        private int x, y, h, w;

        Heading(int x, int y, int h, int w) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        protected BufferedImage texture(TextureAtlas atlas) {
            return atlas.cut(x, y, w, h);
        }
    }

    private static int lives;
    private static int strength;

    private Heading heading;
    private Map<Heading, Sprite> spriteMap;
    private float speed;
    private float bulletSpeed;
    private Bullet bullet;
    private boolean isProtected;
    private List<Sprite> protectionList;
    private Game game;

    Player(float scale, float speed, TextureAtlas atlas, Level lvl, Game game) {
        super(APPEARANCE_X, APPEARANCE_Y, scale, atlas, lvl);
        this.game = game;

        heading = Heading.NORTH_SIMPLE;
        spriteMap = new HashMap<>();
        this.speed = speed;
        bulletSpeed = 6;
        lives = 2;
        strength = 1;

        isProtected = true;
        protectionList = new ArrayList<>();
        protectionList.add(
                new Sprite(new SpriteSheet(
                        atlas.cut(16 * SPRITE_SCALE, 9 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));
        protectionList.add(
                new Sprite(new SpriteSheet(
                        atlas.cut(17 * SPRITE_SCALE, 9 * SPRITE_SCALE, SPRITE_SCALE, SPRITE_SCALE),
                        SPRITES_PER_HEADING, SPRITE_SCALE), scale));

        for (Heading h : Heading.values()) {
            SpriteSheet sheet = new SpriteSheet(h.texture(atlas), SPRITES_PER_HEADING, SPRITE_SCALE);
            Sprite sprite = new Sprite(sheet, scale);
            spriteMap.put(h, sprite);
        }
    }

    @Override
    public void update(Input input) {
        if (!lvl.isEagleAlive() || evolving) return;

        if (System.currentTimeMillis() > createdTime + EVOLVING_TIME + PROTECTION_TIME) {
            isProtected = false;
        }

        float newX = x;
        float newY = y;

        if (input.getKey(KeyEvent.VK_UP)) {
            newY -= speed;
            newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            x = newX;
            heading = strength > 1 ? (strength > 2 ? Heading.NORTH_STRONG : Heading.NORTH_MEDIUM)
                    : Heading.NORTH_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_RIGHT)) {
            newX += speed;
            newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            y = newY;
            heading = strength > 1 ? (strength > 2 ? Heading.EAST_STRONG : Heading.EAST_MEDIUM) : Heading.EAST_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_DOWN)) {
            newY += speed;
            newX = (Math.round(newX / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            x = newX;
            heading = strength > 1 ? (strength > 2 ? Heading.SOUTH_STRONG : Heading.SOUTH_MEDIUM)
                    : Heading.SOUTH_SIMPLE;
        } else if (input.getKey(KeyEvent.VK_LEFT)) {
            newX -= speed;
            newY = (Math.round(newY / Level.SCALED_TILE_SIZE)) * Level.SCALED_TILE_SIZE;
            y = newY;
            heading = strength > 1 ? (strength > 2 ? Heading.WEST_STRONG : Heading.WEST_MEDIUM) : Heading.WEST_SIMPLE;
        }

        if (newX < 0) {
            newX = 0;
        } else if (newX >= Game.WIDTH - SPRITE_SCALE * scale) {
            newX = Game.WIDTH - SPRITE_SCALE * scale;
        }

        if (newY < 0) {
            newY = 0;
        } else if (newY >= Game.HEIGHT - SPRITE_SCALE * scale) {
            newY = Game.HEIGHT - SPRITE_SCALE * scale;
        }

        switch (heading) {
            case NORTH_SIMPLE:
            case NORTH_MEDIUM:
            case NORTH_STRONG:
                if (canMove(newX, newY, newX + (SPRITE_SCALE * scale / 2), newY, newX + (SPRITE_SCALE * scale), newY)
                        && notIntersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case SOUTH_SIMPLE:
            case SOUTH_MEDIUM:
            case SOUTH_STRONG:
                if (canMove(newX, newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale / 2),
                        newY + (SPRITE_SCALE * scale), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case EAST_SIMPLE:
            case EAST_MEDIUM:
            case EAST_STRONG:
                if (canMove(newX + (SPRITE_SCALE * scale), newY, newX + (SPRITE_SCALE * scale),
                        newY + (SPRITE_SCALE * scale / 2), newX + (SPRITE_SCALE * scale), newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
            case WEST_SIMPLE:
            case WEST_MEDIUM:
            case WEST_STRONG:
                if (canMove(newX, newY, newX, newY + (SPRITE_SCALE * scale / 2), newX, newY + (SPRITE_SCALE * scale))
                        && notIntersectsEnemy(newX, newY)) {
                    x = newX;
                    y = newY;
                }
                break;
        }

        List<Bullet> bullets = game.getBullets(EntityType.Enemy);
        for (Bullet enemyBullet : bullets) {
            if (getRectangle().intersects(enemyBullet.getRectangle()) && enemyBullet.isActive()) {
                if (!isProtected) {
                    isAlive = false;
                }
                enemyBullet.setInactive();
            }
        }

        if (lvl.hasBonus() && getRectangle().intersects(lvl.getBonusRectangle())) {
            Bonus bonus = lvl.getBonus();
            switch (bonus) {
                case PROTECTION:
                    createdTime = System.currentTimeMillis();
                    isProtected = true;
                    break;
                case FREEZE:
                    game.freezeEnemies();
                    break;
                case SHIELD:
                    lvl.protectEagle();
                    break;
                case STAR:
                    upgrade();
                    break;
                case DETONATION:
                    game.detonateEnemies();
                    break;
                case LIFE:
                    if (++lives > 9)
                        lives = 9;
                    break;
            }
            lvl.removeBonus();

        }

        if (input.getKey(KeyEvent.VK_SPACE) &&
                (bullet == null || !bullet.isActive()) &&
                game.getBullets(EntityType.Player).isEmpty()) {
            bullet = new Bullet(x, y, scale, bulletSpeed, heading.toString().substring(0, 4), atlas, lvl,
                    EntityType.Player, game);
        }

    }

    private boolean notIntersectsEnemy(float newX, float newY) {
        List<Enemy> enemyList = game.getEnemies();
        Rectangle2D.Float rect = getRectangle(newX, newY);
        for (Enemy enemy : enemyList) {
            if (rect.intersects(enemy.getRectangle()))
                return false;
        }
        return true;
    }

    @Override
    public void render(Graphics2D g) {
        if (evolving) {
            drawEvolving(g);
            return;
        }
        spriteMap.get(heading).render(g, x, y);

        if (isProtected)
            drawProtection(g);

    }

    private void drawProtection(Graphics2D g) {
        if (animationCount % 16 < 8) {
            protectionList.get(0).render(g, x, y);
        } else {
            protectionList.get(1).render(g, x, y);
        }
        animationCount++;

    }

    @Override
    public void drawExplosion(Graphics2D g) {
        super.drawExplosion(g);
        lives--;
        if (lives >= 0) {
            reset();
        } else {
            game.setGameOver();
        }
    }

    private void reset() {
        this.x = APPEARANCE_X;
        this.y = APPEARANCE_Y;
        isAlive = true;
        evolving = true;
        isProtected = true;
        createdTime = System.currentTimeMillis();
        strength = 1;
        heading = Heading.NORTH_SIMPLE;
    }

    boolean hasMoreLives() {
        return lives >= 0;
    }

    @Override
    public boolean isDead() {
        return !isAlive;
    }

    private void upgrade() {
        if (++strength > 3)
            strength = 3;

        switch (heading) {
            case NORTH_SIMPLE:
                heading = Heading.NORTH_MEDIUM;
                break;
            case EAST_SIMPLE:
                heading = Heading.EAST_MEDIUM;
                break;
            case SOUTH_SIMPLE:
                heading = Heading.SOUTH_MEDIUM;
                break;
            case WEST_SIMPLE:
                heading = Heading.WEST_MEDIUM;
                break;

            case NORTH_MEDIUM:
                heading = Heading.NORTH_STRONG;
                break;
            case EAST_MEDIUM:
                heading = Heading.EAST_STRONG;
                break;
            case SOUTH_MEDIUM:
                heading = Heading.SOUTH_STRONG;
                break;
            case WEST_MEDIUM:
                heading = Heading.WEST_STRONG;
                break;

            case NORTH_STRONG:
            case EAST_STRONG:
            case SOUTH_STRONG:
            case WEST_STRONG:
        }
    }

    public static int getPlayerLives() {
        return lives;
    }

    static int getPlayerStrength() {
        return strength;
    }

    void moveOnNextLevel() {
        this.x = APPEARANCE_X;
        this.y = APPEARANCE_Y;
        evolving = true;
        isProtected = true;
        bullet = null;
        createdTime = System.currentTimeMillis();

        switch (heading) {
            case EAST_SIMPLE:
            case SOUTH_SIMPLE:
            case WEST_SIMPLE:
            case NORTH_SIMPLE:
                heading = Heading.NORTH_SIMPLE;
                break;

            case EAST_MEDIUM:
            case SOUTH_MEDIUM:
            case WEST_MEDIUM:
            case NORTH_MEDIUM:
                heading = Heading.NORTH_MEDIUM;
                break;

            case EAST_STRONG:
            case SOUTH_STRONG:
            case WEST_STRONG:
            case NORTH_STRONG:
                heading = Heading.NORTH_STRONG;
                break;
        }
    }
}
