package com.devinalexandre;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.w3c.dom.Text;


import java.awt.geom.Point2D;
import java.util.ArrayList;

public class WumpusWorld {
    private int world[][];
    private boolean visible[][];

    private Texture groundTile;
    private Texture blackTile;
    private Texture glitterTile;
    private Texture goldTile;
    private Texture pitTile;
    private Texture question;
    private Texture spiderTile;
    private Texture stinkTile;
    private Texture trophy;
    private Texture webTile;
    private Texture windTile;
    private Texture wumpusTile;


    private Texture random;
    private Texture bullet;

    private boolean hideWorld = true;

    public static final int GROUND=0, SPIDER=2, PIT =3, WUMPUS=4, GOLD=5,
                                      WEB=12, BREEZE=13,STINK=14,GLITTER=15;

    public WumpusWorld(int row, int col) {
        world = new int[row][col];
        visible = new boolean[row][col];

        groundTile = new Texture("groundTile.png");
        blackTile = new Texture("blackTile.png");
        glitterTile = new Texture("glitterTile.png");
        goldTile = new Texture("goldTile.png");
        pitTile = new Texture("pitTile.png");
        question = new Texture("question.png");
        spiderTile = new Texture("spiderTile.png");
        stinkTile = new Texture("stinkTile.png");
        trophy = new Texture("trophy.png");
        webTile = new Texture("webTile.png");
        windTile = new Texture("windTile.png");
        wumpusTile = new Texture("wumpusTile.png");

        random = new Texture("random.png");
        bullet = new Texture("bullet.png");
    }

    public int getNumCols() {
        return world[0].length;
    }

    public int getNumRows() {
        return world.length;
    }

    public void setVisible(int row, int col) {
        visible[row][col] = true;
    }

    public boolean getVisible(int row, int col) {
        return visible[row][col];
    }

    public boolean isLocDeath(Location loc) {
        return world[loc.getRow()][loc.getCol()] >= 1 && world[loc.getRow()][loc.getCol()] <= 4;

    }
    public boolean warningTile(Location loc) {
        return world[loc.getRow()][loc.getCol()] >= 11 && world[loc.getRow()][loc.getCol()] <= 14;
    }
    public boolean isGlitterTile(Location loc) {
        return world[loc.getRow()][loc.getCol()] == GLITTER;
    }
    public boolean isGoldTile(Location loc) {
        return world[loc.getRow()][loc.getCol()] == GOLD;
    }

    public void flipWorldVisible() {
        hideWorld = !hideWorld;
    }


    /*
    tileID -> type of main tile that was placed (PIT,SPIDER,GOLD,WUMPUS,GROUND)
    loc -> the location of the main tileID
    result will be: above, below, left, and right of main tile will be the hints
     */
    public void placeTileHints(int tileID, Location loc) {
        Location above = new Location(loc.getRow()-1, loc.getCol());
        Location below = new Location(loc.getRow()+1, loc.getCol());
        Location left = new Location(loc.getRow(), loc.getCol()-1);
        Location right = new Location(loc.getRow(), loc.getCol()+1);

        if(isLocValid(above)) {
            world[above.getRow()][above.getCol()] = tileID+10;
        }
        if(isLocValid(below)) {
            world[below.getRow()][below.getCol()] = tileID+10;
        }
        if(isLocValid(left)) {
            world[left.getRow()][left.getCol()] = tileID+10;
        }
        if(isLocValid(right)) {
            world[right.getRow()][right.getCol()] = tileID+10;
        }
    }

    public boolean isLocValid(Location loc) {
        return loc.getRow() >= 0 && loc.getRow() < world.length && loc.getCol() >= 0 && loc.getCol() < world[0].length;
    }
    public Texture getTextureByID(int id) {
        switch (id) {
            case 0:
                return groundTile;
            case 2:
                return spiderTile;
            case 3:
                return pitTile;
            case 4:
                return wumpusTile;
            case 5:
                return goldTile;
            case 10:
                return groundTile;
            case 12:
                return webTile;
            case 13:
                return windTile;
            case 14:
                return stinkTile;
            case 15:
                return glitterTile;
            default:
                return null;
        }
    }

    //(x,y) are screen coordinates, need to translate these to row, col of the 2D world
    public void placeTileInWorld(int tileID, int x, int y) {
        Location loc = mouseToWorldCoordinates(x,y);
        world[loc.getRow()][loc.getCol()] = tileID;

        placeTileHints(tileID,loc);
    }
    public void placeTile(int tileID, int x, int y) {
        Location loc = new Location(x,y);
        world[loc.getRow()][loc.getCol()] = tileID;

        placeTileHints(tileID,loc);
    }

