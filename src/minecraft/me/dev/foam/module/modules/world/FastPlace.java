/*
Author:SuMuGod
Date:2022/7/10 5:13
Project:foam Reborn
*/
package me.dev.foam.module.modules.world;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventTick;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;

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
