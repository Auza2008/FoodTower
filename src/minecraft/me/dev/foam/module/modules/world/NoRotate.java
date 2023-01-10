/*
Author:SuMuGod
Date:2022/7/10 5:13
Project:foam Reborn
*/
package me.dev.foam.module.modules.world;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketSend;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.awt.*;

public class NoRotate
        extends Module {
    public NoRotate() {
        super("NoRotate", "无转头", new String[]{"rotate"}, ModuleType.World);
        this.setColor(new Color(17, 250, 154).getRGB());
    }

    @NMSL
    private void onPacket(EventPacketSend e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook) {
            S08PacketPlayerPosLook look = (S08PacketPlayerPosLook) e.getPacket();
            look.yaw = this.mc.thePlayer.rotationYaw;
            look.pitch = this.mc.thePlayer.rotationPitch;
        }
    }
}
