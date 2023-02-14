/*
Author:SuMuGod
Date:2022/7/10 4:42
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Numbers;

import java.awt.*;

public class Bobbing
        extends Module {
    private Numbers<Double> boob = new Numbers<Double>("Amount", "Amount", 1.0, 0.1, 5.0, 0.5);

    public Bobbing() {
        super("Bobbing", "振旅动而改容", new String[]{"bobbing+"}, ModuleType.Player);
    }

    @NMSL
    public void onUpdate(EventPreUpdate event) {
        this.setColor(new Color(20, 200, 100).getRGB());
        if (this.mc.thePlayer.onGround) {
            this.mc.thePlayer.cameraYaw = (float) (0.09090908616781235 * this.boob.getValue());
        }
    }
}
