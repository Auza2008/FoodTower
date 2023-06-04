package cn.foodtower.module.modules.combat.criticalsmode.criticals.ncp;

import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventMove;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.module.modules.combat.criticalsmode.CriticalsModule;

public class NCPCrit extends CriticalsModule {
    int ncpcrit = 0;

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onAttack(EventAttack e) {
        if (ncpcrit >= 3) {
            Crit(new Double[]{0.00001100134977413, 0.00000000013487744, 0.00000571003114589, 0.00000001578887744}, false);
            ncpcrit = 0;
        } else {
            ++ncpcrit;
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
