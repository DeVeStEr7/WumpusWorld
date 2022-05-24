package com.devinalexandre;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


import javax.swing.text.html.MinimalHTMLWriter;
import java.util.ArrayList;
import java.util.Stack;

public class Bob {
    private int row;
    private int col;
    private Texture texture;
    private Stack<Location> list;
    private WumpusWorld myWorld;
    private int warningZone[][];

    public boolean RUNBACK = false;
    public boolean winner = false;
    private boolean unseenLocation = false;
    private boolean stuck = false;
    private boolean failedtest = false;
    private Location stuckLoc = null;

    public Bob(WumpusWorld myWorld) {
        this.myWorld = myWorld;
        row = 9;
        col = 0;
        texture = new Texture("guy.png");
        list = new Stack<Location>();
        list.push(new Location(row,col));
        this.warningZone = new int[myWorld.getNumRows()][myWorld.getNumCols()];
        warningZone[row][col] = 1;
        updateVisibilityLoc();
    }

    public Bob(int row, int col, WumpusWorld myWorld) {
        this.myWorld = myWorld;
        this.row = row;
        this.col = col;
        updateVisibilityLoc();
    }

    //make one move
    public void step() {
        Location newLoc = chooseRandomMove();
        row = newLoc.getRow();
        col = newLoc.getCol();
        myWorld.setVisible(row,col);
        if(myWorld.isLocDeath(newLoc)) {
            MainScreen.simOver = true;
            MainScreen.runAI = false;
        }
    }



