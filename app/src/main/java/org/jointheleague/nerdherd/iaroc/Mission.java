package org.jointheleague.nerdherd.iaroc;

import ioio.lib.api.exception.ConnectionLostException;

/**
 * Created by RussB on 6/22/15.
 */
public abstract class Mission {
    Dashboard dashboard;
    public Mission(Dashboard dashboard)
    {
        this.dashboard = dashboard;
    }
    public abstract void runMission() throws ConnectionLostException;
}
