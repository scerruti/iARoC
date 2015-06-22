package org.jointheleague.nerdherd.iaroc;

/**
 * ***********************************************************************
 * Super Happy version...ultrasonics working...Version 140512A...mods by Vic
 * Added compass class...works..updatged to adt bundle 20140321
 * ************************************************************************
 */

import android.os.SystemClock;

import org.wintrisstech.irobot.ioio.IRobotCreateAdapter;
import org.wintrisstech.irobot.ioio.IRobotCreateInterface;
import org.jointheleague.nerdherd.sensors.UltraSonicSensors;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;

/**
 * A Robot is an implementation of the IRobotCreateInterface, inspired by Vic's
 * awesome API. It is entirely event driven.
 *
 * @author Erik
 *         Simplified version 140512A by Erik  Super Happy Version
 */
public class Robot extends IRobotCreateAdapter {
    private final DashboardOld dashboard;
    public UltraSonicSensors sonar;
    private boolean firstPass = true;
    ;
    private int commandAzimuth;

    /**
     * Constructs a Robot, an amazing machine!
     *
     * @param ioio      the IOIO instance that the Robot can use to communicate with
     *                  other peripherals such as sensors
     * @param create    an implementation of an iRobot
     * @param dashboard the DashboardOld instance that is connected to the Robot
     * @throws ConnectionLostException
     */
    public Robot(IOIO ioio, IRobotCreateInterface create, DashboardOld dashboard)
            throws ConnectionLostException {
        super(create);
        sonar = new UltraSonicSensors(ioio);
        this.dashboard = dashboard;
    }

    public void initialize() throws ConnectionLostException {
        dashboard.log("Nerd Herd iARoC App Starting");
    }

    /**
     * This method is called repeatedly
     *
     * @param uss
     * @throws ConnectionLostException
     */
    public void loop(UltraSonicSensors uss) throws ConnectionLostException {
        dashboard.log("SENSING");
        SystemClock.sleep(100);
        try {
            uss.read();
        } catch (InterruptedException e) {
            dashboard.log(e.getMessage());
        }
        //dashboard.log(String.valueOf(dashboard.getAzimuth())+" "+ String.valueOf(dashboard.getPitch())+" "+ String.valueOf(dashboard.getRoll()));
        dashboard.log(String.valueOf(uss.getLeftDistance()) + " " + String.valueOf(uss.getFrontDistance()) + " " + String.valueOf(uss.getRightDistance()));
    }

//	public void turn(int commandAngle) throws ConnectionLostException //Doesn't work for turns through 360
//	{
//		int startAzimuth = 0;
//		if (firstPass) {
//			startAzimuth += readCompass();
//			commandAzimuth = (startAzimuth + commandAngle) % 360;
//			dashboard.log("commandaz = " + commandAzimuth + " startaz = " + startAzimuth);
//			firstPass = false;
//		}
//		int currentAzimuth = readCompass();
//		dashboard.log("now = " + currentAzimuth);
//		if (currentAzimuth >= commandAzimuth) {
//			driveDirect(0, 0);
//			firstPass = true;
//			dashboard.log("finalaz = " + readCompass());
//		}
//	}

//	public int readCompass() {
//		return (int) (dashboard.getAzimuth() + 360) % 360;
//	}
}
