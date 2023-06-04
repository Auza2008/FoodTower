package cn.foodtower.module.modules.combat.criticalsmode.criticals.aac;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import cn.foodtower.module.modules.move.Speed;
import net.minecraft.network.play.client.C03PacketPlayer;

public class AAC440NoGroundCriit extends CriticalsModule {
    int leftTicks = 0;
    boolean first = false;

    @Override
    public void onEnabled() {
        if (leftTicks > 0) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                leftTicks = 299;
            }
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (leftTicks < 299) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                leftTicks = 500;
            }
        } else {
            leftTicks = 500;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (leftTicks > 490) {
            if (e.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
                packet.onGround = false;
            }
        }
    }

    @Override
    public void onMove(EventMove e) {
        if (mc.thePlayer.fallDistance >= 2.0 || ModuleManager.getModuleByClass(Speed.class).isEnabled()) leftTicks = 0;
    }

    @Override
    public void onUpdate(EventMotionUpdate e) {
        if (leftTicks > 0) {
            leftTicks--;
        } else {
            first = true;
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                leftTicks = 299;
            }
        }
    }
}