    public void placeTileInWorldUsingWorld(int tileID, int x, int y) {
        Location loc = new Location(x, y);

        boolean possibleTile = true;

        while (possibleTile) {
            ArrayList<Location> locList = new ArrayList<>();
            Location aboveLoc = new Location(loc.getRow() - 1, loc.getCol());
            Location belowLoc = new Location(loc.getRow() + 1, loc.getCol());
            Location leftLoc = new Location(loc.getRow(), loc.getCol() - 1);
            Location rightLoc = new Location(loc.getRow(), loc.getCol() + 1);
            if (world[loc.getRow()][loc.getCol()] == 0) {
                if (isLocValid(aboveLoc)) {
                    locList.add(aboveLoc);
                }
                if (isLocValid(belowLoc)) {
                    locList.add(belowLoc);
                }
                if (isLocValid(leftLoc)) {
                    locList.add(leftLoc);
                }
                if (isLocValid(rightLoc)) {
                    locList.add(rightLoc);
                }
                for (int i = 0; i < locList.size(); i++) {
                    if (world[locList.get(i).getRow()][locList.get(i).getCol()] == 0 && (locList.get(i).getRow() < 8 || locList.get(i).getCol() > 1)) {
                        possibleTile = false;
                    }
                    else {
                        possibleTile = true;
                        System.out.println("try again1");
                        i = 6;
                        loc = new Location((int)(Math.random() * world.length), (int)(Math.random() * world.length));
                    }
                }
            }
            else {
                loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
            }
        }
        System.out.print(" " + x);
        System.out.print(" " + y +",");
        world[loc.getRow()][loc.getCol()] = tileID;
        placeTileHints(tileID,loc);
    }
            /*
            if(world[loc.getRow()][loc.getCol()] != 0) {
                if(isLocValid(aboveLoc) || world[aboveLoc.getRow()][aboveLoc.getCol()] != 0) {
                    if(isLocValid(belowLoc) || world[belowLoc.getRow()][belowLoc.getCol()] != 0) {
                        if(isLocValid(leftLoc) || world[leftLoc.getRow()][leftLoc.getCol()] != 0) {
                            if(isLocValid(rightLoc) || world[rightLoc.getRow()][rightLoc.getCol()] != 0) {
                                possibleTile = false;
                            }
                            else {
                                loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
                            }
                        }
                        else {
                            loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
                        }
                    }
                    else {
                        loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
                    }
                }
                else {
                    loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
                }
            }
            else {
                loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
            }

             */

        /*


        boolean goodPlace = true;


        /*
        while((world[loc.getRow()][loc.getCol()] != 0) && goodPlace && (isLocValid(aboveLoc) || world[aboveLoc.getRow()][aboveLoc.getCol()] != 0) && (isLocValid(belowLoc) || world[belowLoc.getRow()][belowLoc.getCol()] != 0) && (isLocValid(leftLoc) || world[leftLoc.getRow()][leftLoc.getCol()] != 0) && (isLocValid(rightLoc) || world[rightLoc.getRow()][rightLoc.getCol()] != 0)) {
            boolean test = true;
            for(int i = 0; i < locList.size(); i++) {
                if(world[locList.get(i).getRow()][locList.get(i).getCol()] != 0) {
                    test = false;
                    System.out.println("try again1");
                }
            }

         */
/*
            if(test) {
                goodPlace = false;
            }
            else {
                loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));
                System.out.println("try again2" + loc);
            }

 */
            /*
            if( {
                loc = new Location((int) (Math.random() * world.length), (int) (Math.random() * world.length));

            }

             */


    public Location mouseToWorldCoordinates(int x, int y) {
        int col = x/50;
        int row = (y/50)-1;

        return new Location(row,col);
    }


    public void draw(SpriteBatch spriteBatch) {
        for(int row = 0; row < world.length; row++) {
            for(int col = 0; col < world[row].length; col++) {
                if(!visible[row][col] && hideWorld)
                    spriteBatch.draw(blackTile, col*50+5, 500-(row*50));
                else
                    spriteBatch.draw(getTextureByID(world[row][col]), col*50+5, 500-(row*50));
            }
        }
    }

    public Texture getGroundTile() {
        return groundTile;
    }

    public Texture getBlackTile() {
        return blackTile;
    }

    public Texture getGlitterTile() {
        return glitterTile;
    }

    public Texture getGoldTile() {
        return goldTile;
    }

    public Texture getPitTile() {
        return pitTile;
    }

    public Texture getQuestion() {
        return question;
    }

    public Texture getSpiderTile() {
        return spiderTile;
    }

    public Texture getStinkTile() {
        return stinkTile;
    }

    public Texture getTrophy() {
        return trophy;
    }

    public Texture getWebTile() {
        return webTile;
    }

    public Texture getWindTile() {
        return windTile;
    }

    public Texture getWumpusTile() {
        return wumpusTile;
    }

    public Texture getRandom() { return random;};

    public Texture getBullet() { return bullet;};

    public void randomBoard() {

        for(int i = 2; i <= 5; i++) {
            placeTileInWorldUsingWorld(i, (int)(Math.random() * world.length), (int)(Math.random() * world.length));
        }
        placeTileInWorldUsingWorld(2, (int)(Math.random() * world.length), (int)(Math.random() * world.length));
        placeTileInWorldUsingWorld(2, (int)(Math.random() * world.length), (int)(Math.random() * world.length));
        placeTileInWorldUsingWorld(2, (int)(Math.random() * world.length), (int)(Math.random() * world.length));


    }


    public int getTileId(int i, int col) {
        return world[i][col];
    }
}
