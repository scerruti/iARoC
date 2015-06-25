package org.jointheleague.nerdherd.iaroc;

import android.os.SystemClock;

/**
 * Created by RussB on 6/24/15.
 */
public class MazeFunctions {
    public static final int SQUARE_DIST = 68;
    public static final int MAX_WHEEL_SPEED = 500;
    public static final int TIME = time(SQUARE_DIST, MAX_WHEEL_SPEED);
    protected Dashboard dashboard;

    public MazeFunctions(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    public static int time(int distance, int speed) {
        return (distance * 10000) / speed;
    }

    public static int distance(int time, int speed) {
        return time * speed;
    }

    public static int speed(int distance, int time) {
        return distance / time;
    }

    public boolean isWallRight(int rightDistance) {
        return rightDistance < 20;
    }

    public boolean isWallLeft(int leftDistance) {
        return leftDistance < 20;
    }

    public boolean isWallFront() {
        boolean isWallFront = false;
        driveHalfSquare();
        if (dashboard.getBrain().isBumpLeft() && dashboard.getBrain().isBumpRight()) {
            driveBackHalfSquare();
            isWallFront = true;
        }
        return isWallFront;
    }

    public void turnRight() {
        // Turn right
    }

    public void turnLeft() {
        // Turn left
    }

    public void driveSquare() {
        dashboard.getBrain().driveForward(MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        SystemClock.sleep(TIME);
        dashboard.getBrain().driveForward(0, 0);
    }

    public void driveHalfSquare() {
        dashboard.getBrain().driveForward(MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        SystemClock.sleep(TIME / 2);
        dashboard.getBrain().driveForward(0, 0);
    }

    public void driveBackHalfSquare() {
        dashboard.getBrain().driveForward(-MAX_WHEEL_SPEED, -MAX_WHEEL_SPEED);
        SystemClock.sleep(TIME / 2);
        dashboard.getBrain().driveForward(0, 0);
    }
}
