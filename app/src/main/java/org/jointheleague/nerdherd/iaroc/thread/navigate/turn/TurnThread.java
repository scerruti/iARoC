package org.jointheleague.nerdherd.iaroc.thread.navigate.turn;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.Brain;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/4/15.
 */
public class TurnThread {

    protected static boolean alive = true;

    public static final int DEFAULT_TURN_RADIUS = 80;

    public static void startTurn(final Brain b, final int angle) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int[] curWS = new int[]{250, 250};
                try {
                    int[] wheelSpeeds = b.computeWheelSpeed(DEFAULT_TURN_RADIUS, angle);
                    System.out.printf("Current: %d %d. New: %d %d", curWS[0], curWS[1], wheelSpeeds[0], wheelSpeeds[1]);
                    int speed = (wheelSpeeds[0] + wheelSpeeds[1]) / 2;
                    int distance = (int) (Math.PI * DEFAULT_TURN_RADIUS * angle) / 180;
                    int time = distance / speed;
                    System.out.printf("Time: %d", time);
                    b.driveDirect(wheelSpeeds[0], wheelSpeeds[1]);
                    SystemClock.sleep(time);
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
