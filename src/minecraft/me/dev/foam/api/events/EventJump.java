/*
Author:SuMuGod
Date:2022/7/10 3:20
Project:foam Reborn
*/
package me.dev.foam.api.events;

import me.dev.foam.api.Event;

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
