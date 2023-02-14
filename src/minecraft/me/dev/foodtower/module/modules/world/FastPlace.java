/*
Author:SuMuGod
Date:2022/7/10 5:13
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class FastPlace
        extends Module {
    public FastPlace() {
        super("FastPlace", "疾放", new String[]{"fplace", "fc"}, ModuleType.World);
        this.setColor(new Color(226, 197, 78).getRGB());
    }

    @NMSL
    private void onTick(EventTick e) {
        this.mc.rightClickDelayTimer = 0;
    }
}
