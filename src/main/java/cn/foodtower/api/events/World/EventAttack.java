package cn.foodtower.api.events.World;


import cn.foodtower.api.Event;
import net.minecraft.entity.Entity;

public class EventAttack extends Event {
    private final Entity entity;

    public EventAttack(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return entity;
    }
}
