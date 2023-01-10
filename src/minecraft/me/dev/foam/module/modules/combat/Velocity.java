/*
Author:SuMuGod
Date:2022/7/10 4:21
Project:foam Reborn
*/
package me.dev.foam.module.modules.combat;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketRecieve;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.value.Numbers;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.awt.*;

public class Velocity
        extends Module {
    private Numbers<Double> percentage = new Numbers<Double>("Percentage", "percentage", 0.0, 0.0, 100.0, 5.0);

    public Velocity() {
        super("Velocity", "无击却", new String[]{"antivelocity", "antiknockback", "antikb"}, ModuleType.Combat);
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
            if (this.percentage.getValue().equals(0.0)) {
                e.setCancelled(true);
            } else {
                S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                packet.motionX = (int) (this.percentage.getValue() / 100.0);
                packet.motionY = (int) (this.percentage.getValue() / 100.0);
                packet.motionZ = (int) (this.percentage.getValue() / 100.0);
            }
        }
    }
}

