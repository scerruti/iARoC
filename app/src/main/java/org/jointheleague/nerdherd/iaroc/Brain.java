package org.jointheleague.nerdherd.iaroc;

import org.jointheleague.nerdherd.iaroc.thread.navigate.turn.TurnThread;
import org.jointheleague.nerdherd.sensors.UltraSonicSensors;
import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

public class Brain extends IRobotCreateAdapter {
    private final Dashboard dashboard;
    public UltraSonicSensors sonar;
    int theta=0;
    private boolean turnStarted = false;

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
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {

        }
//        TurnThread.startTurn(this, 90);



    }
    public int[] computeWheelSpeed(int defaultTurnRadius, int angle) {
        return new int[]{343,156}; // TODO implemented by Russ and Ruoya
    }
    /* This method is called repeatedly. */
    public void loop() throws ConnectionLostException {
        /*try {
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
        theta+=45;*/
        if(!turnStarted) {
            turnStarted = true;
            TurnThread.startTurn(this, 90);
        }
    }

    protected int[] getCoordinate(int theta,int distance){
        int x =  (int)Math.round((distance*Math.cos(theta*Math.PI/180)));
        int y = (int)Math.round((distance*Math.sin(theta * Math.PI / 180)));
        return new int[]{x, y};
    }
}