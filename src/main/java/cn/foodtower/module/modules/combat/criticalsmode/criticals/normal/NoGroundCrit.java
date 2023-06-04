package cn.foodtower.module.modules.combat.criticalsmode.criticals.normal;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoGroundCrit extends CriticalsModule {
    @Override
    public void onEnabled() {
        mc.thePlayer.jump();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = false;
        }
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventMotionUpdate e) {

    }
}
