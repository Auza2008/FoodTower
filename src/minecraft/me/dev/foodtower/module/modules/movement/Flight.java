/*
Author:SuMuGod
Date:2022/7/10 4:27
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.normal.DamageUtils;
import me.dev.foodtower.utils.normal.MoveUtils;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class Flight
        extends Module {
    public Flight() {
        super("Flight", "飞成行", new String[]{"fly", "angel"}, ModuleType.Movement);
        this.setColor(new Color(158, 114, 243).getRGB());
        setKey(Keyboard.KEY_F);
    }

    int ticks, stages;

    @NMSL
    public void onUpdate(EventPreUpdate e) {
        ticks++;
        if (stages == 2 || ticks >= 11) {
            mc.timer.timerSpeed = 1;

            mc.thePlayer.motionY = 0;
            mc.thePlayer.lastReportedPosY = 0;
            mc.thePlayer.jumpMovementFactor = 0;

            e.setOnground(true);
        }

        if (stages == 0) {
            if (ticks == 1) {
                DamageUtils.hypixelDamage();
                mc.thePlayer.motionY = 0.2469883648888012144;
            } else if (ticks == 12) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.38, mc.thePlayer.posZ);
            } else if (ticks == 13) {
                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.46, mc.thePlayer.posZ);
            } else if (ticks == 14) {
                mc.thePlayer.motionY = -0.52;
                stages = 2;
            }
        }
    }

    @NMSL
    public void onMove(EventMove e) {
        MoveUtils.setMotion(e, ticks > 12 ? MoveUtils.defaultSpeed() : 0);
    }

    @Override
    public void onEnable() {
        ticks = stages = 0;
        mc.timer.timerSpeed = 100;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1;
        super.onDisable();
    }
}

