/*
Author:SuMuGod
Date:2022/7/10 3:32
Project:foam Reborn
*/
package me.dev.foam.api.events;

import me.dev.foam.api.Event;

public class EventPostUpdate
        extends Event {
    private float yaw;
    private float pitch;

    public EventPostUpdate(float yaw, float pitch) {
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
}
