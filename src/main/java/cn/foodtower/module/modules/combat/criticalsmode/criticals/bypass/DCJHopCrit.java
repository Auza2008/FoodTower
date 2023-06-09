package cn.foodtower.module.modules.combat.criticalsmode.criticals.bypass;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.Criticals;
import cn.foodtower.module.modules.combat.KillAura;
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
        Criticals criticals = new Criticals();
        if (KillAura.curTarget == null) return;
        if (criticals.canCrit(KillAura.curTarget)) {
            mc.thePlayer.motionY = Criticals.motionYvalue.get();
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
