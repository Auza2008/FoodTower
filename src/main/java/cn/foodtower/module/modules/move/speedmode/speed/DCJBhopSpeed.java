package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;

public class DCJBhopSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
    }

    @Override
    public void onMove(EventMove e) {
        if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
            e.setY(mc.thePlayer.motionY = 0.42);
        }
        if (MoveUtils.isMoving() && mc.thePlayer.hurtTime == 0) {
            setMotion(e, 0.8);
        } else if (MoveUtils.isMoving()) {
            setMotion(e, 0.4);
        } else {
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

    @Override
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
