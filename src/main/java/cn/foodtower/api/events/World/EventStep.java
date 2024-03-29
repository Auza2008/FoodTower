package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

/**
 * Created by cool1 on 1/16/2017.
 */
public class EventStep extends Event {
    private final boolean pre;
    private double stepHeight;
    private double realHeight;
    private boolean active;

    public EventStep(boolean state, double stepHeight, double realHeight) {
        this.pre = state;
        this.stepHeight = stepHeight;
        this.realHeight = realHeight;
    }

    public EventStep(boolean state, double stepHeight) {
        this.pre = state;
        this.realHeight = stepHeight;
        this.stepHeight = stepHeight;
    }

    public boolean isPre() {
        return pre;
    }

    public double getStepHeight() {
        return this.stepHeight;
    }

    public void setStepHeight(double stepHeight) {
        this.stepHeight = stepHeight;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean bypass) {
        this.active = bypass;
    }

    public double getRealHeight() {
        return this.realHeight;
    }

    public void setRealHeight(double realHeight) {
        this.realHeight = realHeight;
    }

}
