package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.potion.Potion;

public class HypixelLowHopSpeed extends SpeedModule {

    private double speed;

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.onGround) {
            if (mc.thePlayer.isMoving()) {
                mc.thePlayer.motionY = 0.319f;
                speed = 1.10f;
                mc.timer.timerSpeed = 1.08f;
            } else {
                speed = 0f;
            }
        }
        MovementUtils.strafe((float) (getBaseMoveSpeed() * speed));
    }


    public double getBaseMoveSpeed() {
        double baseSpeed = 0.2603;
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        return baseSpeed;
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
        mc.timer.timerSpeed = 1.0F;
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
