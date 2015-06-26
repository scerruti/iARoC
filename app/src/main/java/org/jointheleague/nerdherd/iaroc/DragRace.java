package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by RussB on 6/22/15.
 */
public class DragRace extends Mission implements DistanceSensorListener {
    private static final int FINISH_DISTANCE = 130;
    private static final int MAX_SPEED = 500;
    private static final int BUFFER = 40;
    private static final int ANGLE_BUFFER = 7;
    private static final int COURSE_WIDTH = 140;
    private static boolean isAngleFixing = false;

    public DragRace(Dashboard dashboard)
    {
        super(dashboard);
        dashboard.getBrain().registerFrontDistanceListener(this);
        dashboard.getBrain().registerSideDistanceListener(this);
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
        dashboard.log("Stuff");
        //if(frontDistance < FINISH_DISTANCE)
        if (leftBump && rightBump)
        {
            dashboard.log("Time to freak out");
            dashboard.getBrain().unregisterFrontDistanceListener(this);
            partTwo();
        }
    }

    public void leftDistanceListener(int leftDistance)
    {
        dashboard.log("Left!");

    }

    public void rightDistanceListener(int rightDistance)
    {
        dashboard.log("Right!");

    }

    @Override
    public void sideDistanceListener(int leftDistance, int rightDistance) {
        double theta = dashboard.getBrain().getAngleOffset(COURSE_WIDTH, leftDistance, rightDistance);
        dashboard.log("Offset Angle = " + theta);
        if (!isAngleFixing) {
            if ((leftDistance > BUFFER && rightDistance > BUFFER)
                    && (theta < ANGLE_BUFFER)) {
                return;
            }
            dashboard.log(leftDistance + ", " + rightDistance);
            if (theta > ANGLE_BUFFER) {
                dashboard.log("the angle is off-al");
                isAngleFixing = true;
                if (leftDistance > rightDistance) {
                    dashboard.log("Right closer");
                    TurnThread.startTurnWithRadius(dashboard.getBrain(), (int) Math.round(theta), true, rightDistance);
                } else {
                    dashboard.log("Left closer");
                    TurnThread.startTurnWithRadius(dashboard.getBrain(), (int) -Math.round(theta), true, rightDistance);
                }
            }
            if (leftDistance <= BUFFER) {
                dashboard.log("Left Too Close");
            } else {
                dashboard.log("Right Too Close");
            }
        } else {
            if (theta < ANGLE_BUFFER) {
                isAngleFixing = false;
                dashboard.getBrain().driveForward(MAX_SPEED, MAX_SPEED);
            }
        }
    }
}
