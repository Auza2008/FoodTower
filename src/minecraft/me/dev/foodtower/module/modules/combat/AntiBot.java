/*
Author:SuMuGod
Date:2022/7/10 4:03
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.entity.Entity;

import java.awt.*;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "反机械者", new String[]{"nobot", "botkiller"}, ModuleType.Combat);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    public static boolean isServerBot(Entity entity) {
        if (Client.instance.getModuleManager().getModuleByClass(AntiBot.class).isEnabled()) {
            if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
                return false;
            }
            return true;
        }
        return false;
    }
}
