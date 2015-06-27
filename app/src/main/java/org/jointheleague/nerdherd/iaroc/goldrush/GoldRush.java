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
                TurnThread.startTurn(dashboard.getBrain(), -90, false);
            } else {
                TurnThread.startTurn(dashboard.getBrain(), 90, false);
            }
            dashboard.getBrain().driveDirect(500, 500);
            while (!bumped) {
                SystemClock.sleep(10);
            }
            dashboard.getBrain().driveDirect(0, 0);
            Runtime.getRuntime().gc();
        }
        dashboard.getBrain().demo(Brain.DEMO_COVER_AND_DOCK);
    }

}
