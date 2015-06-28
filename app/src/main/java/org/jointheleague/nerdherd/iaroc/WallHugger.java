package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

/**
 * Created by RussB on 6/24/15.
 */
public class WallHugger implements DistanceSensorListener {
    protected MazeFunctions mazeFunctions;
    protected Dashboard dashboard;
    protected Maze maze;
    protected int times = -1;
    protected Navigator navigator;

    public WallHugger(Dashboard dashboard, Maze maze, Navigator navigator) {
        this.dashboard = dashboard;
        this.maze = maze;
        this.navigator = navigator;
        mazeFunctions = new MazeFunctions(this.dashboard, this.maze);
        dashboard.getBrain().registerDistanceListener(this);
    }

    public boolean rightWallHugger(TurnEndHandler turnEndHandler) {
        boolean turning = false;
        if (!maze.isWallRight()) {
            turning = true;
            dashboard.log("Turning right");
            navigator.recordMove('R');
            mazeFunctions.turnRight(turnEndHandler);
        }
        else if (!maze.isWallFront()) {
            dashboard.log("Going forward");
            navigator.recordMove('N');
            mazeFunctions.driveSquare();
        }
        else if (!maze.isWallLeft()) {
            turning = true;
            dashboard.log("Turning left");
            navigator.recordMove('L');
            mazeFunctions.driveBackHalfSquare();
            mazeFunctions.turnLeft(turnEndHandler);
            dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
        }
        else {
            turning = true;
            dashboard.log("Turning around");
            navigator.recordMove('U');
            mazeFunctions.driveBackHalfSquare();
            mazeFunctions.turnAround(turnEndHandler);
            dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
        }
        return turning;
    }

    public void leftWallHugger() {

    }

    @Override
    public void distanceListener(int leftDistance, int rightDistance, boolean isBumpLeft, boolean isBumpRight) {

    }
}
