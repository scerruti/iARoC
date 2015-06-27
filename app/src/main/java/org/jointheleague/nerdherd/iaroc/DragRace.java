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
    private static final int ANGLE_BUFFER = 3;
    private static final int SENSOR_READ_BUFFER = 10;
    private static final double COURSE_WIDTH = 129.29;
    private static boolean isAngleFixing = false;
    public int lastL;
    public int lastR;


    public DragRace(Dashboard dashboard)
    {
        super(dashboard);
        dashboard.getBrain().registerDistanceListener(this);
        lastL = 0;
        lastR = 0;
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
        }
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
    public void distanceListener(int leftDistance, int rightDistance, boolean isBumpLeft, boolean isBumpRight) {
        double offsetAngle = dashboard.getBrain().getAngleOffset(COURSE_WIDTH, leftDistance, rightDistance);
        dashboard.log("Offset Angle = " + offsetAngle);
        if (!isAngleFixing) {
            if (lastL == 0 || lastR == 0) {
                lastL = leftDistance;
                lastR = rightDistance;
            }  else if (lastL == 314 || lastR == 314 ||
                    lastL == -1 || lastR == -1) {
                dashboard.log("Sensor Error, lastL = " + lastL + " lastR = " + lastR);
            } else if (Math.abs(lastL - leftDistance) < SENSOR_READ_BUFFER ||
                    Math.abs(lastR - rightDistance) < SENSOR_READ_BUFFER) {
                if ((leftDistance > BUFFER && rightDistance > BUFFER)
                        && (offsetAngle < ANGLE_BUFFER)) {
                    dashboard.log("Nothing to change");
                    return;
                }
                dashboard.log(leftDistance + ", " + rightDistance);
                if (offsetAngle > ANGLE_BUFFER /* || leftDistance <= BUFFER|| rightDistance <= BUFFER */) {
                    dashboard.log("the angle is largely off: start turning");
                    isAngleFixing = true;
                    if (leftDistance > rightDistance) {
                        dashboard.speak("left turn start");
                        TurnThread.startTurn(dashboard.getBrain(), (int) -Math.round(offsetAngle), true, 200, this);
                    } else {
                        dashboard.speak("right turn start");
                        TurnThread.startTurn(dashboard.getBrain(), (int) Math.round(offsetAngle), true, 200, this);
                    }
                }
                lastL = leftDistance;
                lastR = rightDistance;
            } else {
                dashboard.log("Sensor Error, lastL = " + lastL + " lastR = " + lastR + " leftDistance = " + leftDistance + " rightDistance = " + rightDistance);
            }
        }  else {
            dashboard.log("Turning");
            lastL = leftDistance;
            lastR = rightDistance;
        }
    }

    @Override
    public void onTurnEnd() {
        dashboard.log("Back to normal.");
        isAngleFixing = false;
        dashboard.getBrain().driveForward(MAX_SPEED, MAX_SPEED);
    }
}
