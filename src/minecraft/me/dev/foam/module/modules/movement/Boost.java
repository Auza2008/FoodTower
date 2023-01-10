/*
Author:SuMuGod
Date:2022/7/10 4:26
Project:foam Reborn
*/
package me.dev.foam.module.modules.movement;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.math.TimerUtil;

import java.awt.*;

public class Boost
        extends Module {
    private TimerUtil timer = new TimerUtil();

    public Boost() {
        super("Boost", "冲刺", new String[]{"boost"}, ModuleType.Movement);
        this.setColor(new Color(216, 253, 100).getRGB());
    }

    @NMSL
    public void onUpdate(EventPreUpdate event) {
        this.mc.timer.timerSpeed = 3.0f;
        if (this.mc.thePlayer.ticksExisted % 15 == 0) {
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        this.mc.timer.timerSpeed = 1.0f;
    }
}


