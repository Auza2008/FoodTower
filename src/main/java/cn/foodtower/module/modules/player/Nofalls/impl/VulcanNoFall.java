package cn.foodtower.module.modules.player.Nofalls.impl;

import cn.foodtower.module.modules.player.Nofalls.NofallModule;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VulcanNoFall implements NofallModule {
    private boolean nextSpoof = false;
    private boolean doSpoof = false;
    @Override
    public void onEnable() {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            if (doSpoof) {
                packet.onGround = true;
                doSpoof = false;
                packet.y = Math.round(mc.thePlayer.posY * 2) / 2d;
                mc.thePlayer.setPosition(mc.thePlayer.posX, packet.y, mc.thePlayer.posZ);
            }
        }
    }

    @Override
    public void onUpdateMotion(EventMotionUpdate e) {
        if(nextSpoof) {
            mc.thePlayer.motionY = -0.1;
            MovementUtils.strafe(0.343f);
            nextSpoof = false;
        }
        if(mc.thePlayer.fallDistance > 3.65) {
            mc.thePlayer.fallDistance = 0.0f;
            doSpoof = true;
            nextSpoof = true;
        }
    }
}
