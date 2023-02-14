/*
Author:SuMuGod
Date:2022/7/10 3:50
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.Client;
import me.dev.foodtower.command.Command;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.utils.normal.Helper;

public class Hide
        extends Command {
    public Hide() {
        super("h", new String[]{"remove", "hide"}, "", "Hide a module");
    }

    @Override
    public String execute(String[] args) {
        if (args.length >= 1) {
            Module m = Client.instance.getModuleManager().getAlias(args[0]);
            if (m != null) {
                m.setRemoved(!m.wasRemoved());
            } else {
                Helper.sendMessage("> Invalid module name, double check spelling.");
            }
        } else {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .hide <module>");
        }
        return null;
    }
}
