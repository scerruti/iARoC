package org.jointheleague.nerdherd.iaroc;

/**
 * Created by Stephen on 6/27/2015.
 */
public interface BumpListener {

    void onAnyBump(boolean left, boolean right);
    void onRightBump();
    void onLeftBump();
    void onFrontBump();

}
