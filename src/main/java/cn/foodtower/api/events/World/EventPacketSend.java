/*
 * Decompiled with CFR 0_132.
 */
package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;
import net.minecraft.network.Packet;

public class EventPacketSend extends Event {
    private Packet packet;

    public EventPacketSend(Packet packet) {
        this.packet = packet;
    }

    public Packet getPacket() {
        return packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }
}
