package org.jointheleague.nerdherd.iaroc.thread.navigate.turn;

import android.os.SystemClock;

import org.jointheleague.nerdherd.iaroc.Brain;

import java.lang.reflect.Method;
import java.util.Arrays;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/4/15.
 * Class: iARoC:${PACKAGE_NAME}.${CLASS_NAME}
 */
public class TurnThread {

    protected static boolean alive = true;

    public static final int DEFAULT_TURN_RADIUS = 20;

    public static void startTurn(final Brain b, final int angle) {
        startTurn(b, angle, true);
    }

    public static void startTurn(final Brain b, final int angle, boolean threaded) {
        startTurn(b, angle, threaded, DEFAULT_TURN_RADIUS, null);
    }

    public static void startTurn(final Brain b, final int angle, boolean threaded, final int radius, final TurnEndHandler handler) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int[] curWS = new int[]{b.getRequestedRightVelocity(), b.getRequestedLeftVelocity()};
                try {
                    int[] wheelSpeeds = b.computeWheelSpeed(radius, angle);
                    double speed = (wheelSpeeds[0] + wheelSpeeds[1]) / 2;
                    double distance;
                    double time;
                    if(radius == 0 || speed == 0) {
                        distance  = (Brain.DISTANCE_TO_CENTER * angle) * Math.PI / 180;
                        time = Math.abs(distance / Math.abs(wheelSpeeds[0]));
                        //b.getDashboard().log(""+time);
                        //SystemClock.sleep(5000);
                    } else {
                        distance  = (radius * angle) * Math.PI / 180;
                        time = Math.abs(distance / speed);
                    }
                    b.driveDirect(wheelSpeeds[1], wheelSpeeds[0]);
                    SystemClock.sleep((int) (time * 10000));
                    //b.getDashboard().log("" + time * 10000);
                } catch (ConnectionLostException cle) {
                    TurnThread.kill();
                } finally {
                    try {
                        alive = false;
                        if(handler != null)
                            handler.onTurnEnd();
                        b.driveDirect(curWS[0], curWS[1]);
                    } catch (ConnectionLostException ignored) {/*Impossible :)*/}
                }
            }
        });
        alive=true;
        if(threaded)
            t.start();
        else
            t.run();
    }

    public static boolean isAlive() {
        return alive;
    }

    private static void kill() {
        alive = false;
    }
}
