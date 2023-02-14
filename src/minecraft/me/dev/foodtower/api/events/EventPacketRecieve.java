/*
Author:SuMuGod
Date:2022/7/10 3:31
Project:foodtower Reborn
*/
package me.dev.foodtower.api.events;

import me.dev.foodtower.api.Event;
import net.minecraft.network.Packet;

public class EventPacketRecieve
        extends Event {
    private Packet packet;

    public EventPacketRecieve(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
