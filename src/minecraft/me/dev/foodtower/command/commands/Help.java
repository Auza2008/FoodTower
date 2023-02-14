/*
Author:SuMuGod
Date:2022/7/10 3:49
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.command.Command;
import me.dev.foodtower.utils.normal.Helper;

public class Help
        extends Command {
    public Help() {
        super("Help", new String[]{"list"}, "", "sketit");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
            Helper.sendMessageWithoutPrefix("                    \u00a7b\u00a7lETB Client");
            Helper.sendMessageWithoutPrefix("\u00a7b.help >\u00a77 list commands");
            Helper.sendMessageWithoutPrefix("\u00a7b.bind >\u00a77 bind a module to a key");
            Helper.sendMessageWithoutPrefix("\u00a7b.t >\u00a77 toggle a module on/off");
            Helper.sendMessageWithoutPrefix("\u00a7b.friend >\u00a77 friend a player");
            Helper.sendMessageWithoutPrefix("\u00a7b.cheats >\u00a77 list all modules");
            Helper.sendMessageWithoutPrefix("\u00a7b.config >\u00a77 load a premade config");
            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l----------------------------------");
        } else {
            Helper.sendMessage("invalid syntax Valid .help");
        }
        return null;
    }
}

