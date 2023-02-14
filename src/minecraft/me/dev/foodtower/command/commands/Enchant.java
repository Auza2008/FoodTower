/*
Author:SuMuGod
Date:2022/7/10 3:49
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.command.Command;
import me.dev.foodtower.utils.normal.Helper;
import net.minecraft.client.Minecraft;

public class Enchant
        extends Command {
    public Enchant() {
        super("Enchant", new String[]{"e"}, "", "enchanth");
    }

    @Override
    public String execute(String[] args) {
        if (args.length < 1) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/give " + Minecraft.getMinecraft().thePlayer.getName() + " diamond_sword 1 0 {ench:[{id:16,lvl:127}]}");
        } else {
            Helper.sendMessage("invalid syntax Valid .enchant");
        }
        return null;
    }
}
