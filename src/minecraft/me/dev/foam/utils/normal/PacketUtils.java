/*
Author:SuMuGod
Date:2022/7/11 3:43
Project:foam Reborn
*/
package me.dev.foam.utils.normal;

import net.minecraft.network.Packet;

public class PacketUtils extends Helper {
    public static void sendPacket(Packet<?> packet) {
        if (mc.thePlayer != null) {
            mc.getNetHandler().getNetworkManager().sendPacket(packet);
        }
    }

    public static void sendPacketNoEvent(Packet packet) {
        sendPacket(packet);
    }
}
