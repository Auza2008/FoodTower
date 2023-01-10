/*
Author:SuMuGod
Date:2022/7/10 3:28
Project:foam Reborn
*/
package me.dev.foam.api.events;

import me.dev.foam.api.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class EventRenderCape
        extends Event {
    private final EntityPlayer player;
    private ResourceLocation capeLocation;

    public EventRenderCape(ResourceLocation capeLocation, EntityPlayer player) {
        this.capeLocation = capeLocation;
        this.player = player;
    }

    public ResourceLocation getLocation() {
        return this.capeLocation;
    }

    public void setLocation(ResourceLocation location) {
        this.capeLocation = location;
    }

    public EntityPlayer getPlayer() {
        return this.player;
    }
}
