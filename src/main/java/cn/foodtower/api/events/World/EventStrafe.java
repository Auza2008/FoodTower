package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public final class EventStrafe
        extends Event {
    private final float strafe;
    private final float forward;
    private final float friction;

    public EventStrafe(float strafe, float forward, float friction) {
        this.strafe = strafe;
        this.forward = forward;
        this.friction = friction;
    }

    public float getStrafe() {
        return this.strafe;
    }

    public float getForward() {
        return this.forward;
    }

    public float getFriction() {
        return this.friction;
    }
}
