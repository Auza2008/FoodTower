package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.Criticals;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;

public class DCJSmartCrit extends CriticalsModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (mc.thePlayer.getHealth() < Criticals.smartChangeValue.get()) {
            mc.thePlayer.motionY = 0.42;
            mc.thePlayer.fallDistance = 0.42f;
            mc.thePlayer.onGround = false;
        } else {
            mc.thePlayer.motionY = 0.3425;
            mc.thePlayer.fallDistance = 0.3425f;
            mc.thePlayer.onGround = false;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventMotionUpdate e) {

    }
}
