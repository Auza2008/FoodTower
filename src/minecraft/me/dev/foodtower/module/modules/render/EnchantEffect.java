/*
Author:SuMuGod
Date:2022/7/10 5:03
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;

import java.awt.*;

public class EnchantEffect extends Module {
    public EnchantEffect() {
        super("EnchantEffect", "魔咒", new String[]{"ee"}, ModuleType.Render);
        this.setColor(new Color(196, 196, 196).getRGB());
    }
}
