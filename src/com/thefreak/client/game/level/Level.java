package com.thefreak.client.game.level;

import com.thefreak.client.game.Bonus;
import com.thefreak.client.game.Entity;
import com.thefreak.client.game.Game;
import com.thefreak.client.graphics.Sprite;
import com.thefreak.client.graphics.SpriteSheet;
import com.thefreak.client.graphics.TextureAtlas;
import com.thefreak.client.utils.Utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class Level {
    public static final Integer TILE_SCALE = 8;
    public static final Integer TILE_IN_GAME_SCALE = 3;
    public static final Integer SCALED_TILE_SIZE = TILE_SCALE * TILE_IN_GAME_SCALE;

    private static final Integer BONUS_DURATION = 10000;
    private static final Integer[] ARRAY_TO_RESTORE_EAGLE = new Integer[20];

    private Integer[][] tileMap;
    private Map<TileType, Tile> tiles;
    private List<Point> grassCords;
    private Integer count;
    private TextureAtlas atlas;
    private Bonus bonus;
    private Sprite bonusSprite;
    private Point bonusPoint;
    private Boolean hasBonus;
    private Long bonusCreatedTime;
    private Boolean eagleProtected;
    private InfoPanel infoPanel;
    private Boolean eagleAlive;
    private Game game;

    public Level(TextureAtlas atlas, Integer stage, Game game) {
        this.game = game;
        tiles = new HashMap<>();
        count = 0;
        this.atlas = atlas;
        hasBonus = false;
        eagleProtected = false;
        infoPanel = new InfoPanel(atlas, stage);
        eagleAlive = true;

        tiles.put(TileType.BRICK,
                new Tile(atlas.cut(32 * TILE_SCALE, 0, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.BRICK)
        );
        tiles.put(TileType.METAL,
                new Tile(atlas.cut(32 * TILE_SCALE, 2 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.METAL)
        );
        tiles.put(TileType.WATER,
                new Tile(atlas.cut(34 * TILE_SCALE, 10 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.WATER)
        );
        tiles.put(TileType.GRASS,
                new Tile(atlas.cut(34 * TILE_SCALE, 4 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.GRASS)
        );
        tiles.put(TileType.EMPTY,
                new Tile(atlas.cut(36 * TILE_SCALE, 6 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.EMPTY)
        );
        tiles.put(TileType.UP_LEFT_EAGLE,
                new Tile(atlas.cut(38 * TILE_SCALE, 4 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.UP_LEFT_EAGLE)
        );
        tiles.put(TileType.UP_RIGHT_EAGLE,
                new Tile(atlas.cut(39 * TILE_SCALE, 4 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.UP_RIGHT_EAGLE)
        );
        tiles.put(TileType.DOWN_LEFT_EAGLE,
                new Tile(atlas.cut(38 * TILE_SCALE, 5 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.DOWN_LEFT_EAGLE)
        );
        tiles.put(TileType.DOWN_RIGHT_EAGLE,
                new Tile(atlas.cut(39 * TILE_SCALE, 5 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.DOWN_RIGHT_EAGLE)
        );
        tiles.put(TileType.UP_LEFT_DEAD_EAGLE,
                new Tile(atlas.cut(40 * TILE_SCALE, 4 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.UP_LEFT_DEAD_EAGLE)
        );
        tiles.put(TileType.UP_RIGHT_DEAD_EAGLE,
                new Tile(atlas.cut(41 * TILE_SCALE, 4 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.UP_RIGHT_DEAD_EAGLE)
        );
        tiles.put(TileType.DOWN_LEFT_DEAD_EAGLE,
                new Tile(atlas.cut(40 * TILE_SCALE, 5 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.DOWN_LEFT_DEAD_EAGLE)
        );
        tiles.put(TileType.DOWN_RIGHT_DEAD_EAGLE,
                new Tile(atlas.cut(41 * TILE_SCALE, 5 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.DOWN_RIGHT_DEAD_EAGLE)
        );
        tiles.put(TileType.OTHER_WATER,
                new Tile(atlas.cut(33 * TILE_SCALE, 10 * TILE_SCALE, TILE_SCALE, TILE_SCALE),
                        TILE_IN_GAME_SCALE,
                        TileType.OTHER_WATER)
        );

        tileMap = Utils.levelParser("res/lvl" + stage + ".lvl");
        grassCords = new ArrayList<>();
        for (int i = 0; i < tileMap.length; i++) {
            for (int j = 0; j < tileMap[i].length; j++) {
                if (tileMap[i][j] == TileType.GRASS.numeric()) {
                    grassCords.add(new Point(j * SCALED_TILE_SIZE, i * SCALED_TILE_SIZE));
                }
            }
        }
    }

    public void update(int tileX, Integer tileY) {
        if (tileMap[tileY][tileX] == TileType.DOWN_LEFT_EAGLE.numeric()
                || tileMap[tileY][tileX] == TileType.DOWN_RIGHT_EAGLE.numeric()
                || tileMap[tileY][tileX] == TileType.UP_LEFT_EAGLE.numeric()
                || tileMap[tileY][tileX] == TileType.UP_RIGHT_EAGLE.numeric()) {
            destroyEagle();
        } else {
            tileMap[tileY][tileX] = TileType.EMPTY.numeric();
        }
    }

    private void destroyEagle() {
        for (int i = 0; i < tileMap.length; i++)
            for (int j = 0; j < tileMap[i].length; j++) {
                if (tileMap[i][j] == TileType.DOWN_LEFT_EAGLE.numeric()) {
                    tileMap[i][j] = TileType.DOWN_LEFT_DEAD_EAGLE.numeric();
                } else if (tileMap[i][j] == TileType.DOWN_RIGHT_EAGLE.numeric()) {
                    tileMap[i][j] = TileType.DOWN_RIGHT_DEAD_EAGLE.numeric();

                } else if (tileMap[i][j] == TileType.UP_LEFT_EAGLE.numeric()) {
                    tileMap[i][j] = TileType.UP_LEFT_DEAD_EAGLE.numeric();

                } else if (tileMap[i][j] == TileType.UP_RIGHT_EAGLE.numeric()) {
                    tileMap[i][j] = TileType.UP_RIGHT_DEAD_EAGLE.numeric();
                }
            }
        eagleAlive = false;
        game.setGameOver();
    }

    public void render(Graphics2D g) {
        count = ++count % 20;

        for (int i = 0; i < tileMap.length; i++)
            for (int j = 0; j < tileMap[i].length; j++) {
                Tile tile = tiles.get(TileType.fromNumeric(tileMap[i][j]));
                if (tile.type() == TileType.WATER && count < 10) {
                    tiles.get(TileType.fromNumeric(5)).render(g, j * SCALED_TILE_SIZE, i * SCALED_TILE_SIZE);
                } else {
                    if (tile.type() != TileType.GRASS) {

                        tile.render(g, j * SCALED_TILE_SIZE, i * SCALED_TILE_SIZE);

                    }
                }
            }

        if (bonus != null) {
            bonusSprite.render(g, bonusPoint.x, bonusPoint.y);
            if (System.currentTimeMillis() > bonusCreatedTime + BONUS_DURATION)
                removeBonus();
        }

        if (eagleProtected && System.currentTimeMillis() > bonusCreatedTime + BONUS_DURATION) {
            eagleProtected = false;
            restoreEagle();
        }

        if (game.getScore() >= game.getOpponentScore()) {
            infoPanel.renderInfoPanel(g, game.getEnemyCount(),
                    game.getScore() - game.getOpponentScore(), true);
        } else {
            infoPanel.renderInfoPanel(g, game.getEnemyCount(),
                    game.getOpponentScore() - game.getScore(), false);
        }

    }

    public void renderGrass(Graphics2D g) {
        for (Point p : grassCords) {
            tiles.get(TileType.GRASS).render(g, p.x, p.y);
        }
    }

    public Integer[][] getTileMap() {
        return tileMap;
    }

    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
        SpriteSheet sheet = new SpriteSheet(bonus.texture(atlas), Entity.SPRITES_PER_HEADING, Entity.SPRITE_SCALE);
        bonusSprite = new Sprite(sheet, Game.SCALE);
        Random rand = new Random();
        bonusPoint = new Point(rand.nextInt(12) * (int) (Entity.SPRITE_SCALE * Game.SCALE),
                (int) (rand.nextInt(12) * (Entity.SPRITE_SCALE * Game.SCALE)));
        hasBonus = true;
        bonusCreatedTime = System.currentTimeMillis();
    }

    public Boolean hasBonus() {
        return hasBonus;
    }

    public Rectangle2D getBonusRectangle() {
        return new Rectangle2D.Float(bonusPoint.x, bonusPoint.y, Entity.SPRITE_SCALE * Game.SCALE,
                Entity.SPRITE_SCALE * Game.SCALE);
    }

    public Bonus getBonus() {
        return bonus;
    }

    public void removeBonus() {
        bonus = null;
        bonusSprite = null;
        bonusPoint = null;
        hasBonus = false;
    }

    public void protectEagle() {
        eagleProtected = true;
        bonusCreatedTime = System.currentTimeMillis();

        ARRAY_TO_RESTORE_EAGLE[0] = tileMap[25][10];
        ARRAY_TO_RESTORE_EAGLE[1] = tileMap[25][11];
        ARRAY_TO_RESTORE_EAGLE[2] = tileMap[24][10];
        ARRAY_TO_RESTORE_EAGLE[3] = tileMap[24][11];
        ARRAY_TO_RESTORE_EAGLE[4] = tileMap[23][10];
        ARRAY_TO_RESTORE_EAGLE[5] = tileMap[23][11];
        ARRAY_TO_RESTORE_EAGLE[6] = tileMap[22][10];
        ARRAY_TO_RESTORE_EAGLE[7] = tileMap[22][11];
        ARRAY_TO_RESTORE_EAGLE[8] = tileMap[23][12];
        ARRAY_TO_RESTORE_EAGLE[9] = tileMap[23][13];
        ARRAY_TO_RESTORE_EAGLE[10] = tileMap[22][12];
        ARRAY_TO_RESTORE_EAGLE[11] = tileMap[22][13];
        ARRAY_TO_RESTORE_EAGLE[12] = tileMap[25][15];
        ARRAY_TO_RESTORE_EAGLE[13] = tileMap[25][14];
        ARRAY_TO_RESTORE_EAGLE[14] = tileMap[24][15];
        ARRAY_TO_RESTORE_EAGLE[15] = tileMap[24][14];
        ARRAY_TO_RESTORE_EAGLE[16] = tileMap[23][15];
        ARRAY_TO_RESTORE_EAGLE[17] = tileMap[23][14];
        ARRAY_TO_RESTORE_EAGLE[18] = tileMap[22][15];
        ARRAY_TO_RESTORE_EAGLE[19] = tileMap[22][14];

        tileMap[25][10] = 2;
        tileMap[25][11] = 2;
        tileMap[24][10] = 2;
        tileMap[24][11] = 2;
        tileMap[23][10] = 2;
        tileMap[23][11] = 2;
        tileMap[22][10] = 2;
        tileMap[22][11] = 2;
        tileMap[23][12] = 2;
        tileMap[23][13] = 2;
        tileMap[22][12] = 2;
        tileMap[22][13] = 2;
        tileMap[25][15] = 2;
        tileMap[25][14] = 2;
        tileMap[24][15] = 2;
        tileMap[24][14] = 2;
        tileMap[23][15] = 2;
        tileMap[23][14] = 2;
        tileMap[22][15] = 2;
        tileMap[22][14] = 2;
    }

    private void restoreEagle() {
        tileMap[25][10] = ARRAY_TO_RESTORE_EAGLE[0];
        tileMap[25][11] = ARRAY_TO_RESTORE_EAGLE[1];
        tileMap[24][10] = ARRAY_TO_RESTORE_EAGLE[2];
        tileMap[24][11] = ARRAY_TO_RESTORE_EAGLE[3];
        tileMap[23][10] = ARRAY_TO_RESTORE_EAGLE[4];
        tileMap[23][11] = ARRAY_TO_RESTORE_EAGLE[5];
        tileMap[22][10] = ARRAY_TO_RESTORE_EAGLE[6];
        tileMap[22][11] = ARRAY_TO_RESTORE_EAGLE[7];
        tileMap[23][12] = ARRAY_TO_RESTORE_EAGLE[8];
        tileMap[23][13] = ARRAY_TO_RESTORE_EAGLE[9];
        tileMap[22][12] = ARRAY_TO_RESTORE_EAGLE[10];
        tileMap[22][13] = ARRAY_TO_RESTORE_EAGLE[11];
        tileMap[25][15] = ARRAY_TO_RESTORE_EAGLE[12];
        tileMap[25][14] = ARRAY_TO_RESTORE_EAGLE[13];
        tileMap[24][15] = ARRAY_TO_RESTORE_EAGLE[14];
        tileMap[24][14] = ARRAY_TO_RESTORE_EAGLE[15];
        tileMap[23][15] = ARRAY_TO_RESTORE_EAGLE[16];
        tileMap[23][14] = ARRAY_TO_RESTORE_EAGLE[17];
        tileMap[22][15] = ARRAY_TO_RESTORE_EAGLE[18];
        tileMap[22][14] = ARRAY_TO_RESTORE_EAGLE[19];
    }

    public Boolean isEagleAlive() {
        return eagleAlive;
    }
}
