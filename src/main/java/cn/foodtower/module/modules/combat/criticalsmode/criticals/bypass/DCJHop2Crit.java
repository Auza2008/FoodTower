package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;

public class DCJHop2Crit extends CriticalsModule {
    int attack = 0;
    int attacked;

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (attack >= 5) {
            mc.thePlayer.jump();
            attack = 0;
        } else {
            mc.thePlayer.motionY = 0.20;
        }
        ++attack;
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
