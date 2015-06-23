package org.jointheleague.nerdherd.iaroc;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by RussB on 6/22/15.
 */
public class DragRace extends Mission implements DistanceSensorListener {
    private static final int FINISH_DISTANCE = 130;
    private static final int MAX_SPEED = 500;

    public DragRace(Dashboard dashboard)
    {
        super(dashboard);
        dashboard.getBrain().registerFrontDistanceListener(this);
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

    public void frontDistanceListener(int frontDistance) {
        dashboard.log("Stuff");
        if(frontDistance < FINISH_DISTANCE)
        {
            dashboard.log("Time to freak out");
            dashboard.getBrain().unregisterFrontDistanceListener(this);
            partTwo();
        }
    }
}
