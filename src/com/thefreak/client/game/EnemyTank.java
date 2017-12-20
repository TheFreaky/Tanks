package com.thefreak.client.game;

import com.thefreak.client.game.level.Level;
import com.thefreak.client.graphics.TextureAtlas;

public class EnemyTank extends Enemy {
    private static final Integer NORTH_X = 8;
    private static final Integer NORTH_Y = 0;
    private static final Float SPEED = 1.8f;
    private static final Integer LIVES = 0;

    public EnemyTank(Float x, Float y, Float scale, TextureAtlas atlas, Level lvl, Game game) {
        super(x, y, scale, SPEED, atlas, lvl, NORTH_X, NORTH_Y, LIVES, game);
    }
}
