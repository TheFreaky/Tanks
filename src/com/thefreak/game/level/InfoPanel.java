package com.thefreak.game.level;

import com.thefreak.game.Game;
import com.thefreak.game.Player;
import com.thefreak.graphics.Sprite;
import com.thefreak.graphics.SpriteSheet;
import com.thefreak.graphics.TextureAtlas;
import com.thefreak.utils.Utils;

import java.awt.*;

public class InfoPanel {
    private TextureAtlas atlas;
    private Sprite enemySprite;
    private SpriteSheet numbersToFour;
    private SpriteSheet numbersToNine;
    private SpriteSheet signs;
    private Integer stage;

    InfoPanel(TextureAtlas atlas, Integer stage) {
        this.atlas = atlas;
        this.stage = stage % 10;

        signs = new SpriteSheet(
                atlas.cut(41 * Level.TILE_SCALE, 25 * Level.TILE_SCALE, 2 * Level.TILE_SCALE, Level.TILE_SCALE), 2,
                Level.TILE_SCALE);
        numbersToFour = new SpriteSheet(
                atlas.cut(41 * Level.TILE_SCALE, 23 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE), 5,
                Level.TILE_SCALE);
        numbersToNine = new SpriteSheet(
                atlas.cut(41 * Level.TILE_SCALE, 24 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE), 5,
                Level.TILE_SCALE);
        enemySprite = new Sprite(new SpriteSheet(
                atlas.cut(40 * Level.TILE_SCALE, 24 * Level.TILE_SCALE, Level.TILE_SCALE, Level.TILE_SCALE), 1,
                Level.TILE_SCALE), Level.TILE_IN_GAME_SCALE, 0, false);
    }

    public void renderInfoPanel(Graphics2D g, Integer enemyCount, Integer score, Boolean scoreMoreThanOpponent) {
        g.drawImage(Utils.resize(atlas.cut(46 * Level.TILE_SCALE, 0, Level.TILE_SCALE, Level.TILE_SCALE),
                8 * Level.SCALED_TILE_SIZE, Game.HEIGHT), Game.WIDTH, 0, null);

        g.drawImage(
                Utils.resize(
                        atlas.cut(41 * Level.TILE_SCALE, 22 * Level.TILE_SCALE, 5 * Level.TILE_SCALE, Level.TILE_SCALE),
                        5 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE, null);

        new Sprite(stage < 5 ? numbersToFour : numbersToNine, Level.TILE_IN_GAME_SCALE, stage % 5, false).
                render(g, Game.WIDTH + 6 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE);

        g.drawImage(
                Utils.resize(
                        atlas.cut(47 * Level.TILE_SCALE, 17 * Level.TILE_SCALE, 2 * Level.TILE_SCALE, Level.TILE_SCALE),
                        2 * Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + 3 * Level.SCALED_TILE_SIZE, 15 * Level.SCALED_TILE_SIZE, null);

        g.drawImage(
                Utils.resize(
                        atlas.cut(47 * Level.TILE_SCALE, 18 * Level.TILE_SCALE, Level.TILE_SCALE, Level.TILE_SCALE),
                        Level.SCALED_TILE_SIZE, Level.SCALED_TILE_SIZE),
                Game.WIDTH + 3 * Level.SCALED_TILE_SIZE, 16 * Level.SCALED_TILE_SIZE, null);

        Integer playerLives = Player.getPlayerLives() < 0 ? 0 : Player.getPlayerLives();

        new Sprite(playerLives < 5 ? numbersToFour : numbersToNine, Level.TILE_IN_GAME_SCALE,
                playerLives % 5, false)
                .render(g, Game.WIDTH + 4 * Level.SCALED_TILE_SIZE,
                        16 * Level.SCALED_TILE_SIZE);


        // Отрисовывает знак + или -
        new Sprite(signs, Level.TILE_IN_GAME_SCALE,
                scoreMoreThanOpponent ? 0 : 1, false)
                .render(g, Game.WIDTH,
                        24 * Level.SCALED_TILE_SIZE);

        // Отрисовывает кол-во очков
        String scoreStr = score.toString();
        for (int i = 0; i < scoreStr.length(); i++) {
            int digit = Integer.valueOf(scoreStr.substring(i, i + 1));

            new Sprite(digit < 5 ? numbersToFour : numbersToNine, Level.TILE_IN_GAME_SCALE,
                    digit % 5, false)
                    .render(g, Game.WIDTH + (i + 1) * Level.SCALED_TILE_SIZE,
                            24 * Level.SCALED_TILE_SIZE);
        }

        // Отрисовывает кол-во противников, которые еще будут созданы
        for (int i = 0; i < enemyCount; i++) {
            enemySprite.render(g, Game.WIDTH + 3 * Level.SCALED_TILE_SIZE + i % 2 * Level.SCALED_TILE_SIZE,
                    3 * Level.SCALED_TILE_SIZE + i / 2 * Level.SCALED_TILE_SIZE);
        }
    }
}
