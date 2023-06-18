/*
 * Decompiled with CFR 0_132.
 */
package cn.foodtower.module.modules.world;

import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;

public class Timer
        extends Module {
    private float old;
    private final Numbers<Double> Speed = new Numbers<>("Speed", "Speed", 1.0,
            0.0,
            20.0, 0.01);

    public Timer() {
        super("Timer", new String[]{"Timer", "Timer", "Timer"}, ModuleType.World);
        this.setColor(new Color(244, 255, 149).getRGB());
        super.addValues(Speed);
    }

    public void onEnable() {
        mc.timer.timerSpeed = Speed.get().floatValue();
        super.onEnable();
    }

    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        super.onDisable();
    }
}

