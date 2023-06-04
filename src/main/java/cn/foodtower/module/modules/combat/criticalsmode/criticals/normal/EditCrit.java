package cn.foodtower.module.modules.combat.criticalsmode.criticals.normal;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import net.minecraft.network.play.client.C03PacketPlayer;

public class EditCrit extends CriticalsModule {
    boolean readyCrit = false;

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        readyCrit = true;
    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer && readyCrit) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = false;
            readyCrit = false;
        }
    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventMotionUpdate e) {

    }
}
