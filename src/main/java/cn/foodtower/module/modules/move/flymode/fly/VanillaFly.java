package cn.foodtower.module.modules.move.flymode.fly;

import cn.foodtower.api.events.World.*;
import cn.foodtower.module.modules.move.Fly;
import cn.foodtower.module.modules.move.flymode.FlyModule;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.AxisAlignedBB;

public class VanillaFly implements FlyModule {
    private final MSTimer groundTimer = new MSTimer();

    @Override
    public void onEnabled() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onMove(EventMove e) {
        double vanillaSpeed = Fly.speed.get();
        mc.thePlayer.capabilities.isFlying = false;
        mc.thePlayer.motionY = 0;
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
        e.setY(mc.gameSettings.keyBindJump.isKeyDown() ? vanillaSpeed : mc.gameSettings.keyBindSneak.isKeyDown() ? -vanillaSpeed : 0);
        if (mc.gameSettings.keyBindJump.isKeyDown())
            mc.thePlayer.motionY += vanillaSpeed;
        if (mc.gameSettings.keyBindSneak.isKeyDown())
            mc.thePlayer.motionY -= vanillaSpeed;
        MoveUtils.strafe(vanillaSpeed);
        handleVanillaKickBypass();
    }

    @Override
    public void onUpdate(EventPreUpdate e) {

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

    private void handleVanillaKickBypass() {
        if (!Fly.vanillaFlyAntiKick.get() || !groundTimer.hasTimePassed(1000)) return;

        final double ground = calculateGround();

        for (double posY = mc.thePlayer.posY; posY > ground; posY -= 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY - 8D < ground) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, ground, mc.thePlayer.posZ, true));


        for (double posY = ground; posY < mc.thePlayer.posY; posY += 8D) {
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, posY, mc.thePlayer.posZ, true));

            if (posY + 8D > mc.thePlayer.posY) break; // Prevent next step
        }

        mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));

        groundTimer.reset();
    }

    private double calculateGround() {
        final AxisAlignedBB playerBoundingBox = mc.thePlayer.getEntityBoundingBox();
        double blockHeight = 1D;

        for (double ground = mc.thePlayer.posY; ground > 0D; ground -= blockHeight) {
            final AxisAlignedBB customBox = new AxisAlignedBB(playerBoundingBox.maxX, ground + blockHeight, playerBoundingBox.maxZ, playerBoundingBox.minX, ground, playerBoundingBox.minZ);

            if (mc.theWorld.checkBlockCollision(customBox)) {
                if (blockHeight <= 0.05D)
                    return ground + blockHeight;

                ground += blockHeight;
                blockHeight = 0.05D;
            }
        }

        return 0F;
    }

}
