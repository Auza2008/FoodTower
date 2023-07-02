package cn.foodtower.module.modules.move.speedmode.speed;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.combat.KillAura;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class DCJBhopSpeed extends SpeedModule {
    boolean dmgBoost = false;
    TimerUtil t = new TimerUtil();

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {
        if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
            mc.thePlayer.motionY = 0.34;
        }
    }

    @Override
    public void onMove(EventMove e) {
        if (MoveUtils.isMoving()) {
            if (KillAura.curTarget != null) {
                if (!dmgBoost) {
                    setMotion(e, 0.55);
                } else {
                    setMotion(e, 1);
                    if (t.hasReached(1000)) {
                        dmgBoost = false;
                        t.reset();
                    }
                }
            } else {
                setMotion(e, 0.80);
            }
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
        if (e.getTypes() == EventPacket.Type.RECEIVE) {
            if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
                dmgBoost = true;
            }
        }
    }

    @Override
    public void onMotion(EventMotionUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }
}
