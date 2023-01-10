/*
Author:SuMuGod
Date:2022/7/10 3:32
Project:foam Reborn
*/
package me.dev.foam.api.events;

import me.dev.foam.api.Event;
import net.minecraft.network.Packet;

public class EventPacketSend
        extends Event {
    private Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
