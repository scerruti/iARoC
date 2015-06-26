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
        dashboard.getBrain().registerFrontDistanceListener(this);
    }

    @Override
    public void frontDistanceListener(boolean leftBump, boolean rightBump) {
        this.bumped = leftBump || rightBump;
    }

    @Override
    public void leftDistanceListener(int leftDistance) {
        this.left = leftDistance;
    }

    @Override
    public void rightDistanceListener(int rightDistance) {
        this.right = rightDistance;
    }

    @Override
    public void sideDistanceListener(int leftDistance, int rightDistance) {

    }

    @Override
    public void runMission() throws ConnectionLostException {
//        for (int i = 0; i < 3; i++) {
//            boolean doleft = left > right;
//            double time;
//            if (doleft) {
//                time = TurnThread.startTurn(dashboard.getBrain(), -90);
//            } else {
//                time = TurnThread.startTurn(dashboard.getBrain(), 90);
//            }
//            SystemClock.sleep((long) time);
//            dashboard.getBrain().driveDirect(500, 500);
//            while (!bumped) {
//                SystemClock.sleep(10);
//            }
//            dashboard.getBrain().driveDirect(0, 0);
//            Runtime.getRuntime().gc();
//        }
        dashboard.getBrain().demo(Brain.DEMO_COVER_AND_DOCK);
    }

}
