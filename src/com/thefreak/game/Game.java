package com.thefreak.game;

import com.thefreak.IO.Input;
import com.thefreak.display.Display;
import com.thefreak.game.level.Level;
import com.thefreak.graphics.TextureAtlas;
import com.thefreak.utils.Time;
import com.thefreak.utils.Utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

public class Game implements Runnable {

    public static final Integer WIDTH = 624;
    public static final Integer HEIGHT = 624;
    public static final Float SCALE = 3f;

    private static final String TITLE = "Tanks";
    private static final Color CLEAR_COLOR = new Color(0xff000000);
    private static final Integer NUM_BUFFERS = 3;
    private static final Float UPDATE_RATE = 60.0f; //Количество вычислений в секунду
    private static final Float UPDATE_INTERVAL = Time.SECOND / UPDATE_RATE; //Время между каждым апдейтом
    private static final Long IDLE_TIME = 1L; //Кол-во времени для "отдыха" программы(1 милисек)
    private static final String ATLAS_FILE_NAME = "texture_atlas.png";
    private static final Float PLAYER_SPEED = 3f;
    private static final Integer FREEZE_TIME = 8000;

    private List<Enemy> enemyList = new LinkedList<>();
    private Integer stage = 1;
    private Map<EntityType, List<Bullet>> bullets;
    private Graphics2D graphics;
    private Boolean enemiesFrozen;
    private Long freezeImposedTime;
    private Boolean gameOver;
    private TextureAtlas atlas;
    private Player player;
    private Level lvl;
    private Integer enemyCount;
    private Display display;
    private Boolean running;
    private Input input;
    private BufferedImage gameOverImage;
    private long levelStart;
    private long timeWin;
    private Integer score; //Кол-во очков
    private Integer opponentScore; //Кол-во очков противника

    public Game() {
        opponentScore = 0;
        score = 0;
        levelStart = System.currentTimeMillis();
        running = false;
        display = new Display(WIDTH + 8 * Level.SCALED_TILE_SIZE, HEIGHT, TITLE, CLEAR_COLOR, NUM_BUFFERS, this);
        graphics = display.getGraphics();
        input = new Input();
        display.addInputListener(input);
        atlas = new TextureAtlas(ATLAS_FILE_NAME);
        bullets = new HashMap<>();
        bullets.put(EntityType.Player, new LinkedList<>());
        bullets.put(EntityType.Enemy, new LinkedList<>());
        lvl = new Level(atlas, stage, this);
        player = new Player(SCALE, PLAYER_SPEED, atlas, lvl, this);
        enemiesFrozen = false;
        enemyCount = 20;
        timeWin = 0L;
        gameOver = false;
        gameOverImage = Utils.resize(
                atlas.cut(36 * Level.TILE_SCALE, 23 * Level.TILE_SCALE, 4 * Level.TILE_SCALE,
                        2 * Level.TILE_SCALE),
                4 * Level.SCALED_TILE_SIZE, 2 * Level.SCALED_TILE_SIZE);

        for (int i = 0; i < gameOverImage.getHeight(); i++)
            for (int j = 0; j < gameOverImage.getWidth(); j++) {
                Integer pixel = gameOverImage.getRGB(j, i);
                if ((pixel & 0x00FFFFFF) < 10)
                    gameOverImage.setRGB(j, i, (pixel & 0x00FFFFFF));
            }
    }

    public void start() {
        if (running) return;

        running = true;
        Thread gameThread = new Thread(this);
        gameThread.start();
    }

    //Считает физику и все математические расчеты
    private void update() {

        if (enemyList.size() == 0 && enemyCount == 0 && timeWin == 0) {
            timeWin = System.currentTimeMillis();
        }

        if (enemyList.size() == 0 && enemyCount == 0 && player.hasMoreLives() && !gameOver) {
            nextLevel();
        }

        Boolean canCreateEnemy = true;

        if (enemyList.size() < 4 && enemyCount > 0) {
            Random rand = new Random();
            Float possibleX = rand.nextInt(3) * ((Game.WIDTH - Player.SPRITE_SCALE * Game.SCALE) / 2);
            Rectangle2D.Float recForX = new Rectangle2D.Float(possibleX, 0, Player.SPRITE_SCALE * Game.SCALE,
                    Player.SPRITE_SCALE * Game.SCALE);

            for (Enemy enemy : enemyList) {
                if (enemy.isEvolving()) {
                    canCreateEnemy = false;
                    break;
                }

                if (canCreateEnemy) {
                    if (recForX.intersects(enemy.getRectangle())) {
                        canCreateEnemy = false;
                    }
                }
            }

            if (canCreateEnemy) {
                if (player != null) {
                    if (recForX.intersects(player.getRectangle())) {
                        canCreateEnemy = false;
                    }
                }

                if (!canCreateEnemy) return;

                Enemy enemy;
                enemyCount--;
                if (stage == 1) {
                    if (enemyCount < 3) {
                        enemy = new EnemyInfantryVehicle(possibleX, 0f, SCALE, atlas, lvl, this);
                    } else {
                        enemy = new EnemyTank(possibleX, 0f, SCALE, atlas, lvl, this);
                    }
                } else {
                    Random random = new Random();
                    switch (random.nextInt(3)) {
                        case 0:
                            enemy = new EnemyInfantryVehicle(possibleX, 0f, SCALE, atlas, lvl, this);
                            break;
                        case 1:
                            enemy = new EnemyGreenTank(possibleX, 0f, SCALE, atlas, lvl, this);
                            break;
                        default:
                            enemy = new EnemyTank(possibleX, 0f, SCALE, atlas, lvl, this);
                    }
                }
                enemy.setPlayer(player);
                enemyList.add(enemy);
            }
        }

        List<Bullet> playerBulletList = getBullets(EntityType.Player);
        if (!playerBulletList.isEmpty()) {
            for (Enemy enemy : enemyList) {
                if (enemy.isEvolving()) continue;
                if (enemy.getRectangle().intersects(playerBulletList.get(0).getRectangle())
                        && playerBulletList.get(0).isActive()) {
                    enemy.fixHitting(Player.getPlayerStrength());
                    playerBulletList.get(0).setInactive();

                    if (!enemy.hasMoreLives()) {
                        enemy.setDead();
                    }
                }
            }
        }

        if (enemiesFrozen) {
            if (System.currentTimeMillis() > freezeImposedTime + FREEZE_TIME) {
                enemiesFrozen = false;
            }
        } else {
            enemyList.forEach(enemy -> enemy.update(input));
        }

        for (List<Bullet> bulletList : bullets.values()) {
            bulletList.forEach(Bullet::update);
        }

        if (player != null && !player.hasMoreLives()) {
            player = null;
        }

        if (player != null) {
            player.update(input);
        }
    }

