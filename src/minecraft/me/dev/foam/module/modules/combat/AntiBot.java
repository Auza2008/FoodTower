/*
Author:SuMuGod
Date:2022/7/10 4:03
Project:foam Reborn
*/
package me.dev.foam.module.modules.combat;

import me.dev.foam.Client;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import net.minecraft.entity.Entity;

import java.awt.*;

public class AntiBot extends Module {
    public AntiBot() {
        super("AntiBot", "反机械者", new String[]{"nobot", "botkiller"}, ModuleType.Combat);
        this.setColor(new Color(217, 149, 251).getRGB());
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
