/*
Author:SuMuGod
Date:2022/7/10 4:08
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventAttack;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Speed;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Criticals extends Module {
    private final Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Packet);
    private final Numbers<Double> delay = new Numbers<>("Delay", "delay", 0.0, 0.0, 500.0, 10.0);
    private Option<Boolean> alway = new Option<>("Alway", "alway", false);

    private final TimerUtil timer = new TimerUtil();

    public Criticals() {
        super("Criticals", "重击", new String[]{"crits", "crit"}, ModuleType.Combat);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate e) {
        this.setSuffix(this.mode.getValue());
    }

    private boolean canCrit() {
        return timer.hasReached(delay.getValue()) && mc.thePlayer.onGround && !mc.thePlayer.isInWater() && !Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled() && Killaura.target != null || alway.getValue();
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    @NMSL
    private void onAttack(EventAttack event) {
        double x = mc.thePlayer.posX;
        double y = mc.thePlayer.posY;
        double z = mc.thePlayer.posZ;

        if (canCrit()) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "packet":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0183444, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0153444, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0173779, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0182398, z, false));
                    break;
                case "hypixel":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    break;
                case "hvh":
                    mc.thePlayer.setPosition(x, y + 0.1, z);
                    mc.thePlayer.onGround = true;
                    break;
                case "hop":
                    mc.thePlayer.motionY = 0.1;
                    mc.thePlayer.fallDistance = 0.1f;
                    mc.thePlayer.onGround = false;
                    break;
                case "jumps":
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    break;
                case "noground":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.05, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.06, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    break;
            }
            timer.reset();
        }
    }

    enum CritMode {
        Packet,
        NoGround,
        Hypixel,
        HvH,
        Hop,
        Jumps
    }
}

