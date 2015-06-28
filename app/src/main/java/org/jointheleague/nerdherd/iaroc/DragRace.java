package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;
import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by RussB on 6/22/15.
 */
public class DragRace extends Mission implements DistanceSensorListener, TurnEndHandler {
    private static final int FINISH_DISTANCE = 130;
    private static final int MAX_SPEED = 500;
    private static final int BUFFER = 35;
    private static final int ANGLE_BUFFER = 20;
    private static final int COURSE_WIDTH = 132;
    private static boolean isAngleFixing = false;

    public DragRace(Dashboard dashboard)
    {
        super(dashboard);
        dashboard.getBrain().registerDistanceListener(this);
    }

    @Override
    public void runMission() {
        partOne();
    }

    public void partOne()
    {
        dashboard.getBrain().driveForward(MAX_SPEED, MAX_SPEED);
        dashboard.log("Part one");
    }

    public void partTwo()
    {
        dashboard.getBrain().driveForward(-MAX_SPEED, -MAX_SPEED);
        dashboard.log("Part two");
    }

    public void frontDistanceListener(boolean leftBump, boolean rightBump) {
        dashboard.log("front distance listener");
    }

    public void leftDistanceListener(int leftDistance)
    {
        dashboard.log("Left distance listener");

    }

    public void rightDistanceListener(int rightDistance)
    {
        dashboard.log("Right distance listener");

    }

    @Override
    public void distanceListener(int leftDistance, int rightDistance, boolean leftBump, boolean rightBump) {
        dashboard.log("checking if it's time to reverse");
        //if(frontDistance < FINISH_DISTANCE)
        if (leftBump && rightBump)
        {
            dashboard.log("Time to reverse");
            dashboard.getBrain().unregisterDistanceListener(this);
            try {
                dashboard.getBrain().driveDirect(0, 0);
//                partTwo();
            } catch (ConnectionLostException e) {
                e.printStackTrace();
            }
        }        double offsetAngle = dashboard.getBrain().getAngleOffset(COURSE_WIDTH, leftDistance, rightDistance);
        dashboard.log("Offset Angle = " + offsetAngle);
        if (!isAngleFixing) {
            if ((leftDistance > BUFFER && rightDistance > BUFFER)
                    && (offsetAngle < ANGLE_BUFFER)) {
                dashboard.log("Nothing to change");
                return;
            }
            dashboard.log(leftDistance + ", " + rightDistance);
            if (offsetAngle > ANGLE_BUFFER) {
                dashboard.log("the angle is largely off: start turning");
                isAngleFixing = true;
                if (leftDistance > rightDistance) {
                    dashboard.log("left turn not-start");
                    TurnThread.startTurn(dashboard.getBrain(), (int) -Math.round(offsetAngle), true, 200, this);
                } else {
                    dashboard.log("right turn not-start");
                    TurnThread.startTurn(dashboard.getBrain(), (int) Math.round(offsetAngle), true, 200, this);
                }
            }
            if (leftDistance <= BUFFER) {
                dashboard.log("Left Way Too Close");
            } else {
                dashboard.log("Right Way Too Close");
            }
        } else {
            dashboard.log("Turning");
        }
    }

    @Override
    public void onTurnEnd() {
        dashboard.log("Back to normal.");
        isAngleFixing = false;
        dashboard.getBrain().driveForward(MAX_SPEED, MAX_SPEED);
    }
}
