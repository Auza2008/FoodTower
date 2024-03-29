package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public class EventPreUpdate extends Event {
    public static float yaw;
    public static float pitch;
    private final boolean isPre;
    public double y;
    public double x;
    public double z;
    private boolean ground;

    public EventPreUpdate(float yaw, float pitch, double x, double y, double z, boolean ground) {
        EventPreUpdate.yaw = yaw;
        EventPreUpdate.pitch = pitch;
        this.y = y;
        this.x = x;
        this.z = z;
        this.ground = ground;
        this.isPre = true;
    }

    public static float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        EventPreUpdate.yaw = yaw;
    }

    public static float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        EventPreUpdate.pitch = pitch;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public boolean isOnground() {
        return this.ground;
    }

    public void setOnground(boolean ground) {
        this.ground = ground;
    }

    public boolean isPre() {
        return this.isPre;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}
