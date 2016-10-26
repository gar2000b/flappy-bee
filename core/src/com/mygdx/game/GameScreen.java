package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Digilogue on 24/10/2016.
 */
public class GameScreen extends ScreenAdapter {

    private static final float WORLD_WIDTH = 480;
    private static final float WORLD_HEIGHT = 640;
    private static final float GAP_BETWEEN_FLOWERS = 200F;

    private ShapeRenderer shapeRenderer;
    private Viewport viewport;
    private Camera camera;
    private SpriteBatch batch;
    private Flappee flappee;
    // private Flower flower = new Flower();
    private Array<Flower> flowers = new Array<Flower>();
    private BitmapFont bitmapFont;
    private GlyphLayout glyphLayout;
    private Texture background;
    private Texture flowerBottom;
    private Texture flowerTop;
    private Texture flappeeTexture;

    private int score = 0;

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        flappeeTexture = new Texture(Gdx.files.internal("bee.png"));
        flappee = new Flappee(flappeeTexture);
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        // flower.setPosition(40);
        bitmapFont = new BitmapFont();
        glyphLayout = new GlyphLayout();
        background = new Texture(Gdx.files.internal("bg.png"));
        flowerBottom = new Texture(Gdx.files.internal("flowerBottom.png"));
        flowerTop = new Texture(Gdx.files.internal("flowerTop.png"));
    }

    @Override
    public void render(float delta) {
        clearScreen();
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // flappee.drawDebug(shapeRenderer);
        // flower.drawDebug(shapeRenderer);
        shapeRenderer.end();

        update(delta);
        draw();
        // drawDebug();
    }

    private void update(float delta) {
        updateFlappee(delta);
        updateFlowers(delta);
        updateScore();
        if (checkForCollision()) {
            restart();
        }
    }

    private void updateFlappee(float delta) {
        flappee.update(delta);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) flappee.flyUp();
        blockFlappeeLeavingTheWorld();
    }

    private void restart() {
        flappee.setPosition(WORLD_WIDTH / 4, WORLD_HEIGHT / 2);
        flowers.clear();
        score = 0;
    }

    private void updateFlowers(float delta) {
        for (Flower flower : flowers) {
            flower.update(delta);
        }
        checkIfNewFlowerIsNeeded();
        removeFlowersIfPassed();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void blockFlappeeLeavingTheWorld() {
        flappee.setPosition(flappee.getX(), MathUtils.clamp(flappee.getY(), 0, WORLD_HEIGHT));
    }

    private void createNewFlower() {
        Flower newFlower = new Flower(flowerBottom, flowerTop);
        newFlower.setPosition(WORLD_WIDTH + Flower.WIDTH);
        flowers.add(newFlower);
    }

    private void checkIfNewFlowerIsNeeded() {
        if (flowers.size == 0) {
            createNewFlower();
        } else {
            Flower flower = flowers.peek();
            if (flower.getX() < WORLD_WIDTH - GAP_BETWEEN_FLOWERS) {
                createNewFlower();
            }
        }
    }

    private void removeFlowersIfPassed() {
        if (flowers.size > 0) {
            Flower firstFlower = flowers.first();
            if (firstFlower.getX() < -Flower.WIDTH) {
                flowers.removeValue(firstFlower, true);
            }
        }
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Flower flower : flowers) {
            flower.drawDebug(shapeRenderer);
        }
        flappee.drawDebug(shapeRenderer);
        shapeRenderer.end();
    }

    private boolean checkForCollision() {
        for (Flower flower : flowers) {
            if (flower.isFlappeeColliding(flappee)) {
                return true;
            }
        }
        return false;
    }

    private void updateScore() {
        Flower flower = flowers.first();
        if (flower.getX() < flappee.getX() && !flower.isPointClaimed()) {
            flower.markPointClaimed();
            score++;
        }
    }

    private void drawScore() {
        String scoreAsString = Integer.toString(score);
        bitmapFont.getData().setScale(2);
        glyphLayout.setText(bitmapFont, scoreAsString);
        bitmapFont.draw(batch, scoreAsString, (viewport.getWorldWidth() - glyphLayout.width) / 2, (4 * viewport
                .getWorldHeight() / 5) - glyphLayout.height / 2);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        batch.begin();
        batch.draw(background, 0, 0);
        drawFlowers();
        flappee.draw(batch);
        drawScore();
        batch.end();
    }

    private void drawFlowers() {
        for (Flower flower : flowers) {
            flower.draw(batch);
        }
    }
}
