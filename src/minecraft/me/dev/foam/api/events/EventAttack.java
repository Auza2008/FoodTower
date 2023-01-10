package me.dev.foam.api.events;

import me.dev.foam.api.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event {

    private Entity entity;
    public EventAttack(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
