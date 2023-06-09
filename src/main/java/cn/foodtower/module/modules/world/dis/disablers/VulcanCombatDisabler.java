package cn.foodtower.module.modules.world.dis.disablers;

import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.world.dis.DisablerModule;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VulcanCombatDisabler implements DisablerModule {
    private final Queue<Packet<?>> packetBuffer = new ConcurrentLinkedQueue<>();
    private final MSTimer fakeLagDelay = new MSTimer();
    private int vulTickCounterUID = -25767;

    @Override
    public void onEnabled() {

    }

    @Override
    public void onPacket(EventPacketSend event) {
        if (event.getPacket() instanceof C0FPacketConfirmTransaction) {
            C0FPacketConfirmTransaction packet = (C0FPacketConfirmTransaction) event.getPacket();
            if (Math.abs((Math.abs((packet.getUid())) - Math.abs(vulTickCounterUID))) <= 4) {
                vulTickCounterUID = packet.getUid();
                packetBuffer.add(packet);
                event.setCancelled(true);
            } else if (Math.abs((Math.abs((packet.getUid())) - 25767)) <= 4) {
                vulTickCounterUID = (packet.getUid());
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
        if (fakeLagDelay.hasTimePassed(5000L) && packetBuffer.size() > 4) {
            fakeLagDelay.reset();
            while (packetBuffer.size() > 4) {
                mc.getNetHandler().getNetworkManager().sendPacketNoEvent(packetBuffer.poll());
            }
        }
    }

    @Override
    public void onTick(EventTick event) {

    }

    public void onRender3d(EventRender3D event) {

    }
}
