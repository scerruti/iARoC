package org.jointheleague.nerdherd.iaroc;

import android.os.SystemClock;

import org.jointheleague.nerdherd.sensors.UltraSonicSensors;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

import java.util.ArrayList;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class Brain extends IRobotCreateAdapter {
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    int theta = 0;
    public static final int DISTANCE_TO_CENTER = 30;
    ArrayList<DistanceSensorListener> frontDistanceListeners;
    ArrayList<DistanceSensorListener> leftDistanceListeners;
    ArrayList<DistanceSensorListener> rightDistanceListeners;
    private int frontDistance = -1;
    private boolean isBumpLeft = false;
    private boolean isBumpRight = false;
    private int leftDistance = -1;
    private int rightDistance = -1;
    private int MAX_SPEED = 500;
    public int lws = MAX_SPEED;
    public int rws = MAX_SPEED;


    public Brain(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
            throws ConnectionLostException {
        super(create);
        frontDistanceListeners = new ArrayList<DistanceSensorListener>();
        sonar = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    /* This method is executed when the robot first starts up. */
    public void initialize() throws ConnectionLostException {
        dashboard.log("Hello! I'm a Clever Robot!");
        //what would you like me to do, Clever Human?
    }

    public int[] computeWheelSpeed(int turnRadius, int angleOfTurn) {
        double leftWheelSpeed = 500;
        double rightWheelSpeed = 500;

        double a = turnRadius + DISTANCE_TO_CENTER;
        double b = turnRadius - DISTANCE_TO_CENTER;
        double arcRobot = turnRadius * angleOfTurn * 180 / Math.PI;
        double arcA = Math.round(a * angleOfTurn * 180 / Math.PI);
        double arcB = Math.round(b * angleOfTurn * 180 / Math.PI);

        double time = arcRobot / ((rightWheelSpeed + leftWheelSpeed) / 2);
        double aSpeed = arcA / time;
        double bSpeed = arcB / time;
        if (aSpeed > 500) {
            bSpeed = 500 * bSpeed / aSpeed;
            aSpeed = 500;
        } else if (bSpeed > 500) {
            aSpeed = 500 * aSpeed / bSpeed;
            bSpeed = 500;
        }
        return new int[]{(int) aSpeed, (int) bSpeed};
    }


    /* This method is called repeatedly. */
    public void loop() throws ConnectionLostException {
        dashboard.log("Loop");
//        int[] speed = computeWheelSpeed(100, 90);
//        driveDirect(speed[0], speed[1]);
//        try {
//            dashboard.log("BEFORE SONAR READ");
//            sonar.read();
//            dashboard.log("AFTER SONAR READ");
//        } catch (InterruptedException e) {
//            dashboard.log(e.getMessage());
//            return;
//        }
//        dashboard.log("BEFORE MATH");
//        int[] x1y1 = getCoordinate(theta+90,sonar.getLeftDistance());
//        int[] x2y2 = getCoordinate(theta-90,sonar.getRightDistance());
//        dashboard.log("AFTER MATH");
//        dashboard.log("L: "+x1y1[0] +" "+ x1y1[1]);
//        dashboard.log("R: "+x2y2[0] +" "+ x2y2[1]);
//        driveDirect(10,-10);
//        try {
//            Thread.sleep(10000,0);
//        } catch (InterruptedException e) {
//            dashboard.log(e.getMessage());
//
//        }
//        driveDirect(0,0);
//        theta+=45;
        //readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
//        if(dashboard.bumpBox.isChecked()) {
//            dashboard.log("L: " + isBumpLeft() + " R: " + isBumpRight());
//        }
//        SystemClock.sleep(1000);

//        try {
//
//            sonar.read();
//            dashboard.log("Read sonars");
//            if (sonar.getFrontDistance() != frontDistance)
//            {
//                frontDistance = sonar.getFrontDistance();
//                for (DistanceSensorListener dsl: frontDistanceListeners)
//                {
//                    dsl.frontDistanceListener(frontDistance);
//                }
//            }
//        } catch (InterruptedException e) {
//            dashboard.log("Interruption!");
//            e.printStackTrace();
//        }
//        int d0;
//        int dc;
//        try {
//            sonar.read();
//            d0 = sonar.getLeftDistance();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        driveDirect(MAX_SPEED, MAX_SPEED);
//        SystemClock.sleep(1000);
//        driveDirect(0, 0);
//        try {
//            sonar.read();
//            dc = sonar.getLeftDistance();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);

        if (isBumpRight() != isBumpRight) {
                isBumpRight = isBumpRight();
                isBumpLeft = isBumpLeft();
                for (DistanceSensorListener dsl: frontDistanceListeners)
                {
                    dsl.frontDistanceListener(isBumpLeft, isBumpRight);
                }
        }

        try {
            sonar.read();
            if (sonar.getLeftDistance() != leftDistance)
            {
                leftDistance = sonar.getLeftDistance();
                for (DistanceSensorListener dsl: leftDistanceListeners)
                {
                    dsl.leftDistanceListener(leftDistance);
                }
            }
        } catch (InterruptedException e) {
            dashboard.log("Interruption!");
            e.printStackTrace();
        }

        try {
            sonar.read();
            if (sonar.getRightDistance() != rightDistance)
            {
                rightDistance = sonar.getRightDistance();
                for (DistanceSensorListener dsl: rightDistanceListeners)
                {
                    dsl.rightDistanceListener(rightDistance);
                }
            }
        } catch (InterruptedException e) {
            dashboard.log("Interruption!");
            e.printStackTrace();
        }

        driveDirect(lws, rws);
    }

    protected void driveForward(int a, int b) {
        try {
            driveDirect(a, b);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    protected int[] getCoordinate(int theta, int distance) {
        int x = (int) Math.round((distance * Math.cos(theta * Math.PI / 180)));
        int y = (int) Math.round((distance * Math.sin(theta * Math.PI / 180)));
        return new int[]{x, y};
    }

    public void registerFrontDistanceListener(DistanceSensorListener frontDistanceListener) {
        this.frontDistanceListeners.add(frontDistanceListener);
        dashboard.log("Registered front distance listener");
    }

    public void unregisterFrontDistanceListener(DistanceSensorListener frontDistanceListener) {
        this.frontDistanceListeners.remove(frontDistanceListener);
        dashboard.log("Unregistered front distance listener");
    }

    public void hitWall(String event) {
        if (event.equals("maze")) {
            try {
                readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
                if (isBumpLeft() && isBumpRight()) {

                }
            } catch (ConnectionLostException e) {
                e.printStackTrace();
            }
        }
    }
}