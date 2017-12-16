package com.thefreak.game;

import com.thefreak.game.level.Level;
import com.thefreak.graphics.TextureAtlas;

public class EnemyInfantryVehicle extends Enemy {
    private static final Integer NORTH_X = 8;
    private static final Integer NORTH_Y = 5;
    private static final Float SPEED = 2.5f;
    private static final Integer LIVES = 0;

    public EnemyInfantryVehicle(Float x, Float y, Float scale, TextureAtlas atlas, Level lvl, Game game) {
        super(x, y, scale, SPEED, atlas, lvl, NORTH_X, NORTH_Y, LIVES, game);
    }
}
