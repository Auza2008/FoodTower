package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.manager.FriendManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class MCF extends Module {
    private boolean down;

    public MCF() {
        super("MCF", new String[]{"middleclickfriends", "middleclick"}, ModuleType.World);
        this.setColor(new Color(241, 175, 67).getRGB());
    }

    @EventHandler
    private void onClick(EventPreUpdate e) {
        if (Mouse.isButtonDown(2) && !this.down) {
            if (mc.objectMouseOver.entityHit != null && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) mc.objectMouseOver.entityHit;
                String playername = player.getName();
                if (!FriendManager.isFriend(playername)) {
                    mc.thePlayer.sendChatMessage(".f add " + playername);
                } else {
                    mc.thePlayer.sendChatMessage(".f del " + playername);
                }
            }
            this.down = true;
        }
        if (!Mouse.isButtonDown(2)) {
            this.down = false;
        }
    }
}
