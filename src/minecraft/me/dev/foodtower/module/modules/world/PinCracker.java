/*
Author:SuMuGod
Date:2022/7/10 5:14
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;

import java.awt.*;

public class PinCracker
        extends Module {
    int num;
    private TimerUtil time = new TimerUtil();
    private Numbers<Double> delay = new Numbers<Double>("Delay", "Delay", 1.0, 0.0, 20.0, 1.0);
    private Option<Boolean> login = new Option<Boolean>("/login?", "login", false);

    public PinCracker() {
        super("PinCracker", "PIN开裂", new String[]{"pincracker"}, ModuleType.World);
    }

    @NMSL
    public void onUpdate(EventPreUpdate event) {
        this.setColor(new Color(200, 200, 100).getRGB());
        if (this.login.getValue().booleanValue()) {
            if (this.time.delay((float) (this.delay.getValue() * 100.0))) {
                this.mc.thePlayer.sendChatMessage("/login " + this.numbers());
                this.time.reset();
            }
        } else if (this.time.delay((float) (this.delay.getValue() * 100.0))) {
            this.mc.thePlayer.sendChatMessage("/pin " + this.numbers());
            this.time.reset();
        }
    }

    private int numbers() {
        if (this.num <= 10000) {
            ++this.num;
        }
        return this.num;
    }

    @Override
    public void onDisable() {
        this.num = 0;
        super.onDisable();
    }
}

