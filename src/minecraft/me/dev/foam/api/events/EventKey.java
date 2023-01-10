/*
Author:SuMuGod
Date:2022/7/10 3:19
Project:foam Reborn
*/
package me.dev.foam.api.events;

import me.dev.foam.api.Event;

public class EventKey
        extends Event {
    private int key;

    public EventKey(int key) {
        this.key = key;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
