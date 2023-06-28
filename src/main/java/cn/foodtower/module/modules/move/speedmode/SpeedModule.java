package cn.foodtower.module.modules.move.speedmode;

import cn.foodtower.api.events.World.*;
import net.minecraft.client.Minecraft;

public abstract class SpeedModule {
    public static Minecraft mc = Minecraft.getMinecraft();
    public double movementSpeed;
    protected int stage = 0;
    protected double distance;

    public abstract void onStep(EventStep e);

    public abstract void onPre(EventPreUpdate e);

    public abstract void onMove(EventMove e);

    public abstract void onPost(EventPostUpdate e);

    public abstract void onEnabled();

    public abstract void onDisabled();

    public abstract void onPacket(EventPacket e);

    public abstract void onMotion(EventMotionUpdate e);

    public abstract void onPacketSend(EventPacketSend e);

    public void setMotion(EventMove em, double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            em.setX(0.0);
            em.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -44 : 44);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 44 : -44);
                }

                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90));
            double sin = Math.sin(Math.toRadians(yaw + 90));
            em.setX(forward * speed * cos + strafe * speed * sin);
            em.setZ(forward * speed * sin - strafe * speed * cos);
        }
    }

    protected boolean canZoom() {
        return mc.thePlayer.moving() && mc.thePlayer.onGround;
    }
}
