package cn.foodtower.module.modules.world.dis.disablers.bypass;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class AAC4LessFlagDisabler implements DisablerModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {

    }

    @Override
    public void onPacket(EventPacketReceive event) {
        Packet<?> packet = event.getPacket();
        if (packet instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook packetS08 = (S08PacketPlayerPosLook) packet;
            double x = packetS08.getX() - mc.thePlayer.posX;
            double y = packetS08.getY() - mc.thePlayer.posY;
            double z = packetS08.getZ() - mc.thePlayer.posZ;
            double diff = Math.sqrt(x * x + y * y + z * z);
            if (diff <= 8) {
                event.setCancelled(true);
                mc.getNetHandler().getNetworkManager().sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(packetS08.getX(), packetS08.getY(), packetS08.getZ(), packetS08.getYaw(), packetS08.getPitch(), true));
            }
        }
    }

    @Override
    public void onPacket(EventPacket event) {

    }

    @Override
    public void onUpdate(EventPreUpdate event) {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onWorldChange(EventWorldChanged event) {

    }

    @Override
    public void onRender2d(EventRender2D event) {

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate event) {

    }

    @Override
    public void onTick(EventTick event) {

    }

    public void onRender3d(EventRender3D event) {

    }
}
