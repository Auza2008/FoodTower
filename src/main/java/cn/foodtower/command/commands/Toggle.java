package cn.foodtower.command.commands;

import cn.foodtower.Client;
import cn.foodtower.command.Command;
import cn.foodtower.module.Module;
import cn.foodtower.util.misc.Helper;
import net.minecraft.util.EnumChatFormatting;

public class Toggle extends Command {
    public Toggle() {
        super("t", new String[]{"toggle", "togl", "turnon", "enable"}, "", "切换指定模块的开关");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 0) {
            Helper.sendMessage("Correct usage .t <module>");
            return null;
        }
        for (String s : args) {
            boolean found = false;
            Module m = Client.instance.getModuleManager().getAlias(s);
            if (m != null) {
                m.setEnabled(!m.isEnabled());
                found = true;
                if (m.isEnabled()) {
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " was"
                            + EnumChatFormatting.GREEN + " enabled");
                } else {
                    Helper.sendMessage(m.getName() + EnumChatFormatting.GRAY + " was"
                            + EnumChatFormatting.RED + " disabled");
                }
            }
            if (!found) {
                Helper.sendMessage("Module name " + EnumChatFormatting.RED + s
                        + EnumChatFormatting.GRAY + " is invalid");
            }

        }
        return null;
    }
}
