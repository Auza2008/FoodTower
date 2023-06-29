package cn.foodtower.module.modules.world;

import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.misc.Helper;
import net.minecraft.item.ItemSword;

public class AutoOnlySword extends Module {
    public AutoOnlySword() {
        super("AutoOnlySword", new String[]{"os"}, ModuleType.World);
    }

    @Override
    public void onEnable() {
        if (Helper.onServer("mcga") || Helper.onServer("dcj")) {
            if (!(mc.thePlayer.inventory.getStackInSlot(1).getItem() instanceof ItemSword)) {
                Helper.sendMessage("没有检测到用来加入队列的剑.");
                return;
            } else {
                mc.thePlayer.inventory.currentItem = 1;
                mc.rightClickMouse();
            }
        } else {
            Helper.sendMessage("请在dcj服务器使用.");
        }
        this.setEnabled(false);
    }
}
