package cn.foodtower.module.modules.combat.criticalsmode.criticals.aac;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;

public class AAC5Crit extends CriticalsModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        mc.thePlayer.motionY = 0.104514886;
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
