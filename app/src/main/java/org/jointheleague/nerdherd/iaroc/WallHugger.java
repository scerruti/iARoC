package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.DistanceSensorListener;
import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;

/**
 * Created by RussB on 6/24/15.
 */
public class WallHugger implements DistanceSensorListener {
    protected boolean leftBump;
    protected boolean rightBump;
    protected int leftDistance;
    protected int rightDistance;
    protected MazeFunctions mazeFunctions;
    protected Dashboard dashboard;
    protected Maze maze;
    protected int times = -1;

    public WallHugger(Dashboard dashboard, Maze maze) {
        this.dashboard = dashboard;
        this.maze = maze;
        mazeFunctions = new MazeFunctions(this.dashboard);
        dashboard.getBrain().registerLeftDistanceListener(this);
        dashboard.getBrain().registerRightDistanceListener(this);

        //dashboard.speak("WallHugger Created");
    }

    public boolean rightWallHugger(TurnEndHandler turnEndHandler) {
        //mazeFunctions.driveSquare();
        boolean turning = false;
        if (!maze.isWallDataValid()) {
            return true;
        }
        dashboard.log("Right: " + rightDistance + " Left: " + leftDistance + " Times: " + times);
        dashboard.log("Is wall right: " + mazeFunctions.isWallRight(rightDistance) + "  Is wall left: " + mazeFunctions.isWallLeft(leftDistance) + "  Is wall front: " + mazeFunctions.isWallFront(times));
        if (!maze.isWallRight()) {
            dashboard.log("Turning right");
            mazeFunctions.turnRight(turnEndHandler);
            turning = true;
        }
        else if (!mazeFunctions.isWallFront(times)) {
            dashboard.log("Going forward");
            mazeFunctions.driveSquare();
        }
        else if (!maze.isWallLeft()) {
            dashboard.log("Turning left");
            mazeFunctions.turnLeft(turnEndHandler);
            turning = true;
        }
        else {
            mazeFunctions.turnAround(turnEndHandler);
            turning = true;
        }
        times++;
        //dashboard.log("Is there a wall?" + mazeFunctions.isWallFront());
        return turning;
    }

    public void leftWallHugger() {

    }

    public void frontDistanceListener(boolean leftBump, boolean rightBump) {
        this.leftBump = leftBump;
        this.rightBump = rightBump;
    }

    public void leftDistanceListener(int leftDistance) {
        this.leftDistance = leftDistance;
    }

    public void rightDistanceListener(int rightDistance) {
        this.rightDistance = rightDistance;
    }

    @Override
    public void sideDistanceListener(int leftDistance, int rightDistance) {

    }
}
