package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import net.minecraft.potion.Potion;

public class HypixelSpeed extends SpeedModule {
    private int groundTick;

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (MoveUtils.isMoving()) {
            mc.timer.timerSpeed = 1.07f;
            if (mc.thePlayer.onGround) {
                if (groundTick >= 0) {
                    mc.timer.timerSpeed = 1.2f;
                    MoveUtils.strafe(0.42f);
                    mc.thePlayer.motionY = MoveUtils.getJumpBoostModifier(0.41999998688698);
                    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                        MoveUtils.strafe(0.57f);
                    }
                }
                groundTick++;
            } else {
                groundTick = 0;
            }
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
