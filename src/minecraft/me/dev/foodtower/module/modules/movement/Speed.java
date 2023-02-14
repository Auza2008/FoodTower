/*
Author:SuMuGod
Date:2022/7/10 4:32
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.potion.Potion;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Speed extends Module {
    public Mode mode = new Mode("Mode", "mode", SpeedMode.values(), SpeedMode.Hypixel);
    private final Numbers<Double> HypixelJumpHight = new Numbers<>("HypixelJumpHight", "HypixelJumpHight", 0.42, 0.0, 1.0, 0.01);
    private final Numbers<Double> HypixelBoost = new Numbers<>("HypixelBoostSpeed", "HypixelBoostSpeed", 0.1, 0.0, 0.4, 0.1);

    public Speed() {
        super("Speed", "急行以驰", new String[]{"zoom"}, ModuleType.Movement);
        this.setColor(new Color(99, 248, 91).getRGB());
        setKey(Keyboard.KEY_V);
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
        } else if (mode.getValue() == SpeedMode.Jump) {
            if (mc.thePlayer.onGround && MoveUtils.isMoving()) {
                mc.thePlayer.jump();
            }
        }
    }

    enum SpeedMode {
        Hypixel, Jump
    }
}

