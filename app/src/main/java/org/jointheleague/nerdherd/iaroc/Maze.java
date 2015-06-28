package org.jointheleague.nerdherd.iaroc;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

/**
 * Created by RussB on 6/24/15.
 */
public class Maze implements DistanceSensorListener, TurnEndHandler, BumpListener {
    private static final int MAZE_WALL_DISTANCE = 20;
    protected MazeFunctions mazeFunctions;
    WallHugger wallHugger;
    boolean wallLeft;
    boolean wallRight;
    boolean wallFront;
    protected Dashboard dashboard;
    private boolean turning = false;
    private boolean firstCall = false;

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.wallHugger = new WallHugger(dashboard, this);
        dashboard.getBrain().registerDistanceListener(this);
        dashboard.getBrain().registerBumpListener(this);
        mazeFunctions = new MazeFunctions(dashboard, this);
    }

    public void solve() {

    }

    public void distanceListener(int leftDistance, int rightDistance, boolean isBumpLeft, boolean isBumpRight) {
        boolean actionNeeded = false;
        if (turning) {
            return;
        }
        if (firstCall) {
            wallLeft = leftDistance < MAZE_WALL_DISTANCE;
            wallRight = rightDistance < MAZE_WALL_DISTANCE;
            wallFront = false;
            firstCall = true;
            actionNeeded = true;
        }
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

        if (actionNeeded) {
            //dashboard.log("Do action");
            turning = wallHugger.rightWallHugger(this);
            if (turning) {
                //dashboard.log("Turning");
            }
        }

        //dashboard.log("Wall left: " + wallLeft + "  Wall right: " + wallRight + "  Wall front: " + wallFront);
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

    @Override
    public void onAnyBump(boolean left, boolean right) {
        // DON'T DO ANYTHING #ALT+SHIFT+K
    }

    @Override
    public void onRightBump() {
        dashboard.getBrain().driveBackwards(250);
        SystemClock.sleep(1000);
        dashboard.getBrain().stop();
        turning = true;
        dashboard.getBrain().leftTurn(45, 0, new TurnEndHandler() {
            // TODO have everyone upgrade to java 8 and make this a lambda.
            @Override
            public void onTurnEnd() {
                dashboard.log("Turn Done");
                Maze.this.turning = false;
                dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
            }
        });
    }

    @Override
    public void onLeftBump() {
        dashboard.getBrain().driveBackwards(250);
        SystemClock.sleep(1000);
        dashboard.getBrain().stop();
        turning = true;
        dashboard.getBrain().rightTurn(45, 0, new TurnEndHandler() {
            // TODO have everyone upgrade to java 8 and make this a lambda.
            @Override
            public void onTurnEnd() {
                dashboard.log("Turn Done");
                Maze.this.turning = false;
                dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
            }
        });
    }

    @Override
    public void onFrontBump() {
        // DON'T DO ANYTHING #ALT+SHIFT+K
    }
}
