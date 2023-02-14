/*
Author:SuMuGod
Date:2022/7/10 4:46
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.entity.player.EnumPlayerModelParts;

import java.awt.*;

public class SkinFlash
        extends Module {
    public SkinFlash() {
        super("SkinFlash", "身行皮耀体", new String[]{"derpskin"}, ModuleType.Player);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }

    @Override
    public void onDisable() {
        EnumPlayerModelParts[] parts;
        if (this.mc.thePlayer != null && (parts = EnumPlayerModelParts.values()) != null) {
            EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1 = parts;
            int j = arrayOfEnumPlayerModelParts1.length;
            int i = 0;
            while (i < j) {
                EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                this.mc.gameSettings.setModelPartEnabled(part, true);
                ++i;
            }
        }
    }

    @NMSL
    private void onTick(EventTick e) {
        EnumPlayerModelParts[] parts;
        if (this.mc.thePlayer != null && (parts = EnumPlayerModelParts.values()) != null) {
            EnumPlayerModelParts[] arrayOfEnumPlayerModelParts1 = parts;
            int j = arrayOfEnumPlayerModelParts1.length;
            int i = 0;
            while (i < j) {
                EnumPlayerModelParts part = arrayOfEnumPlayerModelParts1[i];
                boolean newState = this.isEnabled() ? random.nextBoolean() : true;
                this.mc.gameSettings.setModelPartEnabled(part, newState);
                ++i;
            }
        }
    }
}
