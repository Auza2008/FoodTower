/*
Author:SuMuGod
Date:2022/7/10 5:17
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Numbers;

import java.awt.*;

public class Timer extends Module {
    private final Numbers<Double> speed = new Numbers<>("Speed", "Speed", 1.3, 0.5, 10.0, 0.1);

    public Timer() {
        super("Timer", "时", new String[]{"time"}, ModuleType.World);
        this.setColor(new Color(19, 52, 47).getRGB());
    }

    @NMSL
    private void onTick(EventTick e) {
        mc.timer.timerSpeed = speed.getValue().floatValue();
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1.0f;
    }
}
