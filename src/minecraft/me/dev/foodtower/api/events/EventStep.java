/*
Author:SuMuGod
Date:2022/7/10 3:34
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;

public class EventStep extends Event {
    private double stepHeight;
    private double realHeight;
    private boolean pre;

    public EventStep(boolean state, double stepHeight, double realHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = realHeight;
    }

    public EventStep(boolean state, double stepHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = this.realHeight;
    }

    public boolean isPre() {
        return this.pre;
    }

    public double getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public double getRealHeight() {
        return this.realHeight;
    }

    public void setRealHeight(double realHeight) {
        this.realHeight = realHeight;
    }
}
