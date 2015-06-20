package org.jointheleague.nerdherd.iaroc.thread.navigate.turn;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.Brain;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/4/15.
 */
public class TurnThread {

    protected static boolean alive = true;

    public static final int DEFAULT_TURN_RADIUS = 36;

    public static void startTurn(final Brain b, final int angle) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int[] curWS = new int[]{250, 250};
                try {
                    double[] wheelSpeeds = b.computeWheelSpeed(DEFAULT_TURN_RADIUS, angle);
                    double speed = (wheelSpeeds[0] + wheelSpeeds[1]) / 2;
                    double distance = (Math.PI * DEFAULT_TURN_RADIUS * angle) / 180;
                    double time = (distance / speed) * 1000;
                    b.getDashboard().log("Speeds:\t"+wheelSpeeds);
                    b.getDashboard().log("Time:\t" + time);
                    b.driveDirect((int) wheelSpeeds[0], (int) wheelSpeeds[1]);
                    SystemClock.sleep((int) time);
                } catch (ConnectionLostException cle) {
                    TurnThread.kill();
                } finally {
                    try {
                        b.driveDirect(curWS[0], curWS[1]);
                    } catch (ConnectionLostException e) {}
                }
            }
        });
        t.start();
    }

    private static void kill() {
        alive = false;
    }

}
