package cn.foodtower.api.events.Misc;

import cn.foodtower.api.Event;

//EntityPlayerSP
public class EventPushBlock extends Event {
    boolean isPre;

    public void fire(boolean pre) {
        this.isPre = pre;
    }

    public boolean isPre() {
        return isPre;
    }

}