    private void nextLevel() {
        if (timeWin == 0 || System.currentTimeMillis() < timeWin + 5000) return;

        score += Player.getPlayerLives() * 500;
        score += Player.getPlayerStrength() * 1000;
        int timeWinSec = (int) ((timeWin - levelStart) / 1000);

        int maxTimeSec = 300;
        if (timeWinSec <= maxTimeSec) {
            score += (maxTimeSec - timeWinSec) * 10;
        }

        bullets = new HashMap<>();
        bullets.put(EntityType.Player, new LinkedList<>());
        bullets.put(EntityType.Enemy, new LinkedList<>());

        stage++;
        if (stage > 3) {
            stage = 1;
        }

        lvl = new Level(atlas, stage, this);
        enemiesFrozen = false;
        enemyCount = 20;
        enemyList = new LinkedList<>();
        player.moveOnNextLevel();
        timeWin = 0L;
    }

    //Отрисовка сцены
    private void render() {
        display.clear();
        lvl.render(graphics);

        if (player != null) {
            if (player.isDead()) {
                player.drawExplosion(graphics);
            } else {
                player.render(graphics);
            }
        }

        for (int i = 0; i < enemyList.size(); i++) {
            if (enemyList.get(i).isDead()) {
                enemyList.get(i).drawExplosion(graphics);

                if (enemyList.get(i) instanceof EnemyTank) {
                    score += 100;
                } else if (enemyList.get(i) instanceof EnemyInfantryVehicle) {
                    score += 150;
                } else {
                    score += 200;
                }
                enemyList.remove(i);

            }
        }

        enemyList.forEach(enemy -> enemy.render(graphics));


        for (int i = 0; i < bullets.get(EntityType.Enemy).size(); i++)
            bullets.get(EntityType.Enemy).get(i).render(graphics);


        for (Bullet bullet : getBullets(EntityType.Player))
            bullet.render(graphics);


        lvl.renderGrass(graphics);

        if (gameOver) {
            graphics.drawImage(gameOverImage, Game.WIDTH / 2 - 2 * Level.SCALED_TILE_SIZE,
                    Game.HEIGHT / 2, null);
        }
        display.swapBuffers();
    }

    public void run() {
        Float delta = 0f;
        Boolean render;
        Long lastTime = Time.get();

        while (running) {
            Long now = Time.get();
            Long elapsedTime = now - lastTime;
            lastTime = now;

            render = false;
            delta += (elapsedTime / UPDATE_INTERVAL);

            while (delta > 1) {
                update();
                delta--;
                if (!render) {
                    render = true;
                }
            }

            if (render) {
                render();
            } else {
                try {
                    Thread.sleep(IDLE_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    List<Enemy> getEnemies() {
        return enemyList;
    }

    void registerBullet(EntityType type, Bullet bullet) {
        bullets.get(type).add(bullet);
    }

    void unregisterBullet(EntityType type, Bullet bullet) {
        if (!bullets.get(type).isEmpty()) {
            bullets.get(type).remove(bullet);
        }
    }

    List<Bullet> getBullets(EntityType type) {
        return bullets.get(type);
    }

    void freezeEnemies() {
        enemiesFrozen = true;
        freezeImposedTime = System.currentTimeMillis();
    }

    void detonateEnemies() {
        for (Enemy enemy : enemyList) {
            enemy.setDead();
        }
    }

    public Integer getEnemyCount() {
        return enemyCount;
    }

    public void setGameOver() {
        gameOver = true;
    }

    public void reset() {
        bullets = new HashMap<>();
        bullets.put(EntityType.Player, new LinkedList<>());
        bullets.put(EntityType.Enemy, new LinkedList<>());
        stage = 1;
        lvl = new Level(atlas, stage, this);
        enemiesFrozen = false;
        enemyCount = 20;
        enemyList = new LinkedList<>();
        player = new Player(SCALE, PLAYER_SPEED, atlas, lvl, this);
        gameOver = false;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getOpponentScore() {
        return opponentScore;
    }
}
