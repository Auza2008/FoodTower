/*
Author:SuMuGod
Date:2022/7/10 3:49
Project:foam Reborn
*/
package me.dev.foam.command.commands;

import me.dev.foam.command.Command;
import me.dev.foam.utils.normal.Helper;
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
