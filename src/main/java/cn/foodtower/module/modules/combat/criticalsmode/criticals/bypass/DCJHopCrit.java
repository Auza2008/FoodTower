package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.Criticals;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;

public class DCJHopCrit extends CriticalsModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (!Criticals.dcj) {
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.22,mc.thePlayer.posZ);
            Criticals.dcj = true;
        } else {
            mc.thePlayer.motionY = 0.10475829;
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
