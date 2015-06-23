package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.sensors.UltraSonicSensors;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class Brain extends IRobotCreateAdapter {
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    int theta=0;
    public static final int DISTANCE_TO_CENTER = 30;

    public Brain(IOIO ioio, IRobotCreateInterface create, Dashboard dashboard)
            throws ConnectionLostException {
        super(create);
        sonar = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    /* This method is executed when the robot first starts up. */
    public void initialize() throws ConnectionLostException {
        dashboard.log("Hello! I'm a Clever Robot!");
        //what would you like me to do, Clever Human?




    }
    /* This method is called repeatedly. */
    public void loop() throws ConnectionLostException {
        try {
            dashboard.log("BEFORE SONAR READ");
            sonar.read();
            dashboard.log("AFTER SONAR READ");
        } catch (InterruptedException e) {
            dashboard.log(e.getMessage());
            return;
        }
        dashboard.log("BEFORE MATH");
        int[] x1y1 = getCoordinate(theta+90,sonar.getLeftDistance());
        int[] x2y2 = getCoordinate(theta-90,sonar.getRightDistance());
        dashboard.log("AFTER MATH");
        dashboard.log("L: "+x1y1[0] +" "+ x1y1[1]);
        dashboard.log("R: "+x2y2[0] +" "+ x2y2[1]);
        driveDirect(10,-10);
        try {
            Thread.sleep(10000,0);
        } catch (InterruptedException e) {
            dashboard.log(e.getMessage());

        }
        driveDirect(0,0);
        theta+=45;
    }

    protected int[] getCoordinate(int theta,int distance){
        int x =  (int)Math.round((distance*Math.cos(theta*Math.PI/180)));
        int y = (int)Math.round((distance*Math.sin(theta * Math.PI / 180)));
        return new int[]{x, y};
    }

    public int[] computeWheelSpeed(int turnRadius, int angleOfTurn) {
        double leftWheelSpeed = 500;
        double rightWheelSpeed = 500;

        double a = turnRadius + DISTANCE_TO_CENTER;
        double b = turnRadius - DISTANCE_TO_CENTER;
        double arcRobot = turnRadius * angleOfTurn * 180 / Math.PI;
        double arcA = Math.round(a * angleOfTurn * 180 / Math.PI);
        double arcB = Math.round(b * angleOfTurn * 180 / Math.PI);
        // int leftWheelSpeed = this.getCurrentWheelSpeed()[Robot.LEFT_WHEEL];
        // int rightWheelSpeed = this.getCurrentWheelSpeed()[Robot.RIGHT_WHEEL];
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
}