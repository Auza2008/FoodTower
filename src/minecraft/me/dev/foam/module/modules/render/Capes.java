/*
Author:SuMuGod
Date:2022/7/10 4:59
Project:foam Reborn
*/
package me.dev.foam.module.modules.render;

import me.dev.foam.Client;
import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventRenderCape;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.other.FriendManager;

import java.awt.*;

public class Capes
        extends Module {
    public Capes() {
        super("Capes", "披风", new String[]{"kape"}, ModuleType.Render);
        this.setColor(new Color(159, 190, 192).getRGB());
        this.setEnabled(true);
        this.setRemoved(true);
    }

    @NMSL
    public void onRender(EventRenderCape event) {
        if (this.mc.theWorld != null && FriendManager.isFriend(event.getPlayer().getName())) {
            event.setLocation(Client.CLIENT_CAPE);
            event.setCancelled(true);
        }
    }
}
