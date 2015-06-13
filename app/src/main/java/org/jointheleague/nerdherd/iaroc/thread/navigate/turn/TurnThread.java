package org.jointheleague.nerdherd.iaroc.thread.navigate.turn;

import org.jointheleague.nerdherd.iaroc.Brain;
import org.jointheleague.nerdherd.iaroc.Dashboard;
import org.jointheleague.nerdherd.iaroc.Robot;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/4/15.
 */
public class TurnThread {

    protected static boolean alive = true;

    public static final int DEFAULT_TURN_RADIUS = 80;

    public static void startTurn(Brain b, int angle) {
        final int nangle = angle;
        final Brain nb = b;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int[] curWS = new int[]{250, 250};
                try {
                    int[] wheelSpeeds = nb.computeWheelSpeed(DEFAULT_TURN_RADIUS, nangle);
                    int speed = (wheelSpeeds[0] + wheelSpeeds[1]) / 2;
                    int distance = (int) (Math.PI * DEFAULT_TURN_RADIUS * nangle) / 180;
                    int time = distance / speed;
                    nb.driveDirect(wheelSpeeds[0], wheelSpeeds[1]);
                    Thread.sleep(time);
                } catch (ConnectionLostException cle) {
                    TurnThread.kill();
                } catch (InterruptedException e) {
                    TurnThread.kill();
                } finally {
                    try {
                        nb.driveDirect(curWS[0], curWS[1]);
                    } catch (ConnectionLostException e) {
                    }
                    ;
                }
            }
        });
        t.start();
    }

    private static void kill() {
        alive = false;
    }

}
