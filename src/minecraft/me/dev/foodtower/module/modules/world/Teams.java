/*
Author:SuMuGod
Date:2022/7/10 4:46
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.Client;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class Teams extends Module {

    public Teams() {
        super("Teams", "团队", new String[]{}, ModuleType.World);
    }

    public static boolean isOnSameTeam(Entity entity) {
        if (!Client.instance.getModuleManager().getModuleByClass(Teams.class).isEnabled()) return false;
        if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().startsWith("\247")) {
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().length() <= 2
                    || entity.getDisplayName().getUnformattedText().length() <= 2) {
                return false;
            }
            if (Minecraft.getMinecraft().thePlayer.getDisplayName().getUnformattedText().substring(0, 2).equals(entity.getDisplayName().getUnformattedText().substring(0, 2))) {
                return true;
            }
        }
        return false;
    }

}
