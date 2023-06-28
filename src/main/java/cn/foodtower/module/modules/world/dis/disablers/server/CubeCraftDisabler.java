package cn.foodtower.module.modules.world.dis.disablers.server;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.network.status.client.C01PacketPing;

public class CubeCraftDisabler implements DisablerModule {
    KillAura killAura = (KillAura) ModuleManager.getModuleByClass(KillAura.class);
    EntityPlayerSP entityPlayerSP;

    @Override
    public void onDisable() {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {
        if (killAura == null) return;
        Packet p = event.getPacket();

        if (p instanceof C03PacketPlayer.C06PacketPlayerPosLook && !ModuleManager.getModuleByClass(Scaffold.class).isEnabled()) {
            event.setCancelled(true);
        }
        if (p instanceof C03PacketPlayer && !((C03PacketPlayer) p).isMoving() && !mc.thePlayer.isUsingItem()) {
            event.setCancelled(true);
        }
        if (p instanceof C00PacketKeepAlive) {
            event.setCancelled(true);
        }
        if (p instanceof C0FPacketConfirmTransaction) {
            event.setCancelled(true);
        }
        if (p instanceof C0CPacketInput) {
            event.setCancelled(true);
        }
        if (p instanceof C01PacketPing) {
            event.setCancelled(true);
        }
        if (killAura.getTarget() == null) {
            assert (p instanceof C0BPacketEntityAction);
            C0BPacketEntityAction c0B = (C0BPacketEntityAction) p;
            if (c0B.getAction().equals(C0BPacketEntityAction.Action.START_SPRINTING)) {
                if (entityPlayerSP.serverSprintState) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                    entityPlayerSP.serverSprintState = false;
                }
                event.setCancelled(true);
            }
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
