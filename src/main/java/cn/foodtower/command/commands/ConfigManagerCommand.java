package cn.foodtower.command.commands;

import cn.foodtower.command.Command;
import cn.foodtower.manager.ConfigManager;
import cn.foodtower.util.misc.Helper;

public class ConfigManagerCommand extends Command {
    public ConfigManagerCommand() {
        super("ConfigManager", new String[]{"cm"}, "", "加载/保存本地配置(也可以用\".cm\"简化指令)");
    }

    @Override
    public String execute(String[] args) {
        if (args.length == 2 && args[0].equalsIgnoreCase("save")) {
            ConfigManager.saveConfig(args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("load")) {
            ConfigManager.loadConfig(args[1]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            ConfigManager.removeConfig(args[1]);
        }
        if (args.length != 2) {
            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l==================================");
            Helper.sendMessageWithoutPrefix("\u00a7b\u00a7lFoodTower ConfigManager");
            Helper.sendMessageWithoutPrefix("\u00a7b.cm save <配置名> :\u00a77 保存一个配置");
            Helper.sendMessageWithoutPrefix("\u00a7b.cm load <配置名> :\u00a77 加载一个配置");
            Helper.sendMessageWithoutPrefix("\u00a7b.cm remove <配置名> :\u00a77 移除一个配置");
            Helper.sendMessageWithoutPrefix("\u00a77\u00a7m\u00a7l==================================");
        }
        return null;
    }
}
