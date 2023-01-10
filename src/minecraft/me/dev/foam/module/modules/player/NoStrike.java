/*
Author:SuMuGod
Date:2022/7/10 4:45
Project:foam Reborn
*/
package me.dev.foam.module.modules.player;

import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;

import java.awt.*;

public class NoStrike
        extends Module {
    public NoStrike() {
        super("NoStrike", "无推效", new String[]{"antistrike"}, ModuleType.Player);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }
}
