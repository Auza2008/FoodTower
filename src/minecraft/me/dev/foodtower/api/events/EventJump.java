/*
Author:SuMuGod
Date:2022/7/10 3:20
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;

public class EventJump extends Event {

    private double height;

    public EventJump(double height) {
        this.height = height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }
}
