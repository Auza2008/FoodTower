/*
Author:SuMuGod
Date:2022/7/10 4:15
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class Regen
        extends Module {
    private final Option<Boolean> guardian = new Option<>("Guardian", "guardian", true);

    public Regen() {
        super("Regen", "自重生", new String[]{"fastgen"}, ModuleType.Combat);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate event) {
        if (mc.thePlayer.onGround && (double) mc.thePlayer.getHealth() < 16.0 && mc.thePlayer.getFoodStats().getFoodLevel() > 17 && mc.thePlayer.isCollidedVertically) {
            int i = 0;
            while (i < 60) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-9, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                mc.thePlayer.motionX = 0.0;
                mc.thePlayer.motionZ = 0.0;
                ++i;
            }
            if (this.guardian.getValue() && mc.thePlayer.ticksExisted % 3 == 0) {
                mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 999.0, mc.thePlayer.posZ, true));
            }
            mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
    }
}
