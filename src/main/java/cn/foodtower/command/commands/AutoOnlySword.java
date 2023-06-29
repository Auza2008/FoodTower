package cn.foodtower.command.commands;

import cn.foodtower.command.Command;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.util.misc.Helper;

public class AutoOnlySword extends Command {
    public AutoOnlySword() {
        super("AutoOnlySword", new String[]{"os"}, "", "自动匹配DCJ的onlysword");
    }

    @Override
    public String execute(String[] var1) {
        if (var1.length == 0) {
            if (ModuleManager.getModuleByClass(cn.foodtower.module.modules.world.AutoOnlySword.class).isEnabled()) {
                Helper.sendMessage("AutoOnlySword模块已经开启了.");
            } else {
                ModuleManager.getModuleByClass(cn.foodtower.module.modules.world.AutoOnlySword.class).setEnabled(true);
            }
        } else {
            Helper.sendMessage("Correct usage .os");
        }
        return null;
    }
}
