package org.jointheleague.nerdherd.iaroc;

/**
 * Created by RussB on 6/24/15.
 */
public class Maze implements DistanceSensorListener {
    protected MazeFunctions mazeFunctions;
    protected Dashboard dashboard;

    public Maze(Dashboard dashboard) {
        this.dashboard = dashboard;
        mazeFunctions = new MazeFunctions(dashboard);
    }

    public void frontDistanceListener(boolean isBumpLeft, boolean isBumpRight) {

    }

    public void leftDistanceListener(int leftDistance) {

    }

    public void rightDistanceListener(int rightDistance) {

    }
}
