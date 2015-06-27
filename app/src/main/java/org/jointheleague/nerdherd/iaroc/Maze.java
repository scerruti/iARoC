package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

/**
 * Created by RussB on 6/24/15.
 */
public class Maze implements DistanceSensorListener, LoopAction, TurnEndHandler {
    private static final int MAZE_WALL_DISTANCE = 20;
    protected MazeFunctions mazeFunctions;
    WallHugger wallHugger;
    boolean isWallLeft;
    boolean isWallRight;
    boolean isWallFront;
    boolean isWallDataValid = false;
    protected Dashboard dashboard;
    private boolean turning = false;

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.wallHugger = new WallHugger(dashboard, this);
        dashboard.getBrain().registerSideDistanceListener(this);
        mazeFunctions = new MazeFunctions(dashboard);
        dashboard.getBrain().registerLoopAction(this);
    }

    public void frontDistanceListener(boolean isBumpLeft, boolean isBumpRight) {

    }

    @Override
    public void leftDistanceListener(int leftDistance) {

    }

    @Override
    public void rightDistanceListener(int rightDistance) {

    }

    public void sideDistanceListener(int leftDistance, int rightDistance) {
        if (turning) {
            return;
        }
        boolean actionNeeded = false;
        if (isWallLeft && leftDistance > MAZE_WALL_DISTANCE) {
            isWallLeft = false;
            actionNeeded = true;
        }
        else if(!isWallLeft && leftDistance < MAZE_WALL_DISTANCE) {
            isWallLeft = true;
            actionNeeded = true;
        }

        if (isWallRight && rightDistance > MAZE_WALL_DISTANCE) {
            isWallRight = false;
            actionNeeded = true;
        }
        else if(!isWallRight && rightDistance < MAZE_WALL_DISTANCE) {
            isWallRight = true;
            actionNeeded = true;
        }

        if (actionNeeded && isWallDataValid) {
            doAction();
        }
        isWallDataValid = true;
    }

    @Override
    public void doAction() {
        dashboard.log("Do action");
        turning = wallHugger.rightWallHugger(this);
        if (turning) {
            dashboard.log("Turning");
            isWallDataValid = false;
        }
    }

    @Override
    public void onTurnEnd() {
        turning = false;
    }

    public boolean isWallLeft() {
        return isWallLeft;
    }

    public void setIsWallLeft(boolean isWallLeft) {
        this.isWallLeft = isWallLeft;
    }

    public boolean isWallRight() {
        return isWallRight;
    }

    public void setIsWallRight(boolean isWallRight) {
        this.isWallRight = isWallRight;
    }

    public boolean isWallFront() {
        return isWallFront;
    }

    public void setIsWallFront(boolean isWallFront) {
        this.isWallFront = isWallFront;
    }

    public boolean isWallDataValid() {
        return isWallDataValid;
    }

    public void setIsWallDataValid(boolean isWallDataValid) {
        this.isWallDataValid = isWallDataValid;
    }

    public void solve() {
    }
}
