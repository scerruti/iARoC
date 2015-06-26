package org.jointheleague.nerdherd.iaroc;

/**
 * Created by RussB on 6/24/15.
 */
public class Maze implements DistanceSensorListener, LoopAction {
    protected MazeFunctions mazeFunctions;
    WallHugger wallHugger;
    protected Dashboard dashboard;

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        this.wallHugger = new WallHugger(dashboard);
        dashboard.getBrain().registerLoopAction(this);
        mazeFunctions = new MazeFunctions(dashboard);
    }

    public void frontDistanceListener(boolean isBumpLeft, boolean isBumpRight) {

    }

    public void leftDistanceListener(int leftDistance) {

    }

    public void rightDistanceListener(int rightDistance) {

    }

    @Override
    public void sideDistanceListener(int leftDistance, int rightDistance) {

    }

    @Override
    public void doAction() {
        wallHugger.rightWallHugger();
    }
}
