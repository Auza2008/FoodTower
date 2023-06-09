package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.entity.MovementUtils;

public class DCJHopSpeed extends SpeedModule {
    private double mspeed = 0.0;
    private boolean justJumped = false;

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onPost(EventPostUpdate e) {

    }

    @Override
    public void onEnabled() {
        mc.timer.timerSpeed = 1.0865f;
    }

    @Override
    public void onDisabled() {
        mc.thePlayer.speedInAir = 0.02f;
        mc.timer.timerSpeed = 1f;
    }

    @Override
    public void onPacket(EventPacket e) {

    }

    @Override
    public void onMotion(EventMotionUpdate e) {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
                mspeed = MoveUtils.defaultSpeed() * 1.73;
                justJumped = true;
            } else {
                if (justJumped) {
                    mspeed *= 0.72150289018;
                    justJumped = false;
                } else {
                    mspeed -= mspeed / 159;
                }
            }
            if (mspeed < MoveUtils.defaultSpeed()) mspeed = MoveUtils.defaultSpeed();
            MoveUtils.strafe(mspeed);
        } else {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
