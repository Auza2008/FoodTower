/*
Author:SuMuGod
Date:2022/7/10 3:48
Project:foam Reborn
*/
package me.dev.foam.command.commands;

import me.dev.foam.Client;
import me.dev.foam.command.Command;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleManager;
import me.dev.foam.utils.normal.Helper;
import net.minecraft.util.EnumChatFormatting;

public class Cheats
        extends Command {
    public Cheats() {
        super("Cheats", new String[]{"mods"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Client.instance.getModuleManager();
            StringBuilder list = new StringBuilder(String.valueOf(ModuleManager.getModules().size()) + " Cheats - ");
            Client.instance.getModuleManager();
            for (Module cheat : ModuleManager.getModules()) {
                list.append((Object) (cheat.isEnabled() ? EnumChatFormatting.GREEN : EnumChatFormatting.RED)).append(cheat.getName()).append(", ");
            }
            Helper.sendMessage("> " + list.toString().substring(0, list.toString().length() - 2));
        } else {
            Helper.sendMessage("> Correct usage .cheats");
        }
        return null;
    }
}
