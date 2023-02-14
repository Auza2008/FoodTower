/*
Author:SuMuGod
Date:2022/7/10 3:15
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;

public class EventChat
        extends Event {
    private String message;

    public EventChat(String message) {
        this.message = message;
        this.setType((byte) 0);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
