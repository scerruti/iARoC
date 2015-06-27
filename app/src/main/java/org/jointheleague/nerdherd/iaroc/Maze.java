package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

/**
 * Created by RussB on 6/24/15.
 */
public class Maze implements DistanceSensorListener, LoopAction, TurnEndHandler {
    private static final int MAZE_WALL_DISTANCE = 20;
    protected MazeFunctions mazeFunctions;
    WallHugger wallHugger;
    boolean wallLeft;
    boolean wallRight;
    boolean wallFront;
    boolean isWallDataValid = false;
    protected Dashboard dashboard;
    private boolean turning = false;

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.wallHugger = new WallHugger(dashboard, this);
        dashboard.getBrain().registerDistanceListener(this);
        mazeFunctions = new MazeFunctions(dashboard, this);
        dashboard.getBrain().registerLoopAction(this);
    }

    public void solve() {

    }

    public void distanceListener(int leftDistance, int rightDistance, boolean isBumpLeft, boolean isBumpRight) {
        if (turning) {
            return;
        }
        boolean actionNeeded = false;
        if (wallLeft && leftDistance > MAZE_WALL_DISTANCE) {
            wallLeft = false;
            actionNeeded = true;
        }
        else if(!wallLeft && leftDistance < MAZE_WALL_DISTANCE) {
            wallLeft = true;
            actionNeeded = true;
        }

        if (wallRight && rightDistance > MAZE_WALL_DISTANCE) {
            wallRight = false;
            actionNeeded = true;
        }
        else if(!wallRight && rightDistance < MAZE_WALL_DISTANCE) {
            wallRight = true;
            actionNeeded = true;
        }

        if (isBumpLeft && isBumpRight) {
            wallFront = true;
            actionNeeded = true;
        }
        else {
            wallFront = false;
        }

        if (actionNeeded && isWallDataValid) {
            doAction();
        }
        isWallDataValid = true;

        dashboard.log("Wall left: " + wallLeft + "  Wall right: " + wallRight + "  Wall front: " + wallFront);
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
        return wallLeft;
    }

    public void setIsWallLeft(boolean isWallLeft) {
        this.wallLeft = isWallLeft;
    }

    public boolean isWallRight() {
        return wallRight;
    }

    public void setIsWallRight(boolean isWallRight) {
        this.wallRight = isWallRight;
    }

    public boolean isWallFront() {
        return wallFront;
    }

    public void setIsWallFront(boolean isWallFront) {
        this.wallFront = isWallFront;
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
