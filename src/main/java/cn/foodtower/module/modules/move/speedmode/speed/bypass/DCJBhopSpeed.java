package cn.foodtower.module.modules.move.speedmode.speed.bypass;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.speedmode.SpeedModule;
import net.minecraft.network.play.client.C03PacketPlayer;

import static java.lang.Math.cos;
import static net.minecraft.util.MathHelper.sin;

public class DCJBhopSpeed extends SpeedModule {

    @Override
    public void onStep(EventStep e) {

    }

    @Override
    public void onPre(EventPreUpdate e) {

    }

    @Override
    public void onMove(EventMove e) {
        if (!mc.thePlayer.onGround) return;
        double s = 5.0;
        double d = 0.2873;
        double yaw = Math.toRadians(this.movingYaw());
        double mx = -sin(yaw) * 0.2873;
        double mz = cos(yaw) * 0.2873;
        while (d <= s) {
            if (d > s) {
                mx = -sin(yaw) * (d - s);
                mz = cos(yaw) * (d - s);
                d = s;
            }
            mc.thePlayer.setPosition(mc.thePlayer.posX + mx, mc.thePlayer.posY, mc.thePlayer.posZ + mz);
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + mx, mc.thePlayer.posY, mc.thePlayer.posZ + mz, mc.thePlayer.onGround));
            d += 0.2873;
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

    double direction() {
        float rotationYaw = mc.thePlayer.rotationYaw;
        if (mc.thePlayer.movementInput.moveForward < 0f) rotationYaw += 180f;
        float forward = 1f;
        if (mc.thePlayer.movementInput.moveForward < 0f) forward = -0.5f;
        else if (mc.thePlayer.movementInput.moveForward > 0f) forward = 0.5f;
        if (mc.thePlayer.movementInput.moveStrafe > 0f) rotationYaw -= 90f * forward;
        if (mc.thePlayer.movementInput.moveStrafe < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }

    float movingYaw() {
        return (float) (this.direction() * 180f / Math.PI);
    }
}
