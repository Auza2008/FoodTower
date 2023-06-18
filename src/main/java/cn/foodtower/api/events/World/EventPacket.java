package cn.foodtower.api.events.World;

import cn.foodtower.api.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {
    public Packet<?> packet;
    Type type;

    public EventPacket(Packet<?> packet, Type type) {
        this.type = type;
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public boolean isServerSide() {
        return type == Type.RECEIVE;
    }

    public Type getTypes() {
        return type;
    }

    public enum Type {
        RECEIVE,
        SEND
    }
}
