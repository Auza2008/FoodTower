package cn.foodtower.module.modules.move.speedmode.speed.normal;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.PlayerUtil;


public class GudHopSpeed extends SpeedModule {
    @Override
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        if (mc.thePlayer.onGround && PlayerUtil.MovementInput() && !mc.thePlayer.isInWater()) {
            e.setY(mc.thePlayer.motionY = 0.42);
        }
        if (PlayerUtil.MovementInput() && !mc.thePlayer.isInWater()) {
            setMotion(e, 0.8);
        } else if (!PlayerUtil.MovementInput()) {
            setMotion(e, 0);
        }
    }

    @Override
    public void onPost(EventPostUpdate e) {

    }

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisabled() {

    }

    @Override
    public void onPacket(EventPacket e) {

    }
}
