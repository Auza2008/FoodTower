package cn.foodtower.module.modules.move.speedmode.speed.ncp;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import net.minecraft.potion.Potion;

public class NCPLatestSpeed extends SpeedModule {
    private boolean wasSlow = false;

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.ticksExisted % 20 <= 9) {
            mc.timer.timerSpeed = 1.05f;
        } else {
            mc.timer.timerSpeed = 0.98f;
        }
        if (MoveUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                wasSlow = false;
                mc.thePlayer.jump();
                MoveUtils.strafe(0.48f);
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    MoveUtils.strafe(0.48f * (1.0f + 0.13f * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1)));
                }
            }
            MoveUtils.strafe(MoveUtils.getSpeed() * 1.007f);
            if (MoveUtils.getSpeed() < 0.277) wasSlow = true;
            if (wasSlow) MoveUtils.strafe(0.277f);
        } else {
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
            wasSlow = true;
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
        mc.thePlayer.jumpMovementFactor = 0.02f;
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
