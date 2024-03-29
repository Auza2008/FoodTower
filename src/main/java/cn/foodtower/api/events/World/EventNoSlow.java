package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;

public class EventNoSlow extends Event {
    float moveStrafe;
    float moveForward;

    public EventNoSlow(float moveStrafe, float moveForward) {
        this.moveForward = moveForward;
        this.moveStrafe = moveStrafe;
    }

    public float getMoveForward() {
        return moveForward;
    }

    public void setMoveForward(float moveForward) {
        this.moveForward = moveForward;
    }

    public float getMoveStrafe() {
        return moveStrafe;
    }

    public void setMoveStrafe(float moveStrafe) {
        this.moveStrafe = moveStrafe;
    }
}
