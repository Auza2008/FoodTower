package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.entity.PlayerUtil;
import net.optifine.util.MathUtils;

public class HypixelSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (PlayerUtil.isInLiquid()) {
            return;
        }
        if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
            mc.thePlayer.jump();
        }
        float speed = 1.48f;
        if (mc.thePlayer.moveStrafing != 0f || mc.thePlayer.moveForward < 0) {
            speed -= 0.20f;
        } else {
            speed -= MathUtils.getIncremental(0.00411, 0.0465123);
        }
        if (mc.thePlayer.onGround) {
            MoveUtils.strafe(MoveUtils.getBaseMoveSpeed() * speed);
        }
    }

    @Override
    public void onMove(EventMove e) {

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
