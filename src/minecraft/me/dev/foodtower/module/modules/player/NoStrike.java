/*
Author:SuMuGod
Date:2022/7/10 4:45
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class NoStrike
        extends Module {
    public NoStrike() {
        super("NoStrike", "无推效", new String[]{"antistrike"}, ModuleType.Player);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }
}
