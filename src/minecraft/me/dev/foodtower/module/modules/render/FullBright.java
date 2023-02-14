/*
Author:SuMuGod
Date:2022/7/10 5:05
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.module.Module;

import java.awt.*;

public class FullBright
        extends Module {
    private float old;

    public FullBright() {
        super("FullBright", "高亮", new String[]{"fbright", "brightness", "bright"}, ModuleType.Render);
        this.setColor(new Color(244, 255, 149).getRGB());
    }

    @Override
    public void onEnable() {
        this.old = this.mc.gameSettings.gammaSetting;
    }

    @NMSL
    private void onTick(EventTick e) {
        this.mc.gameSettings.gammaSetting = 1.5999999E7f;
    }

    @Override
    public void onDisable() {
        this.mc.gameSettings.gammaSetting = this.old;
    }
}
