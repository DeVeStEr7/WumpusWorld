package com.devinalexandre;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainScreen implements Screen {

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 600;

    //Object that allows us to draw all our graphics
    private SpriteBatch spriteBatch;

    //Object that allows us to draw shapes
    private ShapeRenderer shapeRenderer;

    //Camera to view our virtual world
    private Camera camera;

    //control how the camera views the world
    //zoom in/out? Keep everything scaled?
    private Viewport viewport;

    //Textures
    //Create the WumpusWorld object
    WumpusWorld world = new WumpusWorld(10,10);

    //Create Bob!!!!!
    Bob bob = new Bob(world);
    public static boolean runAI = false;
    int totalMoves = 0;
    public static boolean simOver = false;
    public static boolean shootMode = false;

    //create UI variables
    BitmapFont defaultFont = new BitmapFont();
    int currentSelection = -1;

    //runs one time,at the very beginning
    //all setup should happen here
    @Override
    public void show(){
        camera = new OrthographicCamera(); //2D camera
        camera.position.set(WORLD_WIDTH/2, WORLD_HEIGHT/2,0);
        camera.update();

        //freeze my view to 800x600, no matter the window size
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        spriteBatch = new SpriteBatch();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
    }

    public void clearScreen() {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }


    public void getMouseInput() {
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            int x = Gdx.input.getX();
            int y = Gdx.input.getY();

            if(currentSelection != -1) {
                world.placeTileInWorld(currentSelection,x,y);
                currentSelection = -1;
            }
            //question
            if(x >= 630 && x <= 680 && y <= 500 && y >= 450) {
                world.flipWorldVisible();
            }
            //trophy
            if(x >= 630 && x <= 680 && y <= 555 && y >= 505) {
                runAI = !runAI;
            }
            //random
            if(x >= 685 && x <= 735 && y <= 500 && y >= 450) {
                gameOver();
                System.out.print("randomize");
                world.randomBoard();
            }
            //bullet
            if(x >= 685 && x <= 735 && y <= 555 && y >= 505) {
                System.out.print("Ready to Shoot");
                shootingRange();
                //shootMode = !shootMode;
            }
            //ground
            else if(x >= 630 && x < 680 && y <= 180 && y >= 130) {
                currentSelection = world.GROUND;
            }
            //spider
            else if(x >= 630 && x < 680 && y <= 235 && y >= 185) {
                currentSelection = world.SPIDER;
            }
            else if(x >= 630 && x < 680 && y <= 290 && y >= 240) {
                currentSelection = world.PIT;
            }
            else if(x >= 630 && x < 680 && y <= 345 && y >= 295) {
                currentSelection = world.WUMPUS;
            }
            else if(x >= 630 && x < 680 && y <= 400 && y >= 350) {
                currentSelection = world.GOLD;
            }
        }
    }

    public void getKeyboardInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            bob.moveUp();
            totalMoves++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            bob.moveLeft();
            totalMoves++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            bob.moveDown();
            totalMoves++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            bob.moveRight();
            totalMoves++;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            shootingRange();
        }
    }

    public void drawToolbar() {
        defaultFont.draw(spriteBatch,"Toolbar", 630,500);
        defaultFont.draw(spriteBatch,"Total Moves: " + totalMoves, 630,550);

        spriteBatch.draw(world.getGroundTile(),630,420);
        spriteBatch.draw(world.getSpiderTile(), 630, 365);
        spriteBatch.draw(world.getPitTile(), 630, 310);
        spriteBatch.draw(world.getWumpusTile(), 630, 255);
        spriteBatch.draw(world.getGoldTile(), 630, 200);
        spriteBatch.draw(world.getQuestion(), 630, 100);
        spriteBatch.draw(world.getTrophy(), 630, 45);
        spriteBatch.draw(world.getRandom(), 685, 100);
        spriteBatch.draw(world.getBullet(), 685, 45);

        if(currentSelection != -1) {
            spriteBatch.draw(world.getTextureByID(currentSelection), Gdx.input.getX()-25, 575-Gdx.input.getY());
        }
    }

    public void gameOver() {
        world = new WumpusWorld(10,10);
        bob = new Bob(world);
        totalMoves = 0;
    }

    //this method runs as fast as it can,repeatedly,constantly looped
    @Override
    public void render(float delta) {
        clearScreen();

        //all drawing of shapes MUST be in between begin/end
        shapeRenderer.begin();
        shapeRenderer.end();

        //all drawing of graphics MUST be in between begin/end
        spriteBatch.begin();
        world.draw(spriteBatch);
        bob.draw(spriteBatch);
        drawToolbar();

/*
        if(shootMode) {
            shootingRange();
            shootMode = !shootMode;
        }

 */
        if(!simOver) {
            getKeyboardInput();
            getMouseInput();
            if(world.isLocDeath(new Location(bob.getRow(), bob.getCol()))) {
                simOver = !simOver;
            }
            if(world.isGoldTile(new Location(bob.getRow(), bob.getCol()))) {
                bob.winner = true;
                defaultFont.draw(spriteBatch, "Obtained Gold", 200,575);
            }
            if(bob.winner) {
                defaultFont.draw(spriteBatch, "Obtained Gold", 200,575);
                if(bob.getCol() == 0 && bob.getRow() == 9)
                    simOver = !simOver;
            }
        }
        if (simOver) {
            if(bob.winner) {
                defaultFont.draw(spriteBatch, "You have escaped with the gold", 200,300);
                defaultFont.draw(spriteBatch, "Congratulations", 260,275);
            }
            else {
                defaultFont.draw(spriteBatch, "Game Over", 200, 300);
                defaultFont.draw(spriteBatch, "Click to Restart", 185, 275);
            }
            runAI = false;
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                gameOver();
                simOver = !simOver;
                bob.RUNBACK = false;
            }
        }
        if(runAI) {
            bob.runAI();
            totalMoves++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        spriteBatch.end();
    }

    public void shootingRange() {
        System.out.print("hi");
        int count = 0;
        for (int i = bob.getRow(); i > 0; i--) {
            if (world.getTileId(i-1, bob.getCol()) == WumpusWorld.WUMPUS) {
                world.placeTile(0, i-1,bob.getCol());
            }
                    //System.out.print("shoot up");
        }
                //System.exit(1);
                //totalMoves++;

            /*
            else if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                world.setVisible(bob.getRow(), bob.getCol() - 1);
                System.out.print("shoot left");
                 notpressed = false;
                //System.exit(1);
                //totalMoves++;
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                world.setVisible(bob.getRow() + 1, bob.getCol());
                System.out.print("shoot down");
                notpressed = false;
               // System.exit(1);
                // totalMoves++;
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                world.setVisible(bob.getRow(), bob.getCol() + 1);
                System.out.print("shoot right");
                notpressed = false;
                //System.exit(1);
                // totalMoves++;
            }
            /*
            else {
                System.out.println("Ain't nothing being pressed!");
            }

             */

    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }


    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
        shapeRenderer.dispose();
    }
}
