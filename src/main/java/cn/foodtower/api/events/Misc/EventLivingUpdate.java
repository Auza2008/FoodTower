package cn.foodtower.api.events.Misc;



import cn.foodtower.api.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate extends Event {
    public Entity entity;

    public EventLivingUpdate(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return entity;
    }


}
