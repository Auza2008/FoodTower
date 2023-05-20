package cn.foodtower.module.modules.move.flymode.fly;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.util.entity.MovementUtils;
import net.minecraft.network.play.client.C03PacketPlayer;

public class HypixelZoomFly implements FlyModule {
    public float vanillaSpeed = Fly.speed.getValue().floatValue();

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

    }

    @Override
    public void onMotionUpdate(EventMotionUpdate e) {
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY += vanillaSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= vanillaSpeed;
        MovementUtils.strafe(vanillaSpeed);
    }

    @Override
    public void onPostUpdate(EventPostUpdate e) {

    }

    @Override
    public void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = (C03PacketPlayer) e.getPacket();
            packet.onGround = true;
        }
    }

    @Override
    public void onPacketReceive(EventPacketReceive e) {

    }

    @Override
    public void onStep(EventStep e) {

    }
}
