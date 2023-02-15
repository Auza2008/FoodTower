/*
Author:SuMuGod
Date:2022/7/10 4:21
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

import java.awt.*;

public class Velocity extends Module {
    private final Mode mode = new Mode("Mode", "mode", NoKBMode.values(), NoKBMode.Simple);
    private final Numbers<Double> percentage = new Numbers<Double>("Percentage", "percentage", 0.0, 0.0, 100.0, 5.0);
    private boolean canVelo;
    private final Numbers<Double> ReverseStrength = new Numbers<>("ReverseStrength", "ReverseStrength", 1.0, 0.1, 1.0, 0.1);
    private TimerUtil timer = new TimerUtil();

    public Velocity() {
        super("Velocity", "无击却", new String[]{"antivelocity", "antiknockback", "antikb"}, ModuleType.Combat);
        this.setColor(new Color(191, 191, 191).getRGB());
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S12PacketEntityVelocity || e.getPacket() instanceof S27PacketExplosion) {
            switch (mode.getValue().toString().toLowerCase()) {
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
                case "reverse":
                    canVelo = true;
            }
        }
    }

    @NMSL
    private void onUpdate(EventPostUpdate e) {
        if (mode.getValue() == NoKBMode.Reverse) {
            if (!canVelo) {
                return;
            }
            if (!mc.thePlayer.onGround) {
                MoveUtils.strafe(MoveUtils.getSpeed() * ReverseStrength.getValue());
            } else if (timer.hasReached(80)) {
                canVelo = false;
            }
        }
    }

    @NMSL
    private void onUpdate(EventPreUpdate eventPreUpdate) {
        setSuffix(percentage.getValue() + "% | " + percentage.getValue() + "%");
    }

    enum NoKBMode {
        Simple, Reverse
    }
}

