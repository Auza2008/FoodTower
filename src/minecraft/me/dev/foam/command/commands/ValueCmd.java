/*
Author:SuMuGod
Date:2022/7/10 3:51
Project:foam Reborn
*/
package me.dev.foam.command.commands;

import me.dev.foam.command.Command;
import me.dev.foam.module.ModuleManager;
import me.dev.foam.utils.normal.Helper;
import me.dev.foam.value.Value;

public class ValueCmd
        extends Command {
    public ValueCmd() {
        super("value", new String[]{}, "", "Change Value");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 3) {
            if (ModuleManager.getModuleByName(args[0]) != null) {
                for (Value v : ModuleManager.getModuleByName(args[0]).getValues()) {
                    if (v.getDisplayName() == args[1]) {
                        v.setValue(args[2]);
                        Helper.sendMessage(v.getDisplayName() + " now is " + v.getValue());
                    }
                }
            } else {
                Helper.sendMessage("Module " + args[0] + " not found!");
            }
        } else {
            Helper.sendMessage("invalid syntax Valid .value <Module> <Value> <Target>");
        }
        return null;
    }
}