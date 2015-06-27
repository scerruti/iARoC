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
    public static final double DISTANCE_TO_CENTER = 13.335;
    public static final double ROBOT_WIDTH = 34.29;
    ArrayList<DistanceSensorListener> frontDistanceListeners;
    ArrayList<DistanceSensorListener> leftDistanceListeners;
    ArrayList<DistanceSensorListener> rightDistanceListeners;
    ArrayList<DistanceSensorListener> sideDistanceListeners;
    ArrayList<LoopAction> loopActions;
    private int frontDistance = -1;
    private boolean isBumpLeft = false;
    private boolean isBumpRight = false;
    private int leftDistance = -1;
    private int rightDistance = -1;
    public int currLSpeed = 0;
    public int currRSpeed = 0;


    public Brain(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
            throws ConnectionLostException {
        super(create);
        frontDistanceListeners = new ArrayList<>();
        leftDistanceListeners = new ArrayList<>();
        rightDistanceListeners = new ArrayList<>();
        sonar = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    /* This method is executed when the robot first starts up. */
    public void initialize() throws ConnectionLostException {
        dashboard.log("Hello! I'm a Clever Robot!");
        //what would you like me to do, Clever Human?
    }

    public int[] computeWheelSpeed(int turnRadius, int angleOfTurn) {
        if (turnRadius == 0) {
            return new int[]{-250, 250};
        }
        double leftWheelSpeed = 250;
        double rightWheelSpeed = 250;
        double a = turnRadius + DISTANCE_TO_CENTER;
        double b = turnRadius - DISTANCE_TO_CENTER;
        double arcRobot = turnRadius * angleOfTurn * Math.PI / 180;
        double arcA = a * angleOfTurn * Math.PI / 180;
        double arcB = b * angleOfTurn * Math.PI / 180;
        dashboard.log("Computing wheel speed");
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

        readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);

        if (isBumpLeft() || isBumpRight()) {
            driveDirect(0, 0);
        }

        boolean sonarRead = false;
//        int[] speed = computeWheelSpeed(100, 90);
//        driveDirect(speed[0], speed[1]);
        try {
            sonar.read();
            if (sonar.getLeftDistance() != -1 && sonar.getRightDistance() != -1) {
                sonarRead = true;
            }
        } catch (InterruptedException e) {
            dashboard.log(e.getMessage());
        }
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

        if (sonarRead) {
            if ((sonar.getLeftDistance() != leftDistance || sonar.getRightDistance() != rightDistance)
                    && sideDistanceListeners != null)
            {
                dashboard.log("L: "+leftDistance+ " R: "+rightDistance);
                for (DistanceSensorListener dsl: sideDistanceListeners)
                {
                    dsl.distanceListener(sonar.getLeftDistance(), sonar.getRightDistance(), isBumpLeft(), isBumpRight());
                }
                leftDistance = sonar.getLeftDistance();
                rightDistance = sonar.getRightDistance();
            }
//            if (sonar.getFrontDistance() != frontDistance)
//            {
//                frontDistance = sonar.getFrontDistance();
//                for (DistanceSensorListener dsl: frontDistanceListeners)
//                {
//                    dsl.frontDistanceListener(frontDistance);
//                }
//            }
        }
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
        if (loopActions != null) {
            for (LoopAction action : loopActions) {
                action.doAction();
            }
        }
    }

    protected void driveForward(int a, int b) {
        try {
            dashboard.log("Driving forward, " + a + " " + b);
            driveDirect(a, b);
            currLSpeed = a;
            currRSpeed = b;
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    protected int[] getCoordinate(int theta, int distance) {
        int x = (int) Math.round((distance * Math.cos(theta * Math.PI / 180)));
        int y = (int) Math.round((distance * Math.sin(theta * Math.PI / 180)));
        return new int[]{x, y};
    }

    public void registerDistanceListener(DistanceSensorListener sideDistanceListener) {
        if (this.sideDistanceListeners == null) {
            this.sideDistanceListeners = new ArrayList<>();
        }
        this.sideDistanceListeners.add(sideDistanceListener);
    }

    public void hitWall(String event) {
        if (event.equals("maze")) {
            try {
                readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);
                if (isBumpLeft() && isBumpRight()) {
                    // Why?
                }
            } catch (ConnectionLostException e) {
                e.printStackTrace();
            }
        }
    }

    private void printSonar() throws ConnectionLostException {
//        dashboard.log("Top of loop");
        try {
            sonar.read();
//            dashboard.log("Sonar read");
        } catch (InterruptedException e) {
//            dashboard.log("ERROR: "+e.getLocalizedMessage());
        }
//        dashboard.log("After Sonar Read");
    }

    public double getAngleOffset(double width, double l, double r) throws IllegalArgumentException {
        dashboard.log("Width: " + width + "L: " + l + "R: " + r);
        if (width <= l + r + ROBOT_WIDTH) {
            double ratio = width / (l + r + ROBOT_WIDTH);
            double offsetRadians = Math.asin(ratio);
            return 90 - Math.toDegrees(offsetRadians);
        } else {
            dashboard.log("ILLEGAL ARGUMENTS!! Width = " + width + " L + R + ROBOT_WIDTH = " + (l + r + ROBOT_WIDTH));
            return 0;
        }
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void registerLoopAction(LoopAction action) {
        if (loopActions == null) {
            loopActions = new ArrayList<>();
        }
        loopActions.add(action);
    }

    public void unregisterDistanceListener(DistanceSensorListener distanceListener) {

    }
}