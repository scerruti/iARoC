package org.jointheleague.nerdherd.iaroc.thread.navigate.turn;

import org.jointheleague.nerdherd.iaroc.Dashboard;
import org.jointheleague.nerdherd.iaroc.Robot;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by firestar115 on 6/4/15.
 */
public class TurnThread {

    protected static boolean alive = true;

    public static final int DEFAULT_TURN_RADIUS = 80;

    public static void startTurn(Robot r, int angle) {
        Thread t = new Thread(() -> {
            int[] curWS = new int[]{250, 250};
            try {
                int[] wheelSpeeds = r.computeWheelSpeed(DEFAULT_TURN_RADIUS, angle);
                int speed = (wheelSpeeds[0] + wheelSpeeds[1]) / 2;
                int distance = (int) (Math.PI*DEFAULT_TURN_RADIUS*angle) / 180;
                int time = distance / speed;
                r.driveDirect(wheelSpeeds[0], wheelSpeeds[1]);
                Thread.sleep(time);
            } catch (ConnectionLostException cle) {
                TurnThread.kill();
            } catch (InterruptedException e) {
                TurnThread.kill();
            } finally {
                try {
                    r.driveDirect(curWS[0],curWS[1]);
                } catch (ConnectionLostException e) { };
            }
        });
        t.start();
    }

    private static void kill() {
        alive = false;
    }

}
