package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;
import cn.foodtower.util.math.MathUtil;

public class DCJHopCrit extends CriticalsModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        double random = MathUtil.randomNumber(0.5, 0.3425);
        mc.thePlayer.motionY = random;
        mc.thePlayer.fallDistance = (float) random;
        mc.thePlayer.onGround = false;
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
