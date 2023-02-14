/*
Author:SuMuGod
Date:2022/7/10 4:33
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", "驰行而驰", new String[]{"run"}, ModuleType.Movement);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate event) {
        if (!mc.thePlayer.isCollidedHorizontally && mc.thePlayer.moveForward > 0) {
            if (Scaffold.sprint.getValue() || !Client.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled()) {
                mc.thePlayer.setSprinting(true);
            }
        }
    }
}