    public void runAI() {
        Location above = new Location(row - 1, col);
        Location below = new Location(row + 1, col);
        Location left = new Location(row, col - 1);
        Location right = new Location(row, col + 1);
        //move back code
        if (RUNBACK) {
            if (row == 9 && col == 0) {
                MainScreen.simOver = true;
                MainScreen.runAI = false;
                return;
            }
            else {
                Location loc = pathFinderUse(warningZone);
                row = loc.getRow();
                col = loc.getCol();
                warningZone[row][col] = 2;
            }
            printBoard(warningZone);
            return;
        }
        //Glitter testing to Gold
        if(myWorld.isGlitterTile(new Location(row,col))) {
            System.out.print("Glitter");

            if(myWorld.isLocValid(above) && hasBeenSeen(above)) {
                row = above.getRow();
                col = above.getCol();
                list.push(above);
            }
            else if(myWorld.isLocValid(right) && hasBeenSeen(right)) {
                row = right.getRow();
                col = right.getCol();
                list.push(right);
            }
            else if(myWorld.isLocValid(below) && hasBeenSeen(below)) {
                row = below.getRow();
                col = below.getCol();
                list.push(below);
            }
            else if(myWorld.isLocValid(left) && hasBeenSeen(left)) {
                row = left.getRow();
                col = left.getCol();
                list.push(left);
            }
            if (myWorld.isGoldTile(new Location(row,col))) {
                RUNBACK = !RUNBACK;
                winner = true;
                clearLostPath(warningZone);
                warningZone[row][col] = 10;
            }
            else {
               failedtest = true;
            }
            myWorld.setVisible(row,col);
            return;
        }

        if(failedtest) {
            list.pop();
            row = list.peek().getRow();
            col = list.peek().getCol();
            failedtest = false;
            return;
        }

        //follow black zone
        if (!myWorld.warningTile(new Location(row, col))) {
            if (!stuck) {
                System.out.println("on a mission");
                if (myWorld.isLocValid(above) && hasBeenSeen(above) && !ifWarningTile(above)) {
                    goToNextLoc(above);
                } else if (myWorld.isLocValid(right) && hasBeenSeen(right) && !ifWarningTile(right)) {
                    goToNextLoc(right);
                } else if (myWorld.isLocValid(below) && hasBeenSeen(below) && !ifWarningTile(below)) {
                    goToNextLoc(below);
                } else if (myWorld.isLocValid(left) && hasBeenSeen(left) && !ifWarningTile(left)) {
                    goToNextLoc(left);
                } else {
                    goToNextLoc(chooseRandomMove());
                    stuck = true;
                }
            } else {
                if (stuckLoc == null) {
                    stuckLoc = chooseRandomFoundBorderMove();
                    warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 8;
                    //stuckLoc = chooseBlackMove();
                } else if (stuckLoc.getCol() == col && stuckLoc.getRow() == row) {
                    System.out.println("arrived");
                    clearLostPath(warningZone);
                    warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 1;
                    stuck = false;
                    stuckLoc = null;
                } else {
                    Location findPath = (new Location(0,0));
                    if (myWorld.isLocValid(above) && hasBeenSeen(above) && !ifWarningTile(above)) {
                        findPath = above;
                        warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 1;
                        stuck = false;
                    } else if (myWorld.isLocValid(right) && hasBeenSeen(right) && !ifWarningTile(right)) {
                        findPath = right;
                        warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 1;
                        stuck = false;
                    } else if (myWorld.isLocValid(below) && hasBeenSeen(below) && !ifWarningTile(below)) {
                        findPath = below;
                        warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 1;
                        stuck = false;
                    } else if (myWorld.isLocValid(left) && hasBeenSeen(left) && !ifWarningTile(left)) {
                        findPath = left;
                        warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 1;
                        stuck = false;
                    }
                    else {
                        findPath = pathFinderUseAdvanced(warningZone, stuckLoc);
                        warningZone[stuckLoc.getRow()][stuckLoc.getCol()] = 8;
                    }
                    row = findPath.getRow();
                    col = findPath.getCol();
                    warningZone[row][col] = 2;
                    myWorld.setVisible(row, col);
                    list.push(findPath);
                }

            }
        }
        //move back code and create warning squares
        else {
            warningZone[row][col] = 5;
            list.pop();
            row = list.peek().getRow();
            col = list.peek().getCol();
            if (dangerArea(above) && warningZone[above.getRow()][above.getCol()] != 1) {
                warningZone[above.getRow()][above.getCol()] = 9;
            }
            else if (dangerArea(below) && warningZone[below.getRow()][below.getCol()] != 1) {
                warningZone[below.getRow()][below.getCol()] = 9;
            }
            else if (dangerArea(right) && warningZone[right.getRow()][right.getCol()] != 1) {
                warningZone[right.getRow()][right.getCol()] = 9;
            }
            else if (dangerArea(left) && warningZone[left.getRow()][left.getCol()] != 1) {
                warningZone[left.getRow()][left.getCol()] = 9;
            }
        }
        myWorld.setVisible(row,col);
        printBoard(warningZone);
        checkPossibleEnemies();
        System.out.println("");
        System.out.print(list.size());
    }

//board functions
    private void clearLostPath(int[][] arr) {
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < arr[i].length; j++) {
                if(warningZone[i][j] == 2) {
                    warningZone[i][j] = 1;
                }
            }
        }
    }
    public void printBoard(int[][] arr) {
        for(int i = 0; i < arr.length; i++) {
            for(int j = 0; j < arr[i].length; j++) {
                System.out.print(arr[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    //small boolean functions
    public boolean previousTile(Location loc) {
        return loc.getRow() == row && loc.getCol() == col;
    }
    public boolean hasBeenSeen(Location loc) {                  //follow black tiles to find more of the cave
        return !myWorld.getVisible(loc.getRow(), loc.getCol());
    }

    //enemy functions
    public boolean dangerArea(Location loc) {
        return myWorld.isLocValid(loc) && !previousTile(loc);
    }
    public boolean ifWarningTile(Location loc) {
        return warningZone[loc.getRow()][loc.getCol()] == 5;
    }
    private void checkPossibleEnemies() {
        for(int i = 0; i < warningZone.length; i++) {
            for(int j = 0; j < warningZone[i].length; j++) {
                Location above = new Location(i - 1, j);
                Location below = new Location(i + 1, j);
                Location left = new Location(i, j - 1);
                Location right = new Location(i, j + 1);
                //if(warningZone[i][j] == 9 && !((myWorld.isLocValid(above) && warningZone[above.getRow()][above.getCol()] == 5) && (myWorld.isLocValid(below) && warningZone[below.getRow()][below.getCol()] == 5) && (myWorld.isLocValid(right) && warningZone[right.getRow()][right.getCol()] == 5) && (myWorld.isLocValid(left) && warningZone[left.getRow()][left.getCol()] == 5))) {
                // warningZone[i][j] = 0;
                // }
            }
        }
    }

    public void goToNextLoc(Location loc) {
        row = loc.getRow();
        col = loc.getCol();
        myWorld.setVisible(row, col);
        warningZone[row][col] = 1;
        list.push(loc);
    }



    public void updateVisibilityLoc() {
        myWorld.setVisible(this.row,this.col);
    }

    public void draw(SpriteBatch spriteBatch) {
        spriteBatch.draw(texture, col*50+5, 500-(row*50));
    }

    public void moveRight() {
        if(col+1 < myWorld.getNumCols()) {
            col++;
            updateVisibilityLoc();
        }
    }

    public void moveLeft() {
        if(col-1 >= 0) {
            col--;
            updateVisibilityLoc();
        }
    }

    public void moveUp() {
        if(row-1 >= 0) {
            row--;
            updateVisibilityLoc();
        }
    }

    public void moveDown() {
        if(row+1 < myWorld.getNumRows()) {
            row++;
            updateVisibilityLoc();
        }
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

//all choosing functions
    public Location chooseRandomMove() {
        ArrayList<Location> possibleLocs = new ArrayList<>(4);
        Location above = new Location(row-1, col);
        Location below = new Location(row+1, col);
        Location left = new Location(row, col-1);
        Location right = new Location(row, col+1);

        if(myWorld.isLocValid(above) && !ifWarningTile(above))
            possibleLocs.add(above);
        if(myWorld.isLocValid(below) && !ifWarningTile(below))
            possibleLocs.add(below);
        if(myWorld.isLocValid(left) && !ifWarningTile(left))
            possibleLocs.add(left);
        if(myWorld.isLocValid(right) && !ifWarningTile(right))
            possibleLocs.add(right);

        return possibleLocs.get((int)(Math.random() * possibleLocs.size()));
    }

    private Location chooseRandomFoundBorderMove() {
        Location borderLoc = new Location(9,0);
        Location above = new Location(borderLoc.getRow() - 1, borderLoc.getCol());
        Location below = new Location(borderLoc.getRow() + 1, borderLoc.getCol());
        Location left = new Location(borderLoc.getRow(), borderLoc.getCol() - 1);
        Location right = new Location(borderLoc.getRow(), borderLoc.getCol() + 1);
        boolean failedBorder = true;

        while (failedBorder){  // !blackCheck(borderLoc)) {
            borderLoc = chooseRandomMoveOnWorld();
            System.out.print(borderLoc);
            if(warningZone[borderLoc.getRow()][borderLoc.getCol()] == 1) {

                if (myWorld.isLocValid(above) && warningZone[above.getRow()][above.getCol()] == 0 ) {
                    System.out.print("peek a boo1");
                    failedBorder = false;
                } else if (myWorld.isLocValid(below) && warningZone[below.getRow()][below.getCol()] == 0 ) {
                    System.out.print("peek a boo2");
                    failedBorder = false;
                } else if (myWorld.isLocValid(right) && warningZone[right.getRow()][right.getCol()] == 0 ) {
                    System.out.print("peek a boo3");
                    failedBorder = false;
                } else if (myWorld.isLocValid(left) && warningZone[left.getRow()][left.getCol()] == 0) {
                    System.out.print("peek a boo4");
                    failedBorder = false;
                } else {
                    System.out.print("try again");
                    borderLoc = chooseRandomMoveOnWorld();
                }
            }
        }
        System.out.println(borderLoc);
        /*
        while ((warningZone[borderLoc.getRow()][borderLoc.getCol()] != 1) && failedBorder){  // !blackCheck(borderLoc)) {
            System.out.print(borderLoc);
            if(!((myWorld.isLocValid(above) && hasBeenSeen(above)) || (myWorld.isLocValid(below) && hasBeenSeen(below)) || (myWorld.isLocValid(right) && hasBeenSeen(right)) || (myWorld.isLocValid(left) && hasBeenSeen(left)))) {
                System.out.print("peek a boo");
                failedBorder = false;
            }
            else
                System.out.print("try again");
                borderLoc = chooseRandomMoveOnWorld();
        }

         */

        //warningZone[borderLoc.getRow()][borderLoc.getCol()] =7;
        return borderLoc;
    }

    private Location chooseRandomMoveOnWorld() {
        Location loc =  new Location((int)(Math.random() * myWorld.getNumRows()),(int)(Math.random() * myWorld.getNumCols()));
        if(warningZone[loc.getRow()][loc.getCol()] != 1) {
            loc = new Location((int)(Math.random() * myWorld.getNumRows()),(int)(Math.random() * myWorld.getNumCols()));
        }
        return loc;
    }


    //path finder methods
    public Location pathFinderUse(int[][] arr) {
        Location above = new Location(row - 1, col);
        Location below = new Location(row + 1, col);
        Location left = new Location(row, col - 1);
        Location right = new Location(row, col + 1);

        if (myWorld.isLocValid(below) && warningZone[below.getRow()][below.getCol()] == 1) {
            return below;
        }
        else if (myWorld.isLocValid(left) && warningZone[left.getRow()][left.getCol()] == 1) {
            return left;
        }
        else if (myWorld.isLocValid(above) && warningZone[above.getRow()][above.getCol()] == 1) {
            return above;
        }
        else if (myWorld.isLocValid(right) && warningZone[right.getRow()][right.getCol()] == 1) {
            return right;
        }
        else {
            Location loc = chooseRandomMove();
            while(warningZone[loc.getRow()][loc.getCol()] != 0) {
                loc = chooseRandomMove();
            }
            return loc;
        }

    }

    public Location pathFinderUseAdvanced(int[][] arr, Location endMove) {
        System.out.println("Goal: " + endMove);
        //Location loc = pathFinderUse(arr,endMove);
        Location above = new Location(row - 1, col);
        Location below = new Location(row + 1, col);
        Location left = new Location(row, col - 1);
        Location right = new Location(row, col + 1);
/*
        if (myWorld.isLocValid(below) && warningZone[below.getRow()][below.getCol()] == 1) {
            return below;
        }
        else if (myWorld.isLocValid(left) && warningZone[left.getRow()][left.getCol()] == 1) {
            return left;
        }
        else if (myWorld.isLocValid(above) && warningZone[above.getRow()][above.getCol()] == 1) {
            return above;
        }
        else if (myWorld.isLocValid(right) && warningZone[right.getRow()][right.getCol()] == 1) {
            return right;
        }
        else {
            return new Location(row,col);
        }
        /*
        if(blackCheck(new Location(row,col))) {
            return blackMove(new Location(row,col));
        }

         */

        // if(row == endMove.getRow()) {
        if (myWorld.isLocValid(left) && warningZone[left.getRow()][left.getCol()] == 1 && endMove.getCol() < getCol())
            return left;

        else if (myWorld.isLocValid(above) && warningZone[above.getRow()][above.getCol()] == 1 && endMove.getRow() < getRow())
            return above;

        else if((myWorld.isLocValid(right) && warningZone[right.getRow()][right.getCol()] == 1 && endMove.getCol() > getCol()))
            return right;

        else if((myWorld.isLocValid(below) && warningZone[below.getRow()][below.getCol()] == 1 && (endMove.getRow() > getRow())))
            return below;
        else
            //return new Location(row,col);
            return chooseRandomMove();
    }

}
