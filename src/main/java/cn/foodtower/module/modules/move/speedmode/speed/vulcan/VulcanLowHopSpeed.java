package cn.foodtower.module.modules.move.speedmode.speed.vulcan;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.entity.EntityLivingBase;

public class VulcanLowHopSpeed extends SpeedModule {
    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        EntityLivingBase target = KillAura.curTarget;
        if (target != null && target.hurtTime > 0 && !mc.thePlayer.isInWeb) {
            if (!mc.thePlayer.isInLava()) {
                if (!mc.thePlayer.isInWater()) {
                    if (!mc.thePlayer.isOnLadder() && mc.thePlayer.ridingEntity == null && MovementUtils.isMoving()) {
                        mc.gameSettings.keyBindJump.pressed = false;
                        if (mc.thePlayer.onGround) {
                            mc.thePlayer.jump();
                            mc.thePlayer.motionY = 0.0;
                            MovementUtils.strafe(0.65f);
                            e.setY(0.42);
                        }
                    }
                }
            }
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
