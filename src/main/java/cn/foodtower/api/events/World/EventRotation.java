package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public class EventRotation extends Event {
    private float yaw;
    private float pitch;

    public EventRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return this.yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public EventRotation getAngle() {
        this.yaw %= 360.0f;
        this.pitch %= 360.0f;
        while (this.yaw <= -180.0f) {
            this.yaw += 360.0f;
        }
        while (this.pitch <= -180.0f) {
            this.pitch += 360.0f;
        }
        while (this.yaw > 180.0f) {
            this.yaw -= 360.0f;
        }
        while (this.pitch > 180.0f) {
            this.pitch -= 360.0f;
        }
        return this;
    }
}
