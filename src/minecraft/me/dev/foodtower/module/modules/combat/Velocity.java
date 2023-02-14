/*
Author:SuMuGod
Date:2022/7/10 4:21
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.awt.*;

public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", "mode", NoKBMode.values(), NoKBMode.Cancel);
    private final Numbers<Double> percentage = new Numbers<Double>("Percentage", "percentage", 0.0, 0.0, 100.0, 5.0);

    public Velocity() {
        super("Velocity", "无击却", new String[]{"antivelocity", "antiknockback", "antikb"}, ModuleType.Combat);
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "cancel":
                    e.setCancelled(true);
                    break;
                case "simple":
                    if (this.percentage.getValue() == 0.0) {
                        e.setCancelled(true);
                    } else {
                        S12PacketEntityVelocity packet = (S12PacketEntityVelocity) e.getPacket();
                        packet.motionX = (int) (this.percentage.getValue() / 100.0);
                        packet.motionY = (int) (this.percentage.getValue() / 100.0);
                        packet.motionZ = (int) (this.percentage.getValue() / 100.0);
                    }
                    break;
            }
        }
    }

    @NMSL
    private void onUpdate(EventPreUpdate eventPreUpdate) {
        setSuffix(percentage.getValue() + "% | " + percentage.getValue() + "%");
    }

    enum NoKBMode {
        Cancel, Simple
    }
}

