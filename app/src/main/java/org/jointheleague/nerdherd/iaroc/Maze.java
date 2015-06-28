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
    protected Navigator navigator;
    private boolean turning = false;
    private boolean firstCall = false;
    private Mode mode = Mode.MAP;
    public static enum Mode {
        MAP,
        SOLVE,
    }

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        mazeFunctions = new MazeFunctions(dashboard, this);
        this.navigator = new Navigator(dashboard, this, mazeFunctions);
        this.wallHugger = new WallHugger(dashboard, this, navigator);
        dashboard.getBrain().registerDistanceListener(this);
        dashboard.getBrain().registerBumpListener(this);
    }

    public void solve() {
        dashboard.getBrain().unregisterDistanceListener(this);
        navigator.copy();
        dashboard.getBrain().registerDistanceListener(this);
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
            if(dashboard.getBrain().isForceFieldVisible()) {
                victory();
            }
            actionNeeded = true;
        }
        else {
            wallFront = false;
        }

        if (actionNeeded) {
            //dashboard.log("Do action");
            if (mode.equals(Mode.MAP)) {
                turning = wallHugger.rightWallHugger(this);
            } else {
                turning = navigator.doNextMove(this);
            }
            if (turning) {
                //dashboard.log("Turning");
            }
        }

        //dashboard.log("Wall left: " + wallLeft + "  Wall right: " + wallRight + "  Wall front: " + wallFront);
    }

    private void victory() {
        dashboard.getBrain().unregisterDistanceListener(this);
        turning = true;
        dashboard.getBrain().driveBackwards(MazeFunctions.MAX_WHEEL_SPEED);
        SystemClock.sleep(1000);
        dashboard.getBrain().uTurn(new TurnEndHandler() {
            @Override
            public void onTurnEnd() {
                dashboard.getBrain().uTurn(new TurnEndHandler() {
                    @Override
                    public void onTurnEnd() {
                        Maze.this.victory2();
                    }
                });
            }
        });
    }

    private void victory2() {
        for(int i = 0; i < 10; i++) {
            dashboard.speak("We win!");
            SystemClock.sleep(500);
        }
        dashboard.getBrain().stop();
        // Set up for solution run
    }

    @Override
    public void onTurnEnd() {
        turning = false;
        if (dashboard.getBrain().getRequestedLeftVelocity() == 0) {
            dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
        }
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
        //TODO Use a brain function
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
        //TODO Use a brain function
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
