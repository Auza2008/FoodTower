/*
Author:SuMuGod
Date:2022/7/10 5:07
Project:foam Reborn
*/
package me.dev.foam.module.modules.render;

import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;

import java.awt.*;

public class NoRender
        extends Module {
    public NoRender() {
        super("NoRender", "无视图", new String[]{"noitems"}, ModuleType.Render);
        this.setColor(new Color(166, 185, 123).getRGB());
    }
}
