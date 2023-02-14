/*
Author:SuMuGod
Date:2022/7/10 3:48
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.Client;
import me.dev.foodtower.command.Command;
import me.dev.foodtower.other.Config;
import me.dev.foodtower.utils.normal.Helper;

import java.util.Objects;

public class ConfigCmd extends Command {

    public ConfigCmd() {
        super("config", new String[]{"cfg", "loadconfig", "preset"}, "config", "load a cfg");
    }

    @Override
    public String execute(final String[] args) {
        if (args.length >= 1) {
            if (Objects.equals(args[0], "load")) {
                if (args.length == 2) {
                    for (Config c : Client.instance.getConfigManager().configs) {
                        if (c.getName().equals(args[1])) {
                            c.readConfig();
                            Helper.sendMessage("loaded " + c.getName() + ".");
                            return null;
                        }
                        Helper.sendMessage("Config not found!");
                    }
                } else {
                    Helper.sendMessage("> Invalid syntax Valid .config load <config>");
                }
            } else if (Objects.equals(args[0], "save")) {
                if (args.length == 2) {
                    Config c = new Config(args[1]);
                    c.saveSetting();
                    Helper.sendMessage("saved " + c.getName() + ".");
                } else {
                    Client.instance.configInUse.saveSetting();
                    Helper.sendMessage("Config using saved!");
                    return null;
                }
            } else if (Objects.equals(args[0], "del")) {
                if (args.length == 2) {
                    for (Config c : Client.instance.getConfigManager().configs) {
                        if (c.getName().equals(args[1])) {
                            c.delete();
                            Helper.sendMessage("deleted " + c.getName() + ".");
                            return null;
                        }
                        Helper.sendMessage("Config not found!");
                    }
                } else {
                    Helper.sendMessage("> Invalid syntax Valid .config del <config>");
                }
            }
        } else {
            Helper.sendMessage("> Invalid syntax Valid .config <save|load|del> <name>");
        }
        return null;
    }
}

