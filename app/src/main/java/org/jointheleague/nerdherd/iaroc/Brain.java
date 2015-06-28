package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnEndHandler;
import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;
import org.jointheleague.nerdherd.sensors.UltraSonicSensors;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

import java.util.ArrayList;
import java.util.List;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class Brain extends IRobotCreateAdapter {
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    int theta = 0;
    public static final double DISTANCE_TO_CENTER = 13.335;
    public static final double ROBOT_WIDTH = 36;
    ArrayList<DistanceSensorListener> sideDistanceListeners;
    ArrayList<LoopAction> loopActions;
    private int frontDistance = -1;
    private boolean isBumpLeft = false;
    private boolean isBumpRight = false;
    private int leftDistance = -1;
    private int rightDistance = -1;
    private List<BumpListener> bumpListeners;
    protected int beacon = 0;
    public static int RED_BUOY = 8;
    public static int GREEN_BUOY = 4;
    public static int FORCE_FIELD = 2;
    public static int NOTHING = 1;
    private boolean red;
    private boolean green;
    private boolean blue;


    public Brain(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
            throws ConnectionLostException {
        super(create);
        sonar = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    /* This method is executed when the robot first starts up. */
    public void initialize() throws ConnectionLostException {
        dashboard.log("Hello! I'm a Clever Robot!");
        dashboard.speak("what would you like me to do, Clever Human?");
    }

    public int[] computeWheelSpeed(int turnRadius, int angleOfTurn) {
        if (turnRadius == 0) {
            int speed = (Math.abs(getRequestedLeftVelocity()) + Math.abs(getRequestedRightVelocity())) / 2;
            if (speed == 0) speed = MazeFunctions.MAX_WHEEL_SPEED;
            if(angleOfTurn < 0) return new int[]{-speed, speed};
            else return new int[]{speed, -speed};
        }
        double leftWheelSpeed = getRequestedLeftVelocity() == 0 ? MazeFunctions.MAX_WHEEL_SPEED : getRequestedLeftVelocity();
        double rightWheelSpeed = getRequestedRightVelocity() == 0 ? MazeFunctions.MAX_WHEEL_SPEED : getRequestedRightVelocity();
        double a = turnRadius + DISTANCE_TO_CENTER;
        double b = turnRadius - DISTANCE_TO_CENTER;
        double arcRobot = turnRadius * angleOfTurn * Math.PI / 180;
        double arcA = a * angleOfTurn * Math.PI / 180;
        double arcB = b * angleOfTurn * Math.PI / 180;
//        dashboard.log("Computing wheel speed");
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
        //dashboard.log("Loop");

        readSensors(SENSORS_BUMPS_AND_WHEEL_DROPS);

        if (isBumpLeft() || isBumpRight()) {
            stop();
            dashboard.speak("Bump bump bump");
            if(bumpListeners != null) {
                for (BumpListener bumpListener : bumpListeners) {
                    bumpListener.onAnyBump(isBumpLeft(), isBumpRight());
                }
                if (isBumpLeft() && !isBumpRight()) {
                    for (BumpListener bumpListener : bumpListeners) {
                        bumpListener.onLeftBump();
                    }
                }
                if (isBumpRight() && !isBumpLeft()) {
                    for (BumpListener bumpListener : bumpListeners) {
                        bumpListener.onRightBump();
                    }
                }
                if (isBumpLeft() && isBumpRight()) {
                    for (BumpListener bumpListener : bumpListeners) {
                        bumpListener.onFrontBump();
                    }
                }
            }
        }

        readSensors(SENSORS_INFRARED_BYTE);
        int ibyte = getInfraredByte();
        beacon = ibyte & 15;
        if (isBitSet(beacon, 1)) {
            red = false;
            green = false;
            blue = false;
        } else {
            red = isBitSet(beacon, RED_BUOY);
            green = isBitSet(beacon, GREEN_BUOY);
            blue = isBitSet(beacon, FORCE_FIELD);
        }
        dashboard.log("I: "+Integer.toBinaryString(beacon));
        dashboard.log("RGF: " + red + " " + green + " " + blue);

        boolean sonarRead = false;
//        int[] speed = computeWheelSpeed(100, 90);
//        driveDirect(speed[0], speed[1]);
        try {
            sonar.read();
            sonarRead = true;
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
//                dashboard.log("L: "+leftDistance+ " R: "+rightDistance);
                for (DistanceSensorListener dsl: sideDistanceListeners)
                {
                    dsl.distanceListener(sonar.getLeftDistance(), sonar.getRightDistance(), isBumpLeft(), isBumpRight());
                }
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

    public boolean isRedBuoyVisible() {
        return red;
    }

    public boolean isGreenBuoyVisible() {
        return green;
    }

    public boolean isForceFieldVisible() {
        return blue;
    }

    public static boolean isBitSet(int beacon, int bits) {
        return (beacon & bits) == bits;
    }

    protected int[] getCoordinate(int theta, int distance) {
        int x = (int) Math.round((distance * Math.cos(theta * Math.PI / 180)));
        int y = (int) Math.round((distance * Math.sin(theta * Math.PI / 180)));
        return new int[]{x, y};
    }

    public void registerBumpListener(BumpListener bumpListener) {
        if (this.bumpListeners == null) {
            this.bumpListeners = new ArrayList<>();
        }
        this.bumpListeners.add(bumpListener);
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
        if (width < l + r + ROBOT_WIDTH) {
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

    public void unregisterDistanceListener(DistanceSensorListener listener) {
        if (sideDistanceListeners != null) sideDistanceListeners.remove(listener);
    }

    public boolean driveForward(int speed) {
        try {
            dashboard.log("Driving forward, " + speed);
            driveDirect(speed, speed);
            return true;
        } catch (ConnectionLostException e) {
            dashboard.log("Error setting wheel speed.");
            dashboard.log(String.valueOf(e.getStackTrace()));
            return false;
        }
    }

    public boolean driveBackwards(int speed) {
        return driveForward(-speed);
    }

    public boolean stop() {
        return driveForward(0);
    }

    public boolean rightSquareTurn(int radius, TurnEndHandler handler) {
        return rightTurn(90, radius, handler);
    }

    public boolean rightTurn(int angle, int radius, TurnEndHandler handler) {
        return turn(angle, radius, handler);
    }

    public boolean leftSquareTurn(int radius, TurnEndHandler handler) {
        return leftTurn(90, radius, handler);
    }

    public boolean leftTurn(int angle, int radius, TurnEndHandler handler) {
        return turn(-angle, radius, handler);
    }

    public boolean turn(int angle, int radius, TurnEndHandler handler) {
        TurnThread.startTurn(this, angle, true, radius, handler);
        return true;
    }

    public boolean uTurn(TurnEndHandler turnEndHandler) {
        return turn(180, 0, turnEndHandler);
    }

    public boolean rightSquareTurnAndWait(int radius) {
        return rightTurnAndWait(90, radius);
    }

    public boolean rightTurnAndWait(int angle, int radius) {
        return turnAndWait(angle, radius);
    }

    public boolean leftSquareTurnAndWait(int radius) {
        return leftTurnAndWait(90, radius);
    }

    public boolean leftTurnAndWait(int angle, int radius) {
        return turnAndWait(-angle, radius);
    }

    public boolean uTurnAndWait() {
        return turnAndWait(180, 0);
    }

    public boolean turnAndWait(int angle, int radius) {
        TurnThread.startTurn(this, angle, false, radius, null);
        return true;
    }

}