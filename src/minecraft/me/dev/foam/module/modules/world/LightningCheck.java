/*
Author:SuMuGod
Date:2022/7/10 5:13
Project:foam Reborn
*/
package me.dev.foam.module.modules.world;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketRecieve;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.normal.Helper;
import net.minecraft.network.play.server.S29PacketSoundEffect;

public class LightningCheck extends Module {
    public LightningCheck() {
        super("LightningCheck", "雷电以检之", new String[]{}, ModuleType.World);
    }

    @NMSL
    public void onPacket(EventPacketRecieve e) {
        if ((e.getPacket() instanceof S29PacketSoundEffect) && "ambient.weather.thunder".equals(((S29PacketSoundEffect) e.getPacket()).getSoundName())) {
            Helper.sendMessage("> Lightning on:" + ((S29PacketSoundEffect) e.getPacket()).getX() + " " + ((S29PacketSoundEffect) e.getPacket()).getY() + " " + ((S29PacketSoundEffect) e.getPacket()).getZ());
        }
    }
}
