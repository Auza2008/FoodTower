package cn.foodtower.module.modules.world.dis.disablers.bypass;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class BasicDisabler implements DisablerModule {
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {
        if (event.getPacket() instanceof C00PacketKeepAlive || event.getPacket() instanceof C0FPacketConfirmTransaction) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onPacket(EventPacketReceive event) {

    }

    @Override
    public void onPacket(EventPacket event) {

    }

    @Override
    public void onUpdate(EventPreUpdate event) {

    }

    @Override
    public void onWorldChange(EventWorldChanged event) {

    }

    @Override
    public void onRender2d(EventRender2D event) {

    }

    @Override
    public void onRender3d(EventRender3D event) {

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate event) {

    }

    @Override
    public void onTick(EventTick event) {

    }
}
