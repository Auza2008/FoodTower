package cn.foodtower.module.modules.move.flymode.fly;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

import static net.minecraft.util.MathHelper.sin;

public class NCPPacketFly implements FlyModule {
    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onMove(EventMove e) {

    }

    @Override
    public void onUpdate(EventPreUpdate e) {
        double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        double x = -sin(yaw) * Fly.ncpSpeed.get();
        double z = MathHelper.cos((float) yaw) * Fly.ncpSpeed.get();
        resetMotion(true);
        mc.timer.timerSpeed = Fly.ncpTimer.get().floatValue();
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.motionY, mc.thePlayer.motionZ + z, false));
        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + x, mc.thePlayer.motionY - 490, mc.thePlayer.motionZ + z, true));
        mc.thePlayer.posX += x;
        mc.thePlayer.posZ += z;
    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {

    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {

    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {

    }

    private void resetMotion(Boolean y) {
        mc.thePlayer.motionX = 0.0;
        mc.thePlayer.motionZ = 0.0;
        if (y) mc.thePlayer.motionY = 0.0;
    }
}
