/*
Author:SuMuGod
Date:2022/7/10 5:07
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class NoRender
        extends Module {
    public NoRender() {
        super("NoRender", "无视图", new String[]{"noitems"}, ModuleType.Render);
        this.setColor(new Color(166, 185, 123).getRGB());
    }
}
