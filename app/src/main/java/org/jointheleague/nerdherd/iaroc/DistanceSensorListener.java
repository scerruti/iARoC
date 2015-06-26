package org.jointheleague.nerdherd.iaroc;

/**
 * Created by RussB on 6/22/15.
 */
public interface DistanceSensorListener {
    public void distanceListener(int leftDistance, int rightDistance, boolean isBumpLeft, boolean isBumpRight);
}
