/*
Author:SuMuGod
Date:2022/7/10 5:09
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimeHelper;
import me.dev.foodtower.utils.normal.MoveUtils;
import me.dev.foodtower.value.Numbers;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

import java.awt.*;
import java.util.ArrayList;

public class AntiVoid
        extends Module {
    public AntiVoid() {
        super("AntiVoid", "反虚无", new String[]{"novoid", "antifall"}, ModuleType.World);
        this.setColor(new Color(223, 233, 233).getRGB());
    }
    private final Numbers<Double> pullbackTime = new Numbers<>("Pull Back Time","Pull BackTime",700.0,500.0,1500.0,100.0);
    public double[] lastGroundPos = new double[3];
    public static TimeHelper timer = new TimeHelper();
    public static ArrayList<C03PacketPlayer> packets = new ArrayList<>();

    @NMSL
    public void onPacket(EventPacketSend e) {
        if (!packets.isEmpty() && mc.thePlayer.ticksExisted < 100)
            packets.clear();

        if (e.getPacket() instanceof C03PacketPlayer) {
            C03PacketPlayer packet = ((C03PacketPlayer) e.getPacket());
            if (isInVoid()) {
                e.setCancelled(true);
                packets.add(packet);

                if (timer.isDelayComplete(pullbackTime.getValue())) {
                    mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(lastGroundPos[0], lastGroundPos[1] - 1, lastGroundPos[2], true));
                }
            } else {
                lastGroundPos[0] = mc.thePlayer.posX;
                lastGroundPos[1] = mc.thePlayer.posY;
                lastGroundPos[2] = mc.thePlayer.posZ;

                if (!packets.isEmpty()) {
                    for (C03PacketPlayer p : packets)
                        mc.getNetHandler().getNetworkManager().sendPacketNoEvent(p);
                    packets.clear();
                }
                timer.reset();
            }
        }
    }

    public static boolean isInVoid() {
        for (int i = 0; i <= 128; ++i) {
            if (MoveUtils.isOnGround(i)) {
                return false;
            }
        }
        return true;
    }

    @NMSL
    public void onRevPacket(EventPacketRecieve e) {
        if (e.getPacket() instanceof S08PacketPlayerPosLook && packets.size() > 1) {
            packets.clear();
        }
    }

    public static boolean isPullbacking() {
        return Client.instance.getModuleManager().getModuleByClass(AntiVoid.class).isEnabled() && !packets.isEmpty();
    }
}


