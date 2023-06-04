package cn.foodtower.module.modules.render;

import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.gui.GuiBaned;

public class Baned extends Module {
    public Baned() {
        super("AntiBan", new String[]{"ban"}, ModuleType.World);
    }

    @Override
    public void onEnable() {
        if (mc.theWorld != null) {
            mc.theWorld.sendQuittingDisconnectingPacket();
            mc.loadWorld(null);
        }
        mc.displayGuiScreen(new GuiBaned());
    }
}
