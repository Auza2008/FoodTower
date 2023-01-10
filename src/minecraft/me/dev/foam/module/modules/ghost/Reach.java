/*
Author:SuMuGod
Date:2022/7/10 4:25
Project:foam Reborn
*/
package me.dev.foam.module.modules.ghost;

import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.value.Numbers;

import java.awt.*;

public class Reach extends Module {
    public static Numbers<Double> range = new Numbers<>("Reach Value", "Reach Value", 3.3, 1.0, 6.0, 0.1);

    public Reach() {
        super("Reach", "去", new String[]{"range"}, ModuleType.Ghost);
        this.setColor(new Color(117, 134, 197).getRGB());
    }
}
