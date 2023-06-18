package cn.foodtower.module.modules.player.Nofalls.impl;

import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPacketSend;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.module.modules.player.Nofalls.NofallModule;

public class SpoofGroundNoFall implements NofallModule {
    @Override
    public void onEnable() {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        if (mc.thePlayer.fallDistance > 2.5) {
            e.setOnground(true);
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onUpdateMotion(EventMotionUpdate e) {

    }
}
