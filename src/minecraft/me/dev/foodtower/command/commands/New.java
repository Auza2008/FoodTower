/*
Author:SuMuGod
Date:2022/7/10 3:50
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.Client;
import me.dev.foodtower.command.Command;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleManager;
import me.dev.foodtower.utils.normal.Helper;

public class New
        extends Command {
    public New() {
        super("new", new String[]{"ins"}, "", "Generate a new module");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 2) {
            if (ModuleManager.getModuleByName(args[0]) != null) {
                Class<?> m = ModuleManager.getModuleByName(args[0]).getClass();
                try {
                    Module m2 = (Module) m.newInstance();
                    m2.name = args[1];
                    Client.instance.getModuleManager().registerModule(m2);
                    Helper.sendMessage(m2.name + " created!");
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Helper.sendMessage("Module " + args[0] + " not found!");
            }
        } else {
            Helper.sendMessage("invalid syntax Valid .new <Module> <New Module>");
        }
        return null;
    }
}
