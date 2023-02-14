/*
Author:SuMuGod
Date:2022/7/10 5:15
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.world;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.TimerUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PingSpoof
        extends Module {
    private List<Packet> packetList = new CopyOnWriteArrayList<Packet>();
    private TimerUtil timer = new TimerUtil();

    public PingSpoof() {
        super("PingSpoof", "延迟之欺也", new String[]{"spoofping", "ping"}, ModuleType.World);
        this.setColor(new Color(117, 52, 203).getRGB());
    }

    @NMSL
    private void onPacketSend(EventPacketSend e) {
        if (e.getPacket() instanceof C00PacketKeepAlive && this.mc.thePlayer.isEntityAlive()) {
            this.packetList.add(e.getPacket());
            e.setCancelled(true);
        }
        if (this.timer.hasReached(750.0)) {
            if (!this.packetList.isEmpty()) {
                int i = 0;
                double totalPackets = MathUtils.getIncremental(Math.random() * 10.0, 1.0);
                for (Packet packet : this.packetList) {
                    if ((double) i >= totalPackets) continue;
                    ++i;
                    this.mc.getNetHandler().getNetworkManager().sendPacket(packet);
                    this.packetList.remove(packet);
                }
            }
            this.mc.getNetHandler().getNetworkManager().sendPacket(new C00PacketKeepAlive(10000));
            this.timer.reset();
        }
    }
}
