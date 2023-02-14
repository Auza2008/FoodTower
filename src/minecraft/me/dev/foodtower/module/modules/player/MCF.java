/*
Author:SuMuGod
Date:2022/7/10 4:44
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.other.FriendManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class MCF
        extends Module {
    private boolean down;

    public MCF() {
        super("MCF", "于键友", new String[]{"middleclickfriends", "middleclick"}, ModuleType.Player);
        this.setColor(new Color(241, 175, 67).getRGB());
    }

    @NMSL
    private void onClick(EventPreUpdate e) {
        if (Mouse.isButtonDown((int) 2) && !this.down) {
            if (this.mc.objectMouseOver.entityHit != null) {
                EntityPlayer player = (EntityPlayer) this.mc.objectMouseOver.entityHit;
                String playername = player.getName();
                if (!FriendManager.isFriend(playername)) {
                    this.mc.thePlayer.sendChatMessage(".f add " + playername);
                } else {
                    this.mc.thePlayer.sendChatMessage(".f del " + playername);
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown((int) 2)) {
            this.down = false;
        }
    }
}


