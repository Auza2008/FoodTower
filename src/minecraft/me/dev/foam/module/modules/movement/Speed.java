/*
Author:SuMuGod
Date:2022/7/10 4:32
Project:foam Reborn
*/
package me.dev.foam.module.modules.movement;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.normal.MoveUtils;
import me.dev.foam.value.Mode;
import me.dev.foam.value.Numbers;
import net.minecraft.potion.Potion;

import java.awt.*;

public class Speed extends Module {
    public Mode mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.Hypixel);
    private final Numbers<Double> HypixelJumpHight = new Numbers<>("HypixelJumpHight", "HypixelJumpHight", 0.42, 0.0, 1.0, 0.01);
    private final Numbers<Double> HypixelBoost = new Numbers<>("HypixelBoostSpeed", "HypixelBoostSpeed", 0.1, 0.0, 0.4, 0.1);

    public Speed() {
        super("Speed", "急行以驰", new String[]{"zoom"}, ModuleType.Movement);
        this.setColor(new Color(99, 248, 91).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        if (mode.getValue() == SpeedMode.Hypixel) {
            if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                mc.thePlayer.jump();
                mc.thePlayer.motionY = HypixelJumpHight.getValue();

                double oldMotionX = mc.thePlayer.motionX;
                double oldMotionZ = mc.thePlayer.motionZ;

                MoveUtils.strafe(MoveUtils.getSpeed() * 1.01f);
                mc.thePlayer.motionX = (mc.thePlayer.motionX * 1 + oldMotionX * 2) / 3;
                mc.thePlayer.motionZ = (mc.thePlayer.motionZ * 1 + oldMotionZ * 2) / 3;

                if (MoveUtils.getSpeed() < 0.47) {
                    double watchdogMultiplier = 0.47 / (MoveUtils.getSpeed() + 0.001);
                    mc.thePlayer.motionX *= watchdogMultiplier;
                    mc.thePlayer.motionZ *= watchdogMultiplier;
                }
                if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    mc.thePlayer.motionX *= (1.0 + HypixelBoost.getValue() * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                    mc.thePlayer.motionZ *= (1.0 + HypixelBoost.getValue() * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
                }
            }
        }
    }

    enum SpeedMode {
        Hypixel
    }
}

