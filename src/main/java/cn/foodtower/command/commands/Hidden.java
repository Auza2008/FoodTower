package cn.foodtower.command.commands;

import cn.foodtower.Client;
import cn.foodtower.command.Command;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.util.misc.Helper;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;


public class Hidden extends Command {
    public static List<String> list = new ArrayList();

    public Hidden() {
        super("hidden", new String[]{"h", "hide"}, "", "隐藏指定模块");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendMessage("正确用法: .h <module>");
            return null;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            for (Module m : ModuleManager.getModules()) {
                if (m.wasRemoved()) {
                    m.setRemoved(false);
                }
            }
            Helper.sendMessage("清除了所有隐藏的模组");
            return null;
        }

        for (String s : args) {
            boolean found = false;
            Module m = Client.instance.getModuleManager().getAlias(s);
            if (m != null) {
                found = true;
                if (!m.wasRemoved()) {
                    m.setRemoved(true);
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " 已被"
                            + EnumChatFormatting.RED + "隐藏");
                } else {
                    m.setRemoved(false);
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " 已恢复"
                            + EnumChatFormatting.GREEN + "显示");
                }
            }
            if (!found) {
                Helper.sendMessage("模块:" + EnumChatFormatting.RED + s
                        + EnumChatFormatting.GRAY + "不存在");
            }
        }
        return null;
    }
}
