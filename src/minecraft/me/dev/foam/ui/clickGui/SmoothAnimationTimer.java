/*
Author:SuMuGod
Date:2022/7/10 5:34
Project:foam Reborn
*/
package me.dev.foam.ui.clickGui;

public class SmoothAnimationTimer {
    public float target;


    public float speed = 0.3f;

    public SmoothAnimationTimer(float target) {
        this.target = target;
    }

    public SmoothAnimationTimer(float target, float speed) {
        this.target = target;
        this.speed = speed;
    }

    private float value = 0;

    public boolean update(boolean increment) {
        this.value = AnimationUtils.getAnimationState(value, increment ? target : 0, (float) (Math.max(10, (Math.abs(this.value - (increment ? target : 0))) * 40) * speed));
        return value == target;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTarget() {
        return target;
    }

    public void setTarget(float target) {
        this.target = target;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}

