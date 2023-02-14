/*
Author:SuMuGod
Date:2022/7/10 3:29
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;

public class EventMove
        extends Event {
    public static double x;
    public static double y;
    public static double z;
    private double motionX;
    private double motionY;
    private double motionZ;

    public EventMove(double x, double y, double z) {
        EventMove.x = x;
        EventMove.y = y;
        EventMove.z = z;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        EventMove.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        EventMove.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        EventMove.z = z;
    }
}
