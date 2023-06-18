package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public class EventMotion extends Event {
    private Type motionType;

    public EventMotion(Type type) {
        motionType = type;
    }

    public Type getTypes() {
        return motionType;
    }

    public void setTypes(Type motionType) {
        this.motionType = motionType;
    }

    public boolean isPre() {
        return motionType == Type.PRE;
    }

    public enum Type {
        PRE,
        POST
    }
}
