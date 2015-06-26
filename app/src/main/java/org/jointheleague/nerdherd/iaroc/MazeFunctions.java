package org.jointheleague.nerdherd.iaroc;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;
import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;

/**
 * Created by RussB on 6/24/15.
 */
public class MazeFunctions {
    public static final int SQUARE_DIST = 68;
    public static final int MAX_WHEEL_SPEED = 250;
    public static final int TIME = time(SQUARE_DIST, MAX_WHEEL_SPEED);
    private static final int RADIUS = 28;
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

    public boolean isWallFront(int frontDistance) {
        //return frontDistance < 20;
        if (frontDistance == -1) {
            return false;
        }
        if (frontDistance == 0) {
            return true;
        }
        if (frontDistance == 1) {
            return false;
        }
        if (frontDistance == 1) {
            return true;
        }
        if (frontDistance == 1) {
            return false;
        }
        if (frontDistance == 1) {
            return false;
        }
        return false;
    }

    public void turnRight(TurnEndHandler turnEndHandler) {
        TurnThread.startTurn(dashboard.getBrain(), 90, true, RADIUS, turnEndHandler);
    }

    public void turnLeft(TurnEndHandler turnEndHandler) {
        TurnThread.startTurn(dashboard.getBrain(), -90, true, RADIUS, turnEndHandler);
    }

    public void turnAround(TurnEndHandler turnEndHandler) {
        TurnThread.startTurn(dashboard.getBrain(), 180, false, 0, turnEndHandler);
    }

    public void driveSquare() {
        dashboard.getBrain().driveForward(MAX_WHEEL_SPEED, MAX_WHEEL_SPEED);
        SystemClock.sleep(TIME);
    }
}
