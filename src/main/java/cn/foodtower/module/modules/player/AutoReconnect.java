package cn.foodtower.module.modules.player;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventChat;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;

import java.awt.*;

public class AutoReconnect
        extends Module {
    private float old;

    public AutoReconnect() {
        super("AutoReconnect", new String[]{"AutoReconnect", "AutoReconnect", "AutoReconnect"}, ModuleType.Player);
        this.setColor(new Color(244, 255, 149).getRGB());
    }

    @EventHandler
    private void onChat(EventChat e) {
        if (e.getMessage().contains("Flying or related.")) mc.thePlayer.sendChatMessage("/back");
    }

}

