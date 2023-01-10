/*
Author:SuMuGod
Date:2022/7/10 4:08
Project:foam Reborn
*/
package me.dev.foam.module.modules.combat;

import me.dev.foam.Client;
import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventAttack;
import me.dev.foam.api.events.EventPacketSend;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.module.modules.movement.Speed;
import me.dev.foam.utils.math.TimerUtil;
import me.dev.foam.utils.normal.Helper;
import me.dev.foam.value.Mode;
import me.dev.foam.value.Numbers;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class Criticals
        extends Module {
    private final Mode mode = new Mode("Mode", "mode", CritMode.values(), CritMode.Packet);
    private final Numbers<Double> delay = new Numbers<>("Delay", "delay", 0.0, 0.0, 500.0, 10.0);

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
        if (timer.hasReached(delay.getValue()) && mc.thePlayer.onGround && !mc.thePlayer.isInWater() && !Client.instance.getModuleManager().getModuleByClass(Speed.class).isEnabled() && Killaura.target != null) {
            return true;
        }
        return false;
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
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0113444, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0112779, z, false));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.0152398, z, false));
                    break;
                case "hypixel":
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + 0.11, z, true));
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y, z, false));
                    break;
                case "jumps":
                    if (mc.thePlayer.onGround)
                        mc.thePlayer.jump();
                    break;
            }
            timer.reset();
        }
    }
    enum CritMode {
        Packet,
        Hypixel,
        jumps;
    }
}

