package org.jointheleague.nerdherd.iaroc.goldrush;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.Brain;
import org.jointheleague.nerdherd.iaroc.Dashboard;
import org.jointheleague.nerdherd.iaroc.DistanceSensorListener;
import org.jointheleague.nerdherd.iaroc.Mission;
import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/24/15.
 * Class: iARoC:org.jointheleague.nerdherd.iaroc.goldrush.${CLASS_NAME}
 */
public class GoldRush extends Mission implements DistanceSensorListener {

    public static final int MAX_VELOCITY = 500;
    public static final int TURN_RADIUS = 28;
    private Dashboard dashboard;
    private boolean bumped;
    private int left;
    private int right;

    public GoldRush(Dashboard dashboard) {
        super(dashboard);
        this.dashboard = dashboard;
        dashboard.getBrain().registerDistanceListener(this);
    }

    @Override
    public void distanceListener(int leftDistance, int rightDistance, boolean bumpLeft, boolean bumpRight) {
        this.bumped = bumpLeft || bumpRight;
        this.left = leftDistance;
        this.right = rightDistance;
    }

    @Override
    public void runMission() throws ConnectionLostException {
        for (int i = 0; i < 3; i++) {
            boolean doleft = left > right;
            double time;
            if (doleft) {
                dashboard.getBrain().leftSquareTurnAndWait(TURN_RADIUS);
            } else {
                dashboard.getBrain().rightSquareTurnAndWait(TURN_RADIUS);
            }
            dashboard.getBrain().driveForward(MAX_VELOCITY);
            while (!bumped) {
                SystemClock.sleep(10);
            }
            dashboard.getBrain().stop();
            Runtime.getRuntime().gc();
        }
        while (true) {
            dashboard.getBrain().demo(Brain.DEMO_COVER_AND_DOCK);
        }
    }

}
