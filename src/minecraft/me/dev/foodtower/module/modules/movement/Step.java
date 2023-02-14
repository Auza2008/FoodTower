/*
Author:SuMuGod
Date:2022/7/10 4:33
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventStep;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.stats.StatList;

import java.awt.*;

public class Step extends Module {
    public static boolean stepping = false;

    public Step() {
        super("Step", "阶", new String[]{"autojump"}, ModuleType.Movement);
        this.setColor(new Color(165, 238, 65).getRGB());
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.stepHeight = 0.625F;
    }

    @NMSL
    public void onStep(EventStep event) {
        if (event.isPre() && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
            event.setStepHeight(1.0D);
        } else if (!event.isPre() && event.getRealHeight() > 0.5D && event.getStepHeight() > 0.0D && !mc.thePlayer.movementInput.jump && mc.thePlayer.isCollidedVertically) {
            stepping = true;
/*            if (event.getRealHeight() >= 0.87D) {
                double realHeight = event.getRealHeight();
                double height1 = realHeight * 0.42D;
                double height2 = realHeight * 0.75D;
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height1, mc.thePlayer.posZ, mc.thePlayer.onGround));
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + height2, mc.thePlayer.posZ, mc.thePlayer.onGround));

 */

            fakeJump();
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688698, mc.thePlayer.posZ, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.7531999805212, mc.thePlayer.posZ, false));
            mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ, true));
            }
        }

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }
}
