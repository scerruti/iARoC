package org.jointheleague.nerdherd.iaroc;

/**
 * Created by RussB on 6/22/15.
 */
public interface DistanceSensorListener {
    public void frontDistanceListener(boolean leftBump, boolean rightBump);
    public void leftDistanceListener(int leftDistance);
    public void rightDistanceListener(int rightDistance);
    public void sideDistanceListener(int leftDistance, int rightDistance);
}
