/*
Author:SuMuGod
Date:2022/7/10 5:13
Project:foam Reborn
*/
package me.dev.foam.module.modules.world;

import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.normal.Helper;
import net.minecraft.client.network.NetworkPlayerInfo;

public class AutoReport extends Module {
    public AutoReport() {
        super("AutoReport", "自动举办", new String[]{"ar"}, ModuleType.World);
    }

    @Override
    public void onEnable() {
        new Thread(() -> {
            for (NetworkPlayerInfo player : mc.getNetHandler().getPlayerInfoMap()) {

                try {
                    Helper.sendMessage("> Reporting " + player.getGameProfile().getName());
                    Thread.sleep(1000);
                    mc.thePlayer.sendChatMessage("/wdr " + player.getGameProfile().getName() + " ka ac speed scaffold");
                    Helper.sendMessage("> Reported " + player.getGameProfile().getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        setEnabled(false);
    }
}
