package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Speed;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import static cn.foodtower.util.entity.PlayerUtil.setSpeed;

public class AutoJumpSpeed extends SpeedModule {

    private final MSTimer timer = new MSTimer();
    public double dmgspeed;
    public boolean shoulddamageboost = false;

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.isMoving()) {

            if (mc.thePlayer.onGround) {
                mc.thePlayer.jump();
            }

            if (shoulddamageboost && !timer.hasTimePassed(500)) {
                setSpeed(Math.max(Speed.dmspeed.getValue().doubleValue() + (double) MoveUtils.getSpeedEffect() * 0.1, this.getBaseMoveSpeed()));
                mc.timer.timerSpeed = 1.0f;
                System.out.println("damageboost");
            } else {
                shoulddamageboost = false;
            }

        }

    }

    public double getBaseMoveSpeed() {
        return mc.thePlayer.isSprinting() ? 0.2873 : (double) 0.223f;
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
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onPacket(EventPacket e) {
        if ((e.getPacket() instanceof S12PacketEntityVelocity && ((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId())) {
            shoulddamageboost = true;
            timer.reset();
        }
        if (e.getPacket() instanceof S27PacketExplosion) {


            S27PacketExplosion s27PacketExplosion = (S27PacketExplosion) e.getPacket();
            if (s27PacketExplosion.getAffectedBlockPositions().isEmpty()) {
                dmgspeed = Math.hypot(mc.thePlayer.motionX + (double) (s27PacketExplosion.func_149149_c() / 8500.0f), mc.thePlayer.motionZ + (double) (s27PacketExplosion.func_149147_e() / 8500.0f));
                shoulddamageboost = true;
                timer.reset();
            }
        }
    }
}
