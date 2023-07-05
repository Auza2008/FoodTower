package cn.foodtower.module.modules.world.dis.disablers.server;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class DCJDisabler implements DisablerModule {
    @Override
    public void onDisable() {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {

    }

    @Override
    public void onPacket(EventPacketReceive event) {
        if (KillAura.curTarget == null || !ModuleManager.getModuleByClass(KillAura.class).isEnabled()) return;
        if (event.getPacket() instanceof S08PacketPlayerPosLook) {
            event.setCancelled(true);
        }
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
