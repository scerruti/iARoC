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

    public WallHugger(Dashboard dashboard, Maze maze) {
        this.dashboard = dashboard;
        this.maze = maze;
        mazeFunctions = new MazeFunctions(this.dashboard, this.maze);
        dashboard.getBrain().registerDistanceListener(this);
    }

    public boolean rightWallHugger(TurnEndHandler turnEndHandler) {
        boolean turning = false;
        if (!maze.isWallRight()) {
            turning = true;
            dashboard.log("Turning right");
            mazeFunctions.turnRight(turnEndHandler);
        }
        else if (!maze.isWallFront()) {
            dashboard.log("Going forward");
            mazeFunctions.driveSquare();
        }
        else if (!maze.isWallLeft()) {
            turning = true;
            dashboard.log("Turning left");
            mazeFunctions.driveBackHalfSquare();
            mazeFunctions.turnLeft(turnEndHandler);
            dashboard.getBrain().driveForward(MazeFunctions.MAX_WHEEL_SPEED);
        }
        else {
            turning = true;
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
