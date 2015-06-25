package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.DistanceSensorListener;

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

    public WallHugger(Dashboard dashboard) {
        this.dashboard = dashboard;
        mazeFunctions = new MazeFunctions(this.dashboard);
        dashboard.getBrain().registerLeftDistanceListener(this);
        dashboard.getBrain().registerRightDistanceListener(this);
        //dashboard.speak("WallHugger Created");
    }

    public void rightWallHugger() {
        mazeFunctions.driveSquare();
        if (!mazeFunctions.isWallRight(rightDistance)) {
            mazeFunctions.turnRight();
        }
        else if (mazeFunctions.isWallFront()) {
            mazeFunctions.turnRight();
        }
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
}
