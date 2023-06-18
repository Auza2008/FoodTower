package cn.foodtower.module.modules.world;

import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;


public class NoCommand
        extends Module {
    public NoCommand() {
        super("NoCommand", new String[]{"No Command", "Commnand"}, ModuleType.World);
        this.setColor(new Color(223, 233, 233).getRGB());
    }
}
